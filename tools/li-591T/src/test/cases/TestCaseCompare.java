package test.cases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class TestCaseCompare {

	// Looking for the test result from an arff file
	// where store the existing test cases and test results
	public ArrayList<String> getTestResults(String arffFile,
			HashSet<String> currentTestCases) {
		ArrayList<String> tc_result = new ArrayList<String>();
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(arffFile));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.contains(",") && !line.contains("{"))
					if (currentTestCases.contains(line.substring(0, line
							.lastIndexOf(",")))) {
						tc_result.add(line);
					}
			}
			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tc_result;
	}

	public void outputTCResults(ArrayList<String> tc_result, String outputFile) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tc_result.size(); i++)
			sb.append(tc_result.get(i) + "\r\n");

		try {
			// Create file
			FileWriter fstream = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(sb.toString());
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public HashSet<String> constructCurrentTCList(String tcFile) {
		HashSet<String> tcList = new HashSet<String>();
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(tcFile));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				tcList.add(line);
			}
			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tcList;
	}

	public static void main(String[] args) {
		String tcFile = "C:\\MyWorkspace\\li-591T\\data\\testerdesk\\Pairwise_TCAS_processed.txt";
		String resultFile = "C:\\MyWorkspace\\li-591T\\data\\testerdesk\\Pairwise_TCAS_tc_results.txt";
		String arffFile = "C:\\MyWorkspace\\li-591T\\data\\arff-inputs\\v1\\v1.arff";

		TestCaseCompare tcc = new TestCaseCompare();
		ArrayList<String> tc_results = tcc.getTestResults(arffFile, tcc
				.constructCurrentTCList(tcFile));
		tcc.outputTCResults(tc_results, resultFile);
	}
}
