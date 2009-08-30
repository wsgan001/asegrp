package fault.induccing.inputs.isolation;

import java.util.ArrayList;

public class FactorCombination {
	//  The index of the ArrayList attributes associates the ArrayList attributes (factor, value, ratio) 
	public int numberOfFactors=0;	
	public ArrayList<String> factorName = new ArrayList<String>();
	public ArrayList<String> factorValue = new ArrayList<String>();
	//public ArrayList<>
	public float pRatio = 0; 
	//  pRatio = number of this combination in passed test cases / number of test cases contain this combination
	public float fRatio = 0;
	//  fRatio = number of this combination in failed test cases / number of test cases contain this combination
	//  if pRatio ==0 && fRatio >0 && factorName.size > 1 --> Case 1 in my discussion
	//  if pRatio >0 && fRatio >0  --> Case 2 in my discussion
	public float pTC=0;
	//Number of passed test cases
	public float fTC=0;
	//Number of failed test cases
}
