package xweb.assocminer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import xweb.code.analyzer.holder.MethodInvocationHolder;

/**
 * A class for post-processing the data generated for association rule.
 * This is helpful when the data is generated on server
 * @author suresh_thummalapenta
 *
 */
public class AssocDataPostProcessor {
	
	public static void main(String args[]) {
		try {
			HashMap<Integer, String> idToMIHMapper = new HashMap<Integer, String>();
			Scanner scMethodIds = new Scanner(new File(args[0]));
			while(scMethodIds.hasNextLine()) {
				String inpText = scMethodIds.nextLine();
				StringTokenizer stTokens = new StringTokenizer(inpText, " ");
				Integer intObj = Integer.parseInt(stTokens.nextToken().trim());
				stTokens.nextToken();
				idToMIHMapper.put(intObj, stTokens.nextToken());			
			}
			scMethodIds.close();
			
			
			Scanner scRuleFile = new Scanner(new File(args[1]));
			BufferedWriter bwAssocOut = new BufferedWriter(new FileWriter(args[2]));
			bwAssocOut.write("TraceID, Support, Category, NM, EM, Context\n");
			int traceCounter = 1;
			while(scRuleFile.hasNextLine()) {
				String inpStr = scRuleFile.nextLine();
				
				int leftId = -1, rightId = -1;
				int indexOfLeft = inpStr.indexOf("left=");
				if(indexOfLeft != -1) {
					indexOfLeft += 5;
					int indexOfEndVal = inpStr.indexOf(" ", indexOfLeft);
					leftId = Integer.parseInt(inpStr.substring(indexOfLeft, indexOfEndVal));				
				}
				
				int indexOfRight = inpStr.indexOf("right=");
				if(indexOfRight != -1) {
					indexOfRight += 6;
					int indexOfEndVal = inpStr.indexOf(" ", indexOfRight);
					rightId = Integer.parseInt(inpStr.substring(indexOfRight, indexOfEndVal));				
				}
				
				if(leftId == -1 || rightId == -1)
					continue;
				
				bwAssocOut.write((traceCounter++) + ",,," + idToMIHMapper.get(leftId) + "," + idToMIHMapper.get(rightId) + ",\n");
			}
			
			bwAssocOut.close();
			scRuleFile.close();			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}	
}
