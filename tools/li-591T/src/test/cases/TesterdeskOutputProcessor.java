package test.cases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class TesterdeskOutputProcessor {
	private String sPrefix = "any_value_of_";
	private HashMap<String, int[]> variable_value = new HashMap();
		
	public TesterdeskOutputProcessor() {
		this.variable_value.put("Cur_Vertical_Sep", new int[]{ 299, 300, 601});
		this.variable_value.put("High_Confidence", new int[]{0, 1});
		this.variable_value.put("Two_of_Three_Reports_Valid",new int[]{0, 1});
		this.variable_value.put("Own_Tracked_Alt",new int[]{ 1, 2 });
		this.variable_value.put("Own_Tracked_Alt_Rate",new int[]{ 600, 601 });
		this.variable_value.put("Other_Tracked_Alt",new int[]{ 1, 2 });
		this.variable_value.put("Alt_Layer_Value",new int[]{0,1,2,3});
		this.variable_value.put("Up_Separation",new int[]{ 0, 399, 400, 499, 500, 639, 640, 739, 740, 840 });
		this.variable_value.put("Down_Separation ",new int[]{ 0, 399, 400, 499, 500, 639, 640, 739, 740, 840 });
		this.variable_value.put("Other_RAC",new int[]{ 0, 1, 2 });
		this.variable_value.put("Other_Capability",new int[]{ 1, 2 });
		this.variable_value.put("Climb_Inhibit",new int[]{0,1});	
	}
	
	public void generateCMDs(File testerdeskOutput, String outputFile){
		StringBuilder cmdList = new StringBuilder();
		
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      BufferedReader input =  new BufferedReader(new FileReader(testerdeskOutput));
	      
	        String line = null; //not declared within while loop
	        
	        while (( line = input.readLine()) != null){	        	
	        	String[] inputs = line.split("\t");
	        	for (int i=0;i<inputs.length;i++){
	        		if (inputs[i].startsWith(sPrefix)){
	        			String vName = inputs[i].substring(sPrefix.length(),inputs[i].length());	        			
	        			//random choose a value for the input variable
	        			inputs[i] = new Integer(this.variable_value.get(vName.trim())[0]).toString();
	        		}
	        		cmdList.append(inputs[i]+",");
	        	}
	        	cmdList.delete(cmdList.lastIndexOf(","),cmdList.length());
	        	cmdList.append("\r\n");
	        }
	      input.close();	      
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
	    
	 // Output test cases (command lines) into a txt file
		try {
			// Create file
			FileWriter fstream = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fstream);			
			out.write(cmdList.toString());
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TesterdeskOutputProcessor top = new TesterdeskOutputProcessor();
		// TODO Auto-generated method stub
		top.generateCMDs(new File("C:\\MyWorkspace\\li-591T\\data\\testerdesk\\Pairwise_TCAS.txt"),"C:\\MyWorkspace\\li-591T\\data\\testerdesk\\Pairwise_TCAS_processed.txt");
	}

}
