package fault.induccing.inputs.isolation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLTreeAnalyzer {

	// Print out the failure inducing combinations for manual analysis
	public void printFailedCombInFolder(String xmlFolder, String outputFolder) {
		File dir = new File(xmlFolder);
		String[] files = dir.list();
		if (files == null) {
			// Either dir does not exist or is not a directory
			System.err.println("File doesn't exist");
		} else {
			for (int j = 0; j < files.length; j++) {
				// Get filename of file or directory
				String filename = files[j];
				if (filename.endsWith(".xml")) {
					// Generate Decision Tree
					String arg1 = dir.getPath() + "\\" + filename;
					String arg2 = outputFolder + "\\" + filename + ".txt";

					ArrayList<FactorCombination> combinations = getFailureRevealingCombinations(arg1);
					combinations
							.addAll(findSubCombinations(combinations, arg1));

					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < combinations.size(); i++) {
						FactorCombination aCom = (FactorCombination) (combinations
								.get(i));
						for (int k = 0; k < aCom.numberOfFactors; k++) {
							sb.append(aCom.factorName.get(k) + "="
									+ aCom.factorValue.get(k) + "\t");
						}
						sb.append("fTC: " + aCom.fTC + "\t" + "pTC: "
								+ aCom.pTC + "\t" + "fRatio: " + aCom.fRatio
								+ "\t" + "pRatio: " + aCom.pRatio + "\t"
								+ "\r\n\r\n");
					}

					// Output the info into a txt file -- for debug
					try {
						// Create file
						FileWriter fstream = new FileWriter(arg2);
						BufferedWriter out = new BufferedWriter(fstream);
						out.write(sb.toString());
						// Close the output stream
						out.close();
					} catch (Exception e) {// Catch exception if any
						System.err.println("Error: " + e.getMessage());
					}
				}
			}
		}
	}

	private ArrayList<FactorCombination> findSubCombinations(
			ArrayList<FactorCombination> combinations, String treeInTxt) {
		ArrayList<FactorCombination> subCombinations = new ArrayList<FactorCombination>();
		HashMap<ArrayList<String>, ArrayList<String>> existSubCom = new HashMap<ArrayList<String>, ArrayList<String>>();
		for (int i = 0; i < combinations.size() - 1; i++) {
			FactorCombination factorComI = combinations.get(i);
			ArrayList<String> currentSubComName = new ArrayList<String>();
			ArrayList<String> currentSubComValue = new ArrayList<String>();
			for (int j = 0; j < factorComI.numberOfFactors - 1; j++) {
				String elementJName = factorComI.factorName.get(j);
				String elementJValue = factorComI.factorValue.get(j);
				currentSubComName.add(elementJName);
				currentSubComValue.add(elementJValue);

				boolean flag = false;
				float fTC = factorComI.fTC;
				float allTC = 0;
				for (int k = i + 1; k < combinations.size(); k++) {
					// if currentSubCom is not in existSubCom and exists in
					// combinations.get(k)
					// currentSubCom should be added into existSubCom and fRatio
					// of currentSubCom is calculated
					if (existSubCom.containsKey(currentSubComName)) {
						if (existSubCom.get(currentSubComName).equals(
								currentSubComValue))
							continue;
					} else if (isSubSet(combinations.get(k), currentSubComName,
							currentSubComValue)) {
						flag = true;
						fTC = fTC + combinations.get(k).fTC;
					}
				}
				if (flag) {
					ArrayList key = new ArrayList();
					ArrayList value = new ArrayList();
					for (int k = 0; k < currentSubComName.size(); k++) {
						key.add(currentSubComName.get(k));
						value.add(currentSubComValue.get(k));
					}
					existSubCom.put(key, value);

					FactorCombination fc = new FactorCombination();
					fc.factorName = currentSubComName;
					fc.factorValue = currentSubComValue;
					fc.numberOfFactors = currentSubComName.size();

					// Get allTC
					try {
						DOMParser parser = new DOMParser();
						parser.parse(treeInTxt);
						Document doc = parser.getDocument();
						NodeList outputNodes = doc.getElementsByTagName("Test");
						// <Output decision="..." info="(...)" />
						// NodeList nodes = doc.getChildNodes();
						for (int nodeNum = 0; nodeNum < outputNodes.getLength(); nodeNum++) {
							NamedNodeMap attrs = outputNodes.item(nodeNum)
									.getAttributes();
							String nameAtt = ((Attr) attrs.item(0)).getValue();
							String valueAtt = ((Attr) attrs.item(2)).getValue();
							if (elementJName.equals(nameAtt)
									&& elementJValue.equals(valueAtt)) {
								allTC = getTCNumberOnLeaves(outputNodes
										.item(nodeNum));
								break;
							}
						}
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}

					fc.fTC = fTC;
					fc.pTC = allTC - fTC;
					fc.fRatio = fTC / allTC;
					fc.pRatio = fc.pTC / allTC;

					subCombinations.add(fc);
				}
			}
		}
		return subCombinations;
	}

	private float getTCNumberOnLeaves(Node node) {
		// TODO Auto-generated method stub
		float info = 0;
		int childrenNum = node.getChildNodes().getLength();
		if (childrenNum > 0) {
			for (int i = 0; i < childrenNum; i++) {
				if(node.getChildNodes().item(i).getNodeName().equals("Output")||node.getChildNodes().item(i).getNodeName().equals("Test"))
					info = info + getTCNumberOnLeaves(node.getChildNodes().item(i));
			}
		} else {
			// get tc #
			if(!node.getNodeName().equals("Output")){
				System.err.println("!!! This is not a leaf !!!");
			}
			else{
				Attr infoAtt = (Attr) node.getAttributes().item(1);
				String number = infoAtt.getValue().substring(1,
						infoAtt.getValue().indexOf(")"));
				if(number.contains("/"))
					number = number.substring(0, number.indexOf("/"));
				info = Float.parseFloat(number) ;
			}
			
		}
		return info;
	}

	private boolean isSubSet(FactorCombination superCom,
			ArrayList<String> subComName, ArrayList<String> subComValue) {
		// TODO Auto-generated method stub
		boolean isSubSet = false;
		if (subComName
				.equals(superCom.factorName.subList(0, subComName.size())))
			if (subComValue.equals(superCom.factorValue.subList(0, subComValue
					.size())))
				isSubSet = true;

		return isSubSet;
	}

	// Return a list of Object[]
	// Each Object[] contains two elements: the combination of factors, and how
	// many test cases are classified into this group
	public ArrayList<FactorCombination> getFailureRevealingCombinations(
			String treeInTxt) {

		ArrayList<FactorCombination> failureRevealingCombinations = new ArrayList<FactorCombination>();
		try {
			DOMParser parser = new DOMParser();
			parser.parse(treeInTxt);
			Document doc = parser.getDocument();
			NodeList outputNodes = doc.getElementsByTagName("Output");
			// <Output decision="..." info="(...)" />
			// NodeList nodes = doc.getChildNodes();
			for (int i = 0; i < outputNodes.getLength(); i++) {
				NamedNodeMap attrs = outputNodes.item(i).getAttributes();
				Attr decisionAtt = (Attr) attrs.item(0);
				Attr infoAtt = (Attr) attrs.item(1);
				String number = infoAtt.getValue().substring(1,
						infoAtt.getValue().indexOf(")"));

				if (number.equals("0.0"))
					continue;
				else if (!decisionAtt.getValue().equals("1")
						&& !infoAtt.getValue().contains("/"))
					continue;

				FactorCombination combination = new FactorCombination();
				Node item = outputNodes.item(i);
				String factor = "";
				String value = "";
				// A sequence of factors and their values
				// from leave to root (reverse order)
				while (item.getParentNode() != null) {
					Node testNode = item.getParentNode();

					if (testNode.getNodeName().equals("Test")) {
						NamedNodeMap testAttrs = testNode.getAttributes();
						for (int k = 0; k < testAttrs.getLength(); k++) {
							Attr attribute1 = (Attr) testAttrs.item(k);
							if (attribute1.getName().equals("attribute")) {
								factor = attribute1.getValue();
								combination.factorName.add(factor);
								combination.numberOfFactors++;
							} else if (attribute1.getName().equals("value")) {
								value = attribute1.getValue();
								combination.factorValue.add(value);
							}
						}
					}
					item = testNode;
				}

				if (decisionAtt.getValue().equals("1")) {
					// Catch a failure
					// failed/passed

					if (number.contains("/")) {
						float allTC = Float.parseFloat(number.substring(0,
								number.indexOf("/")));
						float pTC = Float.parseFloat(number.substring(number
								.indexOf("/") + 1, number.length()));
						combination.fTC = allTC - pTC;
						combination.pTC = pTC;
						combination.fRatio = combination.fTC / allTC;
						combination.pRatio = pTC / allTC;
					} else {
						combination.fTC = Float.parseFloat(number);
						combination.pTC = 0;
						combination.fRatio = 1;
						combination.pRatio = 0;
					}
				} else if (infoAtt.getValue().contains("/")) {
					// passed/failed
					// decision = 0, but there are exceptions

					float allTC = Float.parseFloat(number.substring(0, number
							.indexOf("/")));
					float fTC = Float.parseFloat(number.substring(number
							.indexOf("/") + 1, number.length()));
					combination.fTC = fTC;
					combination.pTC = allTC - fTC;
					combination.fRatio = fTC / allTC;
					combination.pRatio = combination.pTC / allTC;
				}

				// reverse the order of factor names and values
				ArrayList<String> nameInCorrectOrder = new ArrayList<String>();
				ArrayList<String> valueInCorrectOrder = new ArrayList<String>();
				for (int j = combination.numberOfFactors - 1; j >= 0; j--) {
					nameInCorrectOrder.add(combination.factorName.get(j));
					valueInCorrectOrder.add(combination.factorValue.get(j));
				}
				combination.factorName = nameInCorrectOrder;
				combination.factorValue = valueInCorrectOrder;
				failureRevealingCombinations.add(combination);
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return failureRevealingCombinations;
	}

	public static void main(String[] args) {
		XMLTreeAnalyzer e = new XMLTreeAnalyzer();
		e.printFailedCombInFolder(
				"C:\\MyWorkspace\\li-591T\\data\\xml-trees-run02",
				"C:\\MyWorkspace\\li-591T\\data\\failed-combinations-run02-1");
	}
}