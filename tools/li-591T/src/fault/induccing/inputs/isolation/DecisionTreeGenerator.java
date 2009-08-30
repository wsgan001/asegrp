package fault.induccing.inputs.isolation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class DecisionTreeGenerator {

	public void generateForFolder(String arffFolder, String resultFolder)
			throws Exception {
		File dir = new File(arffFolder);
		File[] folders = dir.listFiles(); // folders v1 .. v41
		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		folders = dir.listFiles(fileFilter);

		for (int i = 0; i < folders.length; i++) {
			// Get filename of file or directory
			File subFolderName = folders[i];
			String[] files = subFolderName.list();
			if (files == null) {
				// Either dir does not exist or is not a directory
				System.err.println("File doesn't exist");
			} else {
				for (int j = 0; j < files.length; j++) {
					// Get filename of file or directory
					String filename = files[j];
					if (filename.startsWith("v")) {
						// Generate Decision Tree
						this.generateDecisionTree(subFolderName
				.getPath()
				+ "\\" + filename, resultFolder
				+ "\\" + filename + ".txt");
						
					}
				}
			}
		}
	}

	private void generateDecisionTree(String arffFile, String outputFile) throws Exception {
		// Generate a tree for a arff file
		DataSource source = new DataSource(arffFile);
		Instances data = source.getDataSet();
		// setting class attribute if the data format does not
		// provide this
		// information
		// E.g., the XRFF format saves the class attribute
		// information as well
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);

		String[] options = new String[1];
		options[0] = "-U"; // unpruned tree
		J48 tree = new J48(); // new instance of tree

		tree.setOptions(options); // set the options
		tree.buildClassifier(data); // build classifier

		// Output tree into a txt file -- for debug
		try {
			// Create file
			FileWriter fstream = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fstream);
			String treeInTxt = tree.toString();
			if(treeInTxt.startsWith("J48 unpruned tree")){
				String signal = "------------------";
				treeInTxt = treeInTxt.substring(treeInTxt.indexOf(signal)+signal.length());
			}
			if(treeInTxt.contains("Number of Leaves  : ")&&treeInTxt.contains("Size of the tree : ")){
				String signal = "Number of Leaves  : ";
				treeInTxt = treeInTxt.substring(0, treeInTxt.indexOf(signal));
			}
			out.write(treeInTxt.trim());
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
		// TODO Auto-generated method stub
		DecisionTreeGenerator generator = new DecisionTreeGenerator();
		
//		try {
//			generator.generateDecisionTree("C:\\MyWorkspace\\li-591T\\data\\sample_weather\\weather.arff", "C:\\MyWorkspace\\li-591T\\data\\sample_weather\\weather.txt");
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		try {
			generator.generateForFolder
					(
							"C:\\MyWorkspace\\li-591T\\data\\arff-inputs-run02",
							"C:\\MyWorkspace\\li-591T\\data\\weka-results-run02");
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		
	}
 
}
