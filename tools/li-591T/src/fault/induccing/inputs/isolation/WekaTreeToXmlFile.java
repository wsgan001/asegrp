package fault.induccing.inputs.isolation;

import java.io.File;

public class WekaTreeToXmlFile {

	public void txtToXmlForFolder(String txtFolder, String xmlFolder)
			throws Exception {
		File dir = new File(txtFolder);

		String[] files = dir.list();
		if (files == null) {
			// Either dir does not exist or is not a directory
			System.err.println("File doesn't exist");
		} else {
			for (int j = 0; j < files.length; j++) {
				// Get filename of file or directory

				String filename = files[j];
				if (filename.endsWith(".txt")) {
					// Generate Decision Tree
					String arg1 = dir.getPath() + "\\" + filename;
					String arg2 = xmlFolder + "\\" + filename + ".xml";
					org.inra.qualscape.wekatexttoxml.WekaTextfileToXMLTextfile wt2xt = new org.inra.qualscape.wekatexttoxml.WekaTextfileToXMLTextfile(
							new File(arg1), new File(arg2), false, false);
					try{
						wt2xt.writeXmlFromWekaText();
					}
					catch(Exception e){
						System.err.println(e.getMessage());
						continue;
					}
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WekaTreeToXmlFile e = new WekaTreeToXmlFile();
		try {
			 e.txtToXmlForFolder("C:\\MyWorkspace\\li-591T\\data\\weka-results-run02",
			 "C:\\MyWorkspace\\li-591T\\data\\xml-trees-run02");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
