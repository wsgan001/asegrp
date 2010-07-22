package randoop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import methodfilter.MethodFilter;

import randoop.main.GenInputsAbstract;
import randoop.util.ProgressDisplay;
import randoop.util.Reflection;

/**
 * An ExecutableSequence adds two functionalities to a Sequence:
 * <p>
 * <ul>
 * <li> The ability to execute the code that the sequence represents.
 * <li> Checks can be added to elements in the sequence.
 * </ul>
 * <p>
 * Method execute(ExecutionVisitor v) executes the code that the sequence represents.
 * This method uses reflection to execute each element in the sequence (method call,
 * constructor call, primitive or array declaration, etc).
 * <p>
 * Before executing each statement (e.g. the i-th statement), execute(v)
 * calls v.visitBefore(this, i), and after executing each statement, it calls
 * v.visitAfter(this, i). The purpose of the visitor is to examine the unfolding
 * execution, and take some action depending on its intended purpose. For example,
 * it may decorate the sequence with checks about the
 * execution. Below are some examples.
 * <p>
 * <ul>
 * <li> A ToStringVisitor calls val.toString() on each value created during
 *      execution. and adds checks indicating the result of each call. For
 *      example, consider executing the sequence
 *
 * <pre>
 *      ArrayList var0 = new ArrayList();
 *      int var1 = 3;
 *      var0.add(var1)  ;
 * </pre>
 *      After executing the sequence with a ToStringVisitor, the sequence contains
 *      the following checks:
 *     <ul>
 *     <li> checks at index 0: var0.String()=="[]"
 *     <li> checks at index 1: var0.String()=="[]", var1.toString()=="3"
 *     <li> checks at index 2: var0.String()=="[3]", var1.toString()=="3"
 *     </ul>
 * <li> A ContractCheckingVisitor v adds checks that represent contract violations.
 *      For example, when v.visitAfter(this, i) is invoked, this visitor checks
 *      (among other things) that for every Variable val, "val.equals(val)==true". If
 *      this property fails for some val, it adds a check (at index i)
 *      that records the failure.
 * </ul>
 * <p>
 * NOTES.
 * <p>
 * <ul>
 * <li> It only makes sense to call the following methods *after* executing the
 * i-th statement in a sequence:
 *    <ul>
 *        <li> isNormalExecution(i)
 *        <li> isExceptionalExecution(i)
 *        <li> getExecutionResult(i)
 *        <li> getResult(i)
 *        <li> getExecption(i)
 *    </ul>
 * </ul>
 *
 */
public class ExecutableSequence implements Serializable {

  private static final long serialVersionUID = 2337273514619824184L;

  // The underlying sequence.
  public Sequence sequence;

  // The i-th element of this list contains the checks for the i-th
  // sequence element. Invariant: sequence.size() == checks.size().
  protected List<List<Check>> checks;
  
  // Contains the fail/pass results of executing the checks in this.checks.
  // The <i,j>-th element of this list is true if during execution,
  // the <i,j>-th check passed, and false if it failed.
  protected List<List<Boolean>> checksResults;

  // Contains the runtime objects created and exceptions thrown (if any)
  // during execution of this sequence.
  // Invariant: Invariant: sequence.size() == executionResults.size().
  // Transient because it can contain arbitrary objects that may not be
  // serializable.
  protected transient /*final*/ Execution executionResults;

  // How long it took to generate this sequence in nanoseconds,
  // excluding execution time.
  // Must be directly set by the generator that creates this object
  // (no code in this class sets its value).
  // TODO doesn't this more properly belong in Sequence class?
  public long gentime = -1;

  // How long it took to execute this sequence in nanoseconds,
  // excluding generation time.
  // Must be directly set by the generator that creates this object.
  // (no code in this class sets its value).
  public long exectime = -1;

  /** Output buffer used to capture the output from the executed sequence**/
  private static ByteArrayOutputStream output_buffer
    = new ByteArrayOutputStream (1024);
  private static PrintStream ps_output_buffer = new PrintStream (output_buffer);

  // Re-initialize executionResults list.
  private void readObject(ObjectInputStream s) throws IOException,
  ClassNotFoundException {
    s.defaultReadObject();
    // customized deserialization code
    this.executionResults = new Execution(sequence);
  }

  /** Create an executable sequence that executes the given sequence. */
  public ExecutableSequence(Sequence sequence) {
    this.sequence = sequence;
    this.checks = new ArrayList<List<Check>>(sequence.size());
    this.checksResults = new ArrayList<List<Boolean>>(sequence.size());
    for (int i = 0 ; i < this.sequence.size() ; i++) {
      this.checks.add(new ArrayList<Check>(1));
      this.checksResults.add(new ArrayList<Boolean>(1));
    }
    this.executionResults = new Execution(sequence);
  }

  /**
   * Create an executable sequence directly using the given arguments.
   *
   * Don't use this constructor! (Unless you know what you're doing.)
   */
  protected ExecutableSequence(Sequence sequence,
      Execution exec, List<List<Check>> checks) {
    this.sequence = sequence;
    this.executionResults = exec;
    this.checks = checks;
  }

  /** Get the checks for the i-th element of the sequence. */
  public List<Check> getChecks(int i) {
    sequence.checkIndex(i);
    return checks.get(i);
  }

  /**
   * Adds the given check to the i-th element of the sequence. Only one
   * check of class StatementThrowsException is allowed for each index,
   * and attempting to add a second check of this type will result in an
   * IllegalArgumentException.
   * @param b 
   *
   * @throws IllegalArgumentException
   *           If the given check's class is StatementThrowsException and
   *           there is already an check of this class at the give index.
   */
  public void addCheck(int i, Check check, boolean passed) {
    sequence.checkIndex(i);

    if (check instanceof ExpectedExceptionChecker &&
        hasCheck(i, ExpectedExceptionChecker.class))
      throw new IllegalArgumentException("Sequence already has an check"
          + " of type " + ExpectedExceptionChecker.class.toString());

    this.checks.get(i).add(check);
    this.checksResults.get(i).add(passed);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    for (int i = 0 ; i < sequence.size() ; i++) {
      sequence.printStatement(b, i);
      if (executionResults.size() > i)
        b.append(executionResults.get(i).toString());
      for (Check d : getChecks(i)) {
        b.append (Globals.lineSep);
        b.append(d.toString());
      }
      b.append(Globals.lineSep);
    }
    return b.toString();
  }
  
  

  /**
   * Output this sequence as code. In addition to printing out the statements,
   * this method prints the checks.
   *
   * If for a given statement there is an check of type
   * StatementThrowsException, that check's pre-statement code is printed
   * immediately before the statement, and its post-statement code is printed
   * immediately after the statement.
   */
  public String toCodeString() {
    StringBuilder b = new StringBuilder();
    for (int i = 0 ; i < sequence.size() ; i++) {
      
      // If short format, don't print out primitive declarations
      // because primitive values will be directly added to methods
      // (e.g. "foo(3)" instead of "int x = 3 ; foo(x)".
      if (!GenInputsAbstract.long_format
          && sequence.getStatementKind(i) instanceof PrimitiveOrStringOrNullDecl) {
        continue;
      }      
            
      StringBuilder oneStatement = new StringBuilder();
      sequence.printStatement(oneStatement, i);

      // Print exception check first, if present.
      List<Check> exObs = getChecks(i, ExpectedExceptionChecker.class);
      if (!exObs.isEmpty()) {
        assert exObs.size() == 1 : toString();
        Check o = exObs.get(0);
        oneStatement.insert(0, o.toCodeStringPreStatement());
        oneStatement.append(o.toCodeStringPostStatement());
        oneStatement.append(Globals.lineSep);
      }

      // Print the rest of the checks.
      for (Check d : getChecks(i)) {
        if (d instanceof ExpectedExceptionChecker)
          continue;
        oneStatement.insert(0, d.toCodeStringPreStatement());
        oneStatement.append(d.toCodeStringPostStatement());
        oneStatement.append(Globals.lineSep);
      }
      b.append(oneStatement);
    }
    return b.toString();// + "/*" + sequence.toParseableString() + "*/";
  }
  
  /**
   * Returns the returntype of the PUT based on the last method call
   * (1) If the last call is a constructor, return the type of the returnvariable of the method call
   * (2) If the last call is a methodcall, return the receiver variable
   */
  public void getReturnType(StringBuilder sbReturnType, StringBuilder sbReturnStatement)
  {	  	  
	  int lastStmtIndex = sequence.size() - 1;
	  StatementKind sk = sequence.getStatementKind(lastStmtIndex);
	  if(sk instanceof RMethod)
	  {		
		  List<Integer> dependentVars = sequence.getDependentStatements(lastStmtIndex);			
		  if(dependentVars.size() > 0)
		  {
			  Variable var = new Variable(sequence, dependentVars.get(0));
			  sbReturnStatement.append("return " + var.getName() + ";");
			  sbReturnType.append(MethodFilter.getType(var.getType()));
			  return;
		  }
	  } else if(sk instanceof RConstructor)
	  {
		  Variable var = new Variable(sequence, lastStmtIndex);
		  sbReturnStatement.append("return " + var.getName() + ";");
		  sbReturnType.append(MethodFilter.getType(var.getType()));
		  return;
	  }
	  
	//Set the return type as void
	sbReturnType.append("void");
	sbReturnStatement.append("");
  }
  
  /**
   * Suresh: Slices the entire sequences based on the last statement
   * and captures the slice, since we are interested only in the sliced
   * statement
   * @param slidedStatements
   */
  public void sliceSequence(Set<Integer> slicedStatements)
  {
	  int size = sequence.size();
	  slicedStatements.add(new Integer(size - 1));
	  for (int i = size - 1 ; i >= 0 ; i--) {		  
			  
		//Ignore the primitive type statements
		if ((sequence.getStatementKind(i) instanceof PrimitiveOrStringOrNullDecl)) {
		  continue;
		}
		  
		//The slicedStatements keeps getting updated during the backward
		//slicing, and only related statements are chosen for further analysis
		if(slicedStatements.contains(new Integer(i)))
		{
			//the return of the current statement is used by some of the
			//statements already added to the slice
			slicedStatements.addAll(sequence.getDependentStatements(i));		  
		}
		else
		{
			if(sequence.getStatementKind(i) instanceof RMethod)
			{
				//Check for the receiver object
				List<Integer> dependentVars = sequence.getDependentStatements(i);
				if(dependentVars.size() > 0)
				{				
					if(slicedStatements.contains(dependentVars.get(0)))
					{
						slicedStatements.add(new Integer(i));
						slicedStatements.addAll(dependentVars);
					}
				}
			}
		}		
	  }
  }
  
  /**
   * Suresh: Returns all the parameters to be appended to the PUT
   * @return: number of parameters
   */
  public int getAllPUTParameters(StringBuilder sb, Set<Integer> slicedStatements)
  {	 	 
	 int size = sequence.size(); 
	 for (int i = 0 ; i < size ; i++) {
		 if(!slicedStatements.contains(new Integer(i)))
		 	 continue;
		 
		 //not a primitive type assignment statement.
	     if (!(sequence.getStatementKind(i) instanceof PrimitiveOrStringOrNullDecl)) {
	        continue;
	     }
	     
	     StringBuilder oneStatement = new StringBuilder();
	     sequence.getVariableDeclaration(oneStatement, i);
	     
	     sb.append(oneStatement.toString() + ",");  	
	 }

	 int numParameters = 0;
	 if(sb.length() > 0)
	 {
		 //remove if ending with comma
		 if(sb.charAt(sb.length() - 1) == ',')
		 {
			 sb.deleteCharAt(sb.length() - 1);
		 }
		 
		 //count the number of commas
 		 StringTokenizer st = new StringTokenizer(sb.toString(), ",");
		 numParameters = st.countTokens();		
	 }	  
	 return numParameters;  
  }
  
  /**
   * Suresh: Function that outputs PUTs instead of JUnit tests
   * Output this sequence as PUT code. In addition to printing out the statements,
   * this method prints the checks.
   *
   * If for a given statement there is an check of type
   * StatementThrowsException, that check's pre-statement code is printed
   * immediately before the statement, and its post-statement code is printed
   * immediately after the statement.
   */
  public String toPUTCodeString(Set<Integer> slicedStmts) {
    StringBuilder b = new StringBuilder();
    for (int i = 0 ; i < sequence.size() ; i++) {
      
      if(!slicedStmts.contains(new Integer(i)))
      	  continue;
    	
      // If short format, don't print out primitive declarations
      // because primitive values will be directly added to methods
      // (e.g. "foo(3)" instead of "int x = 3 ; foo(x)".
      if (sequence.getStatementKind(i) instanceof PrimitiveOrStringOrNullDecl) {
    	  //ignore the primitive declaration statements, since they are handled
    	  //by getAllPUTParameters by promoting them as parameters
    	  continue;
      }
            
      StringBuilder oneStatement = new StringBuilder();
      sequence.printStatement(oneStatement, i);

      // Print exception check first, if present.
      List<Check> exObs = getChecks(i, ExpectedExceptionChecker.class);
      if (!exObs.isEmpty()) {
        assert exObs.size() == 1 : toString();
        Check o = exObs.get(0);
        oneStatement.insert(0, o.toCodeStringPreStatement());
        oneStatement.append(o.toCodeStringPostStatement());
        oneStatement.append(Globals.lineSep);
      }

      /*// Print the rest of the checks.
      for (Check d : getChecks(i)) {
        if (d instanceof ExpectedExceptionChecker)
          continue;
        oneStatement.insert(0, d.toCodeStringPreStatement());
        oneStatement.append(d.toCodeStringPostStatement());
        oneStatement.append(Globals.lineSep);
      }*/
      b.append(oneStatement);
    }
    return b.toString();// + "/*" + sequence.toParseableString() + "*/";
  }

  /**
   * Returns true if there are any expected exception 
   * @return
   */
  public boolean hasExpectedExceptions(Set<Integer> slicedStmts)
  {	  	  
	  for (int i = 0 ; i < sequence.size() ; i++) {	      
	      if(!slicedStmts.contains(new Integer(i)))
	      	  continue;
	    	
	      if (sequence.getStatementKind(i) instanceof PrimitiveOrStringOrNullDecl) {
	    	  continue;
	      }
	            
	      // Print exception check first, if present.
	      List<Check> exObs = getChecks(i, ExpectedExceptionChecker.class);
	      if (!exObs.isEmpty()) {
	        assert exObs.size() == 1 : toString();
	        return true;
	      }      
	  } 
	  return false;
  }
  

  /**
   * Executes sequence, stopping on exceptions
   */
  public void execute (ExecutionVisitor visitor) {
    execute (visitor, true);
  }

  /**
   * Execute this sequence, invoking the given visitor as the execution
   * unfolds. After invoking this method, the client can query the outcome
   * of executing each statement via the method <code>getResult(i)</code>.
   * 
   * <ul>
   * <li> Before the sequence is executed, removes all <code>Check</code>s
   * <li> Executes each statement in the sequence. Before executing each statement
   *      calls the given visitor's <code>visitBefore</code> method. After executing
   *      each statement, calls the visitor's <code>visitAfter</code> method.
   * <li> Execution stops if one of the following conditions holds:
   *   <ul>
   *   <li> All statements in the sequences have been executed.
   *   <li> A statement's execution results in an exception and
   *        <code>stop_on_exception==true</code>.
   *   <li> After executing the i-th statement and calling the visitor's
   *        <code>visitAfter</code> method, a <code>ContractViolation</code>
   *        check is present at index i.
   *   </ul>
   * </ul>
   *
   * @param visitor can be null, in which case no visitor will be invoked
   *        during execution.
   */
  public void execute(ExecutionVisitor visitor, boolean stop_on_exception) {

    // System.out.printf ("Executing sequence %s%n", this);

    executionResults.theList.clear();
    checks.clear();
    for (int i = 0 ; i < sequence.size() ; i++) {
      executionResults.theList.add(NotExecuted.create());
      checks.add(new ArrayList<Check>(1));
    }

    for (int i = 0 ; i < this.sequence.size() ; i++) {

      if (visitor != null) {
        visitor.visitBefore(this, i);
      }

      // Find and collect the input values to i-th statement.
      List<Variable> inputs = sequence.getInputs(i);
      Object[] inputVariables = new Object[inputs.size()];

      if (!getRuntimeInputs(sequence, executionResults.theList, i, inputs, inputVariables))
        break;

      executeStatement(sequence, executionResults.theList, i, inputVariables);

      if (visitor != null) {
        visitor.visitAfter(this, i);
      }

      if (executionResults.get(i) instanceof ExceptionalExecution) {
        if (stop_on_exception)
          break;
      }

      if (hasFailure(i))
        break;
    }
  }

  protected static boolean getRuntimeInputs(Sequence s, List<ExecutionOutcome> outcome,
      int i, List<Variable> inputs, Object[] inputVariables) {
    assert s.size() == outcome.size();
    for (int j = 0 ; j < inputVariables.length ; j++) {
      int creatingStatementIdx = inputs.get(j).getDeclIndex();
      assert outcome.get(creatingStatementIdx) instanceof NormalExecution :
        outcome.get(creatingStatementIdx).getClass();
      NormalExecution ne = (NormalExecution)outcome.get(creatingStatementIdx);
      inputVariables[j] = ne.getRuntimeValue();

      // If null value and not explicity null, stop execution.
      if (inputVariables[j] == null) {

        StatementKind creatingStatement = s.getStatementKind(creatingStatementIdx);

        // If receiver position of a method, don't continue execution.
        if (j == 0) {
          StatementKind st = s.getStatementKind(i);
          if (st instanceof RMethod && (!((RMethod)st).isStatic())) {
            return false;
          }
        }

        // If null value is implicitly passed (i.e. not passed from a
        // statement like "x = null;" don't continue execution.
        if (!(creatingStatement instanceof PrimitiveOrStringOrNullDecl)) {
          return false;
        }
      }
    }
    return true;
  }

  // Execute the index-th statement in the sequence.
  // Precondition: this method has been invoked on 0..index-1.
  protected static void executeStatement(Sequence s, List<ExecutionOutcome> outcome,
      int index, Object[] inputVariables) {
    StatementKind statement = s.getStatementKind(index);

    // Capture any output  Syncronize with ProgressDisplay so that
    // we don't capture its output as well.
    synchronized (ProgressDisplay.print_synchro) {
      PrintStream orig_out = System.out;
      PrintStream orig_err = System.err;
      if (GenInputsAbstract.capture_output) {
        System.out.flush();
        System.err.flush();
        System.setOut (ps_output_buffer);
        System.setErr (ps_output_buffer);
      }
      ExecutionOutcome r = statement.execute(inputVariables, Globals.blackHole);
      assert r != null;
      if (GenInputsAbstract.capture_output) {
        System.setOut (orig_out);
        System.setErr (orig_err);
        r.set_output (output_buffer.toString());
        output_buffer.reset();
      }
      outcome.set(index, r);
    }
  }

  /**
   * This method is typically used by ExecutionVisitors.
   *
   * The result of executing the i-th element of the sequence.
   */
  public ExecutionOutcome getResult(int index) {
    sequence.checkIndex(index);
    return executionResults.get(index);
  }

  /**
   *
   * @return all the execution outcomes for this sequence.
   */
  public ExecutionOutcome[] getAllResults() {
    ExecutionOutcome[] ret = new ExecutionOutcome[executionResults.size()];
    for (int i = 0 ; i < executionResults.size() ; i++) {
      ret[i] = executionResults.get(i);
    }
    return ret;
  }

  /**
   * @return the number of elements in the sequence that were executed before
   * an execution result of type randoop.NotExecuted.
   */
  public int executedSize() {
    int count = 0;
    for ( ; count < executionResults.size(); count++) {
      if (executionResults.get(count) instanceof NotExecuted) {
        break;
      }
    }
    return count;
  }

  /**
   * This method is typically used by ExecutionVisitors.
   *
   * True if execution of the i-th statement terminated normally.
   */
  public boolean isNormalExecution(int i) {
    sequence.checkIndex(i);
    return getResult(i) instanceof NormalExecution;
  }

  public boolean isNormalExecution() {
    for (int i = 0 ; i < this.sequence.size() ; i++) {
      if (!isNormalExecution(i))
        return false;
    }
    return true;
  }

  /**
   * Returns true if there was an exception on any statement other than
   * the last statement.  Exceptions within a sequence are unexpected, because
   * Randoop only builds on sequences that don't throw exceptions.  They
   * can happen in some cases, though, when changes to the global state
   * cause an existing sequence to throw an exception early
   */
  public boolean hasUnexpectedException() {
    for (int i = 0 ; i < (this.sequence.size()-1) ; i++) {
      if (!isNormalExecution(i))
        return true;
    }
    return false;
  }

  public List<Check> getChecks(int i, Class<? extends Check> clazz) {
    sequence.checkIndex(i);
    List<Check> matchingObs = new ArrayList<Check>();
    for (Check d : checks.get(i)) {
      if (Reflection.canBeUsedAs(d.getClass(), clazz)) {
        matchingObs.add(d);
      }
    }
    return matchingObs;
  }

  public void removeChecks() {
    for (int i = 0 ; i < checks.size() ; i++) {
      checks.get(i).clear();
    }
  }
  
  


  public boolean hasCheck(Class<? extends Check> clazz) {
    for (int i = 0 ; i < sequence.size() ; i++) {
      if (hasCheck(i, clazz))
        return true;
    }
    return false;
  }

  /**
   * @return the first index at which an check of the given type occurs,
   *         or -1 if there is no check of the given type in this
   *         sequence.
   */
  public int getContractCheckIndex(Class<? extends Check> clazz) {
    for (int i = 0 ; i < sequence.size() ; i++) {
      if (hasCheck(i, clazz))
        return i;
    }
    return -1;
  }

  /** True iff this sequences has at least one check of the given type at index i. */
  public boolean hasCheck(int i, Class<? extends Check> clazz) {
    sequence.checkIndex(i);
    for (Check d : checks.get(i)) {
      if (Reflection.canBeUsedAs(d.getClass(), clazz)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasCheck(int i) {
    sequence.checkIndex(i);
    return checks.get(i).size() > 0;
  }

  public boolean hasFailure(int i) {
    for (boolean b : checksResults.get(i)) {
      if (!b) {
        return true;
      }
    }
    return false;
  }
  
  public boolean hasFailure() {
    for (int i = 0 ; i < sequence.size() ; i++) {
      if (hasFailure(i)) {
        return true;
      }
    }
    return false;
  }
  
  public List<Check> getFailures(int idx) {
    List<Check> failedContracts = new ArrayList<Check>();
    for (int i = 0 ; i < checksResults.get(idx).size() ; i++) {
      if (!checksResults.get(idx).get(i)) {
        failedContracts.add(checks.get(idx).get(i));
      }
    }
    return failedContracts;
  }

  public int getFailureIndex() {
    for (int i = 0 ; i < sequence.size() ; i++) {
      if (hasFailure(i)) {
        return i;
      }
    }
    return -1;
  }
  
  /**
   * @return The index in the sequence at which an exception of the
   * given class (or a class compatible with it) was thrown. If no such
   * exception, returns -1.
   */
  public int getExceptionIndex(Class<?> exceptionClass) {
    if (exceptionClass == null) throw new IllegalArgumentException("exceptionClass<?> cannot be null");
    for (int i = 0 ; i < this.sequence.size() ; i++)
      if ((getResult(i) instanceof ExceptionalExecution)) {
        ExceptionalExecution e = (ExceptionalExecution)getResult(i);
        if (Reflection.canBeUsedAs(e.getException().getClass(), exceptionClass))
          return i;
      }
    return -1;
  }

  /**
   * @return True if an exception of the given class (or a class compatible with
   *         it) has been thrown during this sequence's execution.
   */
  public boolean throwsException(Class<?> exceptionClass) {
    return getExceptionIndex(exceptionClass) >= 0;
  }

  /**
   *
   * @return True if an exception has been thrown during this sequence's execution.
   */
  public boolean throwsException() {
    for (int i = 0 ; i < this.sequence.size() ; i++)
      if (getResult(i) instanceof ExceptionalExecution)
        return true;
    return false;
  }

  public boolean hasNonExecutedStatements() {
    // Starting from the end of the sequence is always faster to find non-executed statements.
    for (int i = this.sequence.size() - 1 ; i >= 0 ; i--)
      if (getResult(i) instanceof NotExecuted)
        return true;
    return false;
  }

  public ExecutableSequence duplicate() {
    ExecutableSequence newSequence = new ExecutableSequence(this.sequence);
    // Add checks
    for (int i = 0 ; i < newSequence.sequence.size() ; i++) {
      newSequence.checks.get(i).addAll(getChecks(i));
    }
    return newSequence;
  }

  /**
   * Returns the index i for which this.isExceptionalExecution(i), or -1 if
   * there is no such index.
   */
  public int exceptionIndex() {
    if (!throwsException())
      throw new RuntimeException("Execution does not throw an exception");
    for (int i = 0; i < this.sequence.size(); i++) {
      if (this.getResult(i) instanceof ExceptionalExecution) {
        return i;
      }
    }
    return -1;
  }

  public static <D extends Check> List<Sequence> getSequences(List<ExecutableSequence> exec) {
    List<Sequence> result= new ArrayList<Sequence>(exec.size());
    for (ExecutableSequence execSeq : exec) {
      result.add(execSeq.sequence);
    }
    return result;
  }

  @Override
  public int hashCode() {
    return sequence.hashCode() * 3 +
    checks.hashCode() * 5;
    //results are not part of this because they contain actual runtime objects. XXX is that bogus?
  }

  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof ExecutableSequence))
      return false;
    ExecutableSequence that= (ExecutableSequence)obj;
    if (! this.sequence.equals(that.sequence))
      return false;

    if (! this.checks.equals(that.checks))
      return false;

    //results are not part of this because they contain actual runtime objects. XXX is that bogus?

    return true;
  }

  /**
   * Compares the results of the checks of two sequences.  Returns the
   * number of different checks.  If remove_diffs is true any
   * differing checks are removed from both sequences.
   * Prints any differences to stdout if print_diffs is true.
   */
  public int compare_checks (ExecutableSequence es, boolean remove_diffs,
                                   boolean print_diffs){

    int cnt = 0;

    for (int ii = 0; ii < checks.size(); ii++) {
      // System.out.printf ("Sequence1: %n%s%n", this);
      // System.out.printf ("Sequence2: %n%s%n", es);
      List<Check> obs1 = checks.get(ii);
      List<Check> obs2 = es.checks.get(ii);
      List<Integer> diff_obs = new ArrayList<Integer>();
      // System.out.printf ("checks 1/%d = %s%n", ii, obs1);
      // System.out.printf ("checks 2/%d = %s%n", ii, obs2);
      if (obs1.size() != obs2.size()) {
        if ((ii < (checks.size()-1))
            && (obs1.size() == 0) && (obs2.size() == 1)) {
          System.out.printf ("keeping mismatched check %s%n", obs2);
        } else { // number of checks must match
          System.out.printf ("obs %d size mismatch %d - %d\n", ii, obs1.size(),
                             obs2.size());
          System.out.printf ("Sequence1: %n%s%n", this);
          System.out.printf ("Sequence2: %n%s%n", es);
          System.out.printf ("Sequence1: %n%s%n", toCodeString());
          System.out.printf ("Sequence2: %n%s%n", es.toCodeString());
          System.out.printf ("obs1: %s%n", obs1);
          System.out.printf ("obs2: %s%n", obs2);
          assert false;
        }
      }
      for (int jj = 0; jj < obs1.size(); jj++) {
        Check ob1 = obs1.get(jj);
        Check ob2 = obs2.get(jj);
        if (!ob1.get_value().equals (ob2.get_value())) {
          diff_obs.add (0, jj);
          cnt++;
          if (print_diffs) {
            System.out.printf ("check mismatch in seq [%b]%n%s%n",
                               remove_diffs, es);
            System.out.printf ("Line %d, obs %d%n", ii, jj);
            System.out.printf ("ob1 = %s, ob 2 = %s%n", ob1, ob2);
          }
        } else { // they match
          if (ob1.get_value().contains ("EquipmentHolder@")) {
            System.out.printf ("check match in seq [%b]%n%s%n",
                               remove_diffs, es);
            System.out.printf ("Line %d, obs %d%n", ii, jj);
            System.out.printf ("ob1 = %s, ob 2 = %s%n", ob1, ob2);
            assert false;
          }
        }

      }

      // Remove any checks that don't match
      if (remove_diffs && (diff_obs.size() > 0)) {
        // System.out.printf ("obs1 size before = %d%n", obs1.size());
        for (int obs : diff_obs) {
          // System.out.printf ("Removing obs %d from sequence%n", obs);
          obs1.remove (obs);
          obs2.remove (obs);
        }
        // System.out.printf ("obs1 size after = %d%n", obs1.size());
      }
    }

    return cnt;

  }

  public String toDotString() {
    StringBuilder b = new StringBuilder();

    b.append("digraph G {\n");

    for (int i = 0 ; i < sequence.size() ; i++) {
      b.append("s" + i + " [color=" + getColor(i) + ",style=filled];\n");
    }

    for (int i = 0; i < sequence.size() ; i++) {
      StatementKind st = sequence.getStatementKind(i);
      List<Variable> inputs = sequence.getInputs(i);
      if (st instanceof PrimitiveOrStringOrNullDecl) {
        continue;
      }
      if (inputs.isEmpty()) {
        continue;
      }
      for (Variable input : inputs) {
        if (sequence.getStatementKind(input.getDeclIndex())
            instanceof PrimitiveOrStringOrNullDecl) {
          continue;
        }
        b.append("s" + sequence.lastUseBefore(i, input) + " -> " + "s" + i + ";\n");
      }
    }
    b.append("}\n");
    return b.toString();
  }

  private String getColor(int i) {
    ExecutionOutcome res = getResult(i);
    if (res instanceof NotExecuted) {
      return "white";
    } else if (res instanceof ExceptionalExecution) {
      return "yellow";
    } else {
      assert res instanceof NormalExecution;
      if (hasFailure(i)) {
        return "red";
      } else {
        return "green";
      }
    }
  }

  /**
   * Return the total number of checks in a list of sequences
   */
  public static int checks_count (List<ExecutableSequence> seqs) {

    int cnt = 0;

    for (ExecutableSequence es : seqs) {
      for (List<Check> obs : es.checks) {
        cnt += obs.size();
      }
    }

    return cnt;
  }


}
