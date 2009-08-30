package test.cases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TestCaseExecutor {

	private StringBuffer results = new StringBuffer();

	private static boolean procDone(Process p) {
		try {
			int v = p.exitValue();
			return true;
		} catch (IllegalThreadStateException e) {
			return false;
		}
	}

	private void executeOneTestCase(String[] cmd) {
		if (cmd.length != 13) {
			System.err
					.println("The arguments for the exe file are less than 13");
			return;
		}
		try {
			Process p = Runtime.getRuntime().exec(cmd); // tell the path.
			// read the standard output of the command
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String s = "";
			String result = "";
			int count = 0;
			while ((s = stdInput.readLine()) != null) {
				count++;
				result = result + s + "\n";
				System.out.println("result " + count + ": " + result);
				this.results.append(result + "\r\n");
			}
			stdInput.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void executeTestCasesForOneVersion(ArrayList<String[]> cmdList,
			String resultFile) {
		// TODO Auto-generated method stub
		for (int i = 0; i < cmdList.size(); i++) {
			executeOneTestCase(cmdList.get(i));
		}

		// Output the test results into a txt file
		try {
			// Create file
			FileWriter fstream = new FileWriter(resultFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(this.results.toString());
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private ArrayList<String[]> constructCMDList(String exeFile, File tcFile) {
		ArrayList<String[]> cmds = new ArrayList<String[]>();

		try {
			BufferedReader input = new BufferedReader(new FileReader(tcFile));

			try {
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					String[] inputs = line.split(",");
					String[] cmd = new String[13];
					cmd[0] = exeFile;
					for (int i = 1; i < 13; i++) {
						cmd[i] = inputs[i - 1];
					}
					cmds.add(cmd);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return cmds;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String exeFile = "C:\\MyWorkspace\\My1stCProject\\Debug\\My1stCProject.exe";
		String tcFile = "C:\\MyWorkspace\\li-591T\\data\\testerdesk\\Pairwise_TCAS_processed.txt";
		String resultFile = "C:\\MyWorkspace\\li-591T\\data\\testerdesk\\Pairwise_TCAS_results.txt";// Set where store the result files.
		
		TestCaseExecutor executor = new TestCaseExecutor();

		File testcaseFile = new File(tcFile); 
		//each testcaseFile stores a group of test cases
		//a line corresponds to a test case
		//use comma to split 12 inputs
		ArrayList<String[]> cmdList = executor.constructCMDList(exeFile,
				testcaseFile);
		executor.executeTestCasesForOneVersion(cmdList, resultFile);

		// TODO Auto-generated method stub

		/*
		String[] cmd = {
				"C:\\MyWorkspace\\My1stCProject\\Debug\\My1stCProject.exe",
				"601", "1", "1", "2", "600", "1", "0", "499", "639", "2", "2",
	 			"0" };
				executor.executeOneTestCase(cmd);
				*/
		
	}


}
