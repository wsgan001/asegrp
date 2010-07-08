package randoop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import randoop.main.GenInputsAbstract;
import randoop.util.ArrayListSimpleList;
import randoop.util.ListOfLists;
import randoop.util.Log;
import randoop.util.MultiMap;
import randoop.util.Randomness;
import randoop.util.Reflection;
import randoop.util.SimpleList;
import randoop.util.Reflection.Match;

/**
 * Randoop's forward, component-based generator.
 */
public class ForwardGenerator extends AbstractGenerator {

  public final Set<Sequence> allSequences;
  public final SequenceCollection components;

  /** Sequences that are used in other sequences (and are thus redundant) **/
  public Set<Sequence> subsumed_sequences = new LinkedHashSet<Sequence>();

  public final CovWitnessHelperVisitor covWitnessHelperVisitor;

  // For testing purposes only. If Globals.randooptestrun==false then the array
  // is never populated or queried. This set contains the same set of
  // components as the set "allsequences" above, but stores them as
  // strings obtained via the toCodeString() method.
  private final List<String> allsequencesAsCode = new ArrayList<String>();

  // For testing purposes only [[comment]]
  private final List<Sequence> allsequencesAsList = new ArrayList<Sequence>();


  protected ObjectCache objectCache = new ObjectCache(new EqualsMethodMatcher());

  public void setObjectCache(ObjectCache newCache) {
    if (newCache == null) throw new IllegalArgumentException();
    this.objectCache = newCache;
  }

  public ForwardGenerator(List<StatementKind> statements,
      List<Class<?>> coverageClasses,
      long timeMillis, int maxSequences,
      SequenceCollection seeds) {

    super(statements, coverageClasses, timeMillis, maxSequences, seeds);

    System.out.printf ("list of statements for ForwardGenerator = %s%n",
                       statements);

    if (seeds == null)
      this.components = new SequenceCollection(SeedSequences.defaultSeeds());
    else
      this.components = seeds;

    this.allSequences = new LinkedHashSet<Sequence>();

    // Add a visitor for coverage.
    if (GenInputsAbstract.output_cov_witnesses) {
      this.covWitnessHelperVisitor =
        new CovWitnessHelperVisitor(this.covClasses, this.branchesToCoveringSeqs);
      this.executionVisitor.visitors.add(this.covWitnessHelperVisitor);
    } else {
      this.covWitnessHelperVisitor = null;
    }

  }

  @Override
  public long numSequences() {
    return allSequences.size();
  }

  public ExecutableSequence step() {

    long startTime = System.nanoTime();

    SequenceGeneratorStats.steps++;

    if (components.size() % GenInputsAbstract.clear == 0)
      components.clear();

    ExecutableSequence eSeq = null;

    eSeq = createNewUniqueSequence();
    if (eSeq == null) {
      return null;
    }

    assert eSeq != null;

    if (GenInputsAbstract.dontexecute) {
      this.components.add(eSeq.sequence);
      return null;
    }

    // If Randoop detects possible hang, outputs last
    // sequence under execution.
    SequenceGeneratorStats.currSeq = eSeq.sequence;

    long endTime = System.nanoTime();
    long gentime = endTime - startTime;
    startTime = endTime; // reset start time.

    eSeq.execute(executionVisitor);

    endTime = System.nanoTime();

    eSeq.exectime = endTime - startTime;
    startTime = endTime; // reset start time.

    processSequence(eSeq);

    if (eSeq.sequence.hasActiveFlags()) {
      components.add(eSeq.sequence);
    }

    endTime = System.nanoTime();
    gentime += endTime - startTime;
    eSeq.gentime = gentime;

    return eSeq;

  }

  public Set<Sequence> allSequences() {
    return Collections.unmodifiableSet(this.allSequences);
  }

  public void processSequence(ExecutableSequence seq) {

    if (GenInputsAbstract.offline) {
      seq.sequence.setAllActiveFlags();
      return;
    }

    if (seq.hasNonExecutedStatements()) {
      seq.sequence.clearAllActiveFlags();
      return;
    }

    if (seq.hasFailure()) {
      seq.sequence.clearAllActiveFlags();
      return;
    }

    if (!seq.isNormalExecution()) {
      seq.sequence.clearAllActiveFlags();
      return;
    }

    if (GenInputsAbstract.use_object_cache) {
      objectCache.setActiveFlags(seq);
    }
  }

  /**
   * Tries to create and execute a new sequence. If the sequence is new (not
   * already in the specivied sequence manager), then it is executed and
   * added to the manager's sequences. If the sequence created is already in
   * the manager's sequences, this method has no effect.
   */
  ExecutableSequence createNewUniqueSequence() {

    if (Log.isLoggingOn()) Log.logLine("-------------------------------------------");

    StatementKind statement = null;

    if (this.statements.isEmpty())
      return null;

    // Select a StatementInfo
    statement = Randomness.randomMember(this.statements);
    if (Log.isLoggingOn()) Log.logLine("Selected statement: " + statement.toString());

    stats.statStatementSelected(statement);

    // jhp: add flags here
    InputsAndSuccessFlag  sequences = selectInputs(statement, components);

    if (!sequences.success) {
      if (Log.isLoggingOn()) Log.logLine("Failed to find inputs for statement.");
      stats.statStatementNoArgs(statement);
      return null;
    }

    int[] seqlengths = new int[sequences.sequences.size()];
    for (int i = 0 ; i < sequences.sequences.size() ; i++) {
      Sequence oneseq = sequences.sequences.get(i);
      assert oneseq.size() > 0;
      seqlengths[i] = oneseq.size();
    }

    Sequence concatSeq = Sequence.concatenate(sequences.sequences);

    // Figure out input variables.
    List<Variable> inputs = new ArrayList<Variable>();
    for (Integer oneinput : sequences.variables) {
      Variable v = concatSeq.getVariable(oneinput);
      inputs.add(v);
    }

    Sequence newSequence = concatSeq.extend(statement, inputs);

    // With .5 probability, do a primitive value heuristic.
    if (GenInputsAbstract.repeat_heuristic && Randomness.nextRandomInt(10) == 0) {
      int times = Randomness.nextRandomInt(100);
      newSequence = newSequence.repeatLast(times);
      if (Log.isLoggingOn()) Log.log(">>>" + times + newSequence.toCodeString());
    }

    // Heuristic: if parameterless statement, subsequence inputs
    // will all be redundant, so just remove it from list of
    // statements.
    // Note that this can make the list of statements empty, violating its
    // rep invariant.
    if (GenInputsAbstract.no_args_statement_heuristic && statement.getInputTypes().size() == 0) {
      statements.remove(statement);
    }

    // If sequence is larger than size limit, try again.
    if (newSequence.size() > GenInputsAbstract.maxsize) {
      if (Log.isLoggingOn()) Log.logLine("Sequence discarded because size " + newSequence.size() + " exceeds maximum allowed size " + GenInputsAbstract.maxsize);
      stats.statStatementToBig(statement);
      return null;
    }

    randoopConsistencyTests(newSequence);

    if (this.allSequences.contains(newSequence)) {
      if (Log.isLoggingOn()) Log.logLine("Sequence discarded because the same sequence was previously created.");
      stats.statStatementRepeated(statement);
      return null;
    }

    this.allSequences.add(newSequence);

    for (Sequence s : sequences.sequences) {
      s.lastTimeUsed = java.lang.System.currentTimeMillis();
    }

    randoopConsistencyTest2(newSequence);

    if (Log.isLoggingOn()) {
      Log.logLine("Successfully created new unique sequence:" + newSequence.toString());
    }
    //System.out.println("###" + statement.toStringVerbose() + "###" + statement.getClass());
    stats.statStatementNotDiscarded(statement);

    stats.checkStatsConsistent();

    // Keep track of any input sequences that are used in this sequence
    // Tests that contain only these sequences are probably redundant
    for (Sequence is : sequences.sequences) {
      subsumed_sequences.add (is);
    }

    return new ExecutableSequence(newSequence);
  }

  private void randoopConsistencyTest2(Sequence newSequence) {
    // Testing code.
    if (Globals.randooptestrun) {
      this.allsequencesAsCode.add(newSequence.toCodeString());
      this.allsequencesAsList.add(newSequence);
    }
  }

  private void randoopConsistencyTests(Sequence newSequence) {
    // Testing code.
    if (Globals.randooptestrun) {
      String code = newSequence.toCodeString();
      if (this.allSequences.contains(newSequence)) {
        if (!this.allsequencesAsCode.contains(code)) {
          throw new IllegalStateException(code);
        }
      } else {
        if (this.allsequencesAsCode.contains(code)) {
          int index = this.allsequencesAsCode.indexOf(code);
          StringBuilder b = new StringBuilder();
          Sequence  co = this.allsequencesAsList.get(index);
          co.equals(newSequence);
          b.append("new component:" + Globals.lineSep + "" + newSequence.toString()  + "" + Globals.lineSep + "as code:" + Globals.lineSep + "" + code + Globals.lineSep);
          b.append("existing component:" + Globals.lineSep + "" + this.allsequencesAsList.get(index).toString() + "" + Globals.lineSep + "as code:" + Globals.lineSep + ""
              + this.allsequencesAsList.get(index).toCodeString());
          throw new IllegalStateException(b.toString());
        }
      }
    }
  }

  public InputsAndSuccessFlag selectInputs(StatementKind statement, SequenceCollection  components) {

    List<Class<?>> inputClasses = statement.getInputTypes();

    List<Sequence> sequences = new ArrayList<Sequence>();
    int totStatements = 0;
    List<Integer> variables = new ArrayList<Integer>();

    MultiMap<Class<?>, Integer> typesToVars = new MultiMap<Class<?>, Integer>();
    SubTypeSet types = new SubTypeSet(false);

    for (int i = 0; i < inputClasses.size(); i++) {
      Class<?> t = inputClasses.get(i);

      // TODO check if this ever happens.
      if(!Reflection.isVisible(t)) return new InputsAndSuccessFlag (false, null, null);

      boolean isReceiver = (i == 0 && (statement instanceof RMethod)
          && (!((RMethod) statement).isStatic()));

      // If alias factor is given, attempt (with some probability)
      // to use a variable already from a previous sequence.
      if (GenInputsAbstract.alias_ratio != null &&
          Randomness.weighedCoinFlip(GenInputsAbstract.alias_ratio)) {

        List<SimpleList<Integer>> candidateVars = new ArrayList<SimpleList<Integer>>();
        for (Class<?> match : types.getMatches(t)) {
          assert typesToVars.keySet().contains(match);
          candidateVars.add(new ArrayListSimpleList<Integer>(new ArrayList<Integer>(typesToVars.getValues(match))));
        }
        SimpleList<Integer> candidateVars2 = new ListOfLists<Integer>(candidateVars);
        if (candidateVars2.size() > 0) {
          int randVarIdx = Randomness.nextRandomInt(candidateVars2.size());
          Integer randVar = candidateVars2.get(randVarIdx);
          variables.add(randVar);
          continue;
        }
      }

      SimpleList<Sequence> l = null;
      if (GenInputsAbstract.always_use_ints_as_objects && t.equals(Object.class)) {
        if (Log.isLoggingOn()) Log.logLine("Integer-as-object heuristic: will use random Integer.");
        l = components.getSequencesForType(int.class, false);
      } else if (t.isArray()) {
        if (Log.isLoggingOn()) Log.logLine("Array creation heuristic: will create helper array of type " + t);
        l = HelperSequenceCreator.createSequence(t, components);
      } else {
        if (Log.isLoggingOn()) Log.logLine("Will query component set for objects of type" + t);
        l = components.getSequencesForType(t, false);
      }
      assert l != null;

      if (Log.isLoggingOn()) Log.logLine("components: " + l.size());

      if (l.size() == 0) {
        if (isReceiver || GenInputsAbstract.forbid_null) {
          if (Log.isLoggingOn()) Log.logLine("forbid-null option is true. Failed to create new sequence.");
          return new InputsAndSuccessFlag (false, null, null);
        } else {
          if (Log.isLoggingOn()) Log.logLine("Will use null as " + i + "-th input");
          StatementKind st = PrimitiveOrStringOrNullDecl.nullOrZeroDecl(t);
          Sequence seq = new Sequence().extend(st, new ArrayList<Variable>());
          variables.add(totStatements);
          sequences.add(seq);
          assert seq.size() == 1;
          totStatements++;
          // Null is not an interesting value to add to the set of
          // possible values to reuse, so we don't update typesToVars or types.
          continue;
        }
      }

      if (!isReceiver && GenInputsAbstract.null_ratio != null
          && Randomness.weighedCoinFlip(GenInputsAbstract.null_ratio)) {
        if (Log.isLoggingOn()) Log.logLine("null-ratio option given. Randomly decided to use null as input.");
        StatementKind st = PrimitiveOrStringOrNullDecl.nullOrZeroDecl(t);
        Sequence seq = new Sequence().extend(st, new ArrayList<Variable>());
        variables.add(totStatements);
        sequences.add(seq);
        assert seq.size() == 1;
        totStatements++;
        continue;
      }

      Sequence chosenSeq = null;
      if (GenInputsAbstract.weighted_inputs) {
        chosenSeq = Randomness.randomMemberWeighted(l);
      } else {
        chosenSeq = Randomness.randomMember(l);
      }

      // Now, find values that satisfy the constraint set.
      Match m = Match.COMPATIBLE_TYPE;
      //if (i == 0 && statement.isInstanceMethod()) m = Match.EXACT_TYPE;
      Variable randomVariable = chosenSeq.randomVariableForTypeLastStatement(t, m);

      if (randomVariable == null) {
        throw new BugInRandoopException("type: " + t + ", sequence: " + chosenSeq);
      }

      if (i == 0
          && (statement instanceof RMethod)
          && (!((RMethod) statement).isStatic())
          && chosenSeq.getCreatingStatement(randomVariable) instanceof PrimitiveOrStringOrNullDecl)
        return new InputsAndSuccessFlag (false, null, null);

      if (GenInputsAbstract.alias_ratio != null) {
        // Update types and typesToVars.
        for (int j = 0 ; j < chosenSeq.size() ; j++) {
          StatementKind stk = chosenSeq.getStatementKind(j);
          if (stk instanceof PrimitiveOrStringOrNullDecl)
            continue; // Prim decl not an interesting candidate for multiple uses.
          Class<?> outType = stk.getOutputType();
          types.add(outType);
          typesToVars.add(outType, totStatements + j);
        }
      }

      //assert Reflection.canBeUsedAs(randomVariable.getType(), t);
      variables.add(totStatements + randomVariable.index);
      sequences.add(chosenSeq);
      totStatements += chosenSeq.size();
    }

    return new InputsAndSuccessFlag (true, sequences, variables);
  }

  /**
   * Returns the set of sequences that are used as inputs in other sequences
   * (and can thus be thought of as subsumed by another sequence).  
   */
  public Set<Sequence> subsumed_sequences() {
    return subsumed_sequences;
  }
}
