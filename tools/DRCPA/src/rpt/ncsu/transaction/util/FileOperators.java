package rpt.ncsu.transaction.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import rpt.ncsu.transaction.analysis.MethodInvocation;
import rpt.ncsu.transaction.analysis.Transaction;

public class FileOperators {

	public static void outputFile(String contents, String outputFile) {
		try {
			File folder =new File(outputFile).getParentFile();
			if(!folder.exists())
				folder.mkdirs();
		    
			// Create file
			FileWriter fstream = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(contents);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static void splitFile(String file, int lineNum) {
		try {
			FileInputStream fin = new FileInputStream(file);

			BufferedReader myInput = new BufferedReader(new InputStreamReader(
					fin));
			StringBuilder sb = new StringBuilder();
			String thisLine = "";
			int count = 0;

			while ((thisLine = myInput.readLine()) != null) {

				sb.append(thisLine);
				count++;
				if (count % lineNum == 0) {
					outputFile(sb.toString(), file.replace(".xml", "-" + count
							+ ".xml"));
					sb.delete(0, sb.length() - 1);
				}
			}
			if (sb.length() > 0)
				outputFile(sb.toString(), file.replace(".xml", "-" + count
						+ ".xml"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void formatXML(String file) {
		try {
			FileInputStream fin = new FileInputStream(file);

			BufferedReader myInput = new BufferedReader(new InputStreamReader(
					fin));
			StringBuilder sb = new StringBuilder();
			String thisLine = "";

			while ((thisLine = myInput.readLine()) != null) {
				// this statement reads the line from the file and print it to
				// the console.
				String[] lines = thisLine.split("/>");
				for (int i = 0; i < lines.length; i++) {
					sb.append(lines[i] + "/>\r\n");
				}
			}

			outputFile(sb.toString(), file.replace(".xml", "formated.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void outputTransactionInText(ArrayList<Transaction> trans,
			String outputFile, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < trans.size(); i++) {
			Transaction tran = trans.get(i);

			sb.append("$$ Transaction " + (i + count) + " $$\r\n");
			sb.append("[Average Response Time: "
					+ tran.getAveResponseTime() + "] [Total:" + tran.getTotal() + "]\r\n");

			for (int j = 0; j < tran.getMethodInvoke().size(); j++) {
				MethodInvocation mi = (MethodInvocation) tran.getMethodInvoke().get(j)[1];
				NumberFormat formatter = new DecimalFormat("###.#####");
				sb.append("[" + tran.getMethodInvoke().get(j)[0] + "] [" + new String(mi.getClassName().replace("/", ".")) + "] [" + mi.getMethodName()
						+ "] [AET:" + formatter.format(mi.getAveResponseTime()) + "][AET_IN:"+ formatter.format(mi.getAveTimeWithoutInvocations()) +"] \r\n");
			}
			sb.append("\r\n");
		}

		outputFile(sb.toString(), outputFile);
	}
	
}
