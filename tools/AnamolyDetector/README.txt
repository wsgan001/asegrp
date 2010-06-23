
This code base belongs to the ASE Journal 2010 paper, which is an extension of ASE 2009 paper.
Actual code base of ASE 2009 paper is available in the ZIP file: AnamolyDetector_CodeBaseInASE2009Paper.zip

This file describes how to execute the code and the necessary inputs.


1. The entire tool is controlled by the properties file "AlattinProperties.txt". A sample file is available in the root folder of the code base. This file should exist in the directory described by the environment variable "ALATTIN_PATH".

2. Anamoly Detector can be used in two different modes: 
   
   2.1 As an eclipse plugin: This works only with the Eclipse version: 3.1.2. In this mode, run the AnamolyDetector project and give the following values in the properties file. This mode automatically mines patterns and detects violations of these patterns. The output of the entire process is places in the directory set using "InputPatternDirs" as follows.
	
	OperationMode = 1
	InputPatternDirs = D:\\eclipse-SDK-3.1.2-win32\\eclipse;
	
	Below are the steps:
	a. In this mode, first the entire data required for mining patterns is generated. The data is placed in folders: AssocMiner_IDs and AssocMiner_Data
	b. The outputs are mined patterns and detected defects. For example: "OR_PATTERN_Patterns.csv" shows the patterns mined in OR pattern format and "MinedBugs_OR_PATTERN.csv" shows the defects detected by those patterns in the application.

   2.2 As a console application: This mode is primarily used to mine patterns offline from the data already collected from code examples. The parameter values that need to be set for this mode are as follows:

	OperationMode = 0
	InputPatternDirs = D:\\NCSUASE\\MyPublicationMaterials\\Alattin_ASEJ2010\\NegExampleLearner\\JavaUtil_WithOldData;

	Below are the steps:
	a. Multiple directories can also be given for the option: InputPatternDirs
	b. The data folder should contain the following structure:
		
		AssocMethodIds.txt: All method Ids for the API methods of interest
		AssocMinerData.txt: The names of those methods
		AssocMiner_Data/  : Data corresponding to each API method
		AssocMiner_IDs/   : IDs of each element used in the data folder