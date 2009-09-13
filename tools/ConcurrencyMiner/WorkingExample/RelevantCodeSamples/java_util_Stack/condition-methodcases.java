package javax.swing.tree;

import gnu.java.util.EmptyEnumeration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;
import java.util.HashSet;

/**
 * DefaultMutableTreeNode
 *
 * @author Andrew Selkirk
 * @author Robert Schuster (robertschuster@fsfe.org)
 */
public class DefaultMutableTreeNode
  implements Cloneable, MutableTreeNode, Serializable
{
  private static final long serialVersionUID = -4298474751201349152L;

  /**
   * EMPTY_ENUMERATION
   */
  public static final Enumeration EMPTY_ENUMERATION =
    EmptyEnumeration.getInstance();

  /**
   * parent
   */
  protected MutableTreeNode parent;

  /**
   * children
   */
  protected Vector children = new Vector();

  /**
   * userObject
   */
  protected transient Object userObject;

  /**
   * allowsChildren
   */
  protected boolean allowsChildren;

  public void normalCases1()
  {
	Stack stack = new Stack();
	Integer intObj = new Integer(0);
	int status = stack.push(intObj);
	if(status == Stack.SUCCESS) {
		System.out.println("Things are fine");
	} 
	else if (status == Stack.FAILURE)
	{
		System.out.println("Things are not fine");
	}
  }

   /**
   * getDepth
   *
   * @return int
   */
  public void normalCases()
  {
    Stack stack = new Stack();
	Integer intObj = new Integer(0);
	Integer intObj10 = new Integer(1);
	int status = 0;
	
	//Kind 1 check : if (arg != null) ob1.method(arg)
	//Kind 3 check : Check conditions on the receiver variables. like stack != null and stack instanceof Stack
	if((stack instanceof Stack) && (stack != null) && (intObj != null)) {
		//Same method call in the guarding condition
		int status = stack.push(intObj);	
		if(status == Stack.SUCCESS) {
			System.out.println("Things are fine");
		} 
		else if (status == Stack.FAILURE)
		{
			System.out.println("Things are not fine");
		}
	} 

	//Kind 2 check : if (x.y(arg) != null) ob1.method(arg) 
	//Kind 4 check : if(X.Y(receiverVar) != null) receiverVar.method();
	HashSet<String> hashSet = new HashSet<String>();
	Integer intObj1 = new Integer(2);
	boolean bVal = hashSet.anotherCheck(stack);
	if(hashSet != null && hashSet.contains(intObj1) && hashSet.contains(stack) && bVal) {
		status = stack.push(intObj1);
		if(status == Stack.SUCCESS) {
			System.out.println("Things are fine");
		} 
		else if (status == Stack.FAILURE)
		{
			System.out.println("Things are not fine");
		}
		
		if(!stack.isEmpty()) {
			//...
		}
	}	
    
	//Preceding method call checks
	boolean bResult1 = stack.hasMoreElements();
	HashSet<String> hashSet = new HashSet<String>();
	while(stack.size1() != 0 && bResult1)
	{
		Integer popValue = stack.pop();

		bResult1 = stack.hasMoreElements();
		boolean bOtherResult = hashSet.contains(popValue);
		if(bOtherResult)
			System.out.println(popValue);
	}
  }
  

  /* Where the boolean variables are checked before the given condition */
  public void exceptionCases() 
  {
	 Stack stack = new Stack();
	 Integer intObj = new Integer(0);
	
	 HashSet<String> hashSet = new HashSet<String>(); 
	 boolean bVal = hashSet.anotherCheck(stack);
	 boolean bVal2 = hashSet.contains(intObj);

	 if(bVal2 && bVal)
	 {
		status = stack.push(intObj);
		if(status == Stack.SUCCESS) {
			System.out.println("Things are fine");
		} 
		else if (status == Stack.FAILURE)
		{
			System.out.println("Things are not fine");
		} 
	 }     
  }

  /*Where the same kind of check happens two times in the same method */
  public void multiCheckOnArgument()
  {
	Stack stack = new Stack();
    Integer intObj = new Integer(0);
	if(intObj != null)
		stack.push(intObj);

	Integer intObj1 = new Integer(1);
	if(intObj1 != null)
		stack.push(intObj1);
	 
  }

  /* An additional where same method call on the same variable happens two times */
  /*public void check10_12() 
  {
	 Stack stack = new Stack();
	 Integer intObj = new Integer(0);
	
	 HashSet<String> hashSet = new HashSet<String>(); 
	 boolean bVal = hashSet.anotherCheck(stack);
	 boolean bVal1 = hashSet.anotherCheck(stack);

	 if(bVal && bVal1) 
	 {
		boolean bCal = stack.push(intObj);
		if(bCal) { }
	 }
  }*/

  public void testScopeOfConditions() 
  {
	Stack stack = new Stack();
	Integer intObj = new Integer(0);
	int status = 0;


  }

	public void retvalToAFunc() 
	{
		Integer intObj = new Integer();
		if(intObj != null) {
			if(intObj instanceof Integer) {
			}
		} else {
			if(intObj instanceof Double) {
			}
		}

			Stack stack = new Stack();
			if(stack != null) {
				Object obj;
				obj = stack.push(intObj);	
			}
		
	}

	public void succeedingReceiverCondVarTest()
    {
		Integer intObj = new Integer();
		Stack stack = new Stack();

		if(stack != null) {
			stack.push(intObj);

			if(stack.empty()) {
				System.out.println("Error!! stack cannot be empty");
			}

			if(stack == null) {
				System.out.println("This cannot happen");
			}
		}

	}

	public void complexCondTestInRetval()
	{
		Stack st = new Stack();
		boolean bRVal;
		if(bRVal = st.isEmpty() && test()) {
			System.out.println("Stack no elem");
		} else {
			System.out.println("Stack has elem");
		}

	}

	public void conditionalsInAssignment()
	{
		Stack st = new Stack();
		boolean bRVal;
		bRVal = !st.isEmpty() && bRVal;
		if(bRVal) {
			System.out.println("Stack no elem");
		} else {
			System.out.println("Stack has elem");
		}

	}



	public boolean test() {
		return true;
	}

}