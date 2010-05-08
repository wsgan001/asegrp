
Basic information for re-running XWeb (CARMiner) tool:
======================================================
1. The tool operates in two modes:
	a. Mining patterns
	b. Detecting defects

	Currently, the modes are controlled using the XWeb.Properties file.


Here are the high-level steps for running XWeb:

1. Set the environment variable "XWeb_Path" to the folder where the source files downloaded from the web for several API classes are stored.
2. Run the Jex tool on the target input application to generate a file called "Jex_Exceptions.txt". This file is mandatory for mining
	patterns as it helps to avoid irrelevant edges by identifying the potential exceptions thrown by an API.

	TODO: To write more of how to do this part.

2. Set the mode to mining patterns by altering the value of the variable "XWeb_User_Mode" in  XWeb.properties file.
3. Run the "XWeb" application, which generated following files:

	a. PackageList.txt: This is for the Jex tool for generating Jex_Exceptions file, which is a mandatory file for mining patterns.
	b. AssocMethodIds.txt: A file that gives a unique ID for each API method. Each entry is of the form:

		33 : java.io.BufferedReader:CONSTRUCTOR(java.io.FileReader::):java.io.BufferedReader

	c. AssocMinerData.txt: Contains all details of collected patterns. Primarily useful for debugging purposes. Entries are of the form below:

		MethodInvocation : java.io.BufferedReader:CONSTRUCTOR(java.io.FileReader::):java.io.BufferedReader (33) 

		Count 0  1003_SortList.java,sortByOrderPropertyFile
		Normal:
			java.io.FileReader:CONSTRUCTOR(java.io.File::):java.io.FileReader (70787) 
			java.io.BufferedReader:CONSTRUCTOR(java.io.FileReader::):java.io.BufferedReader (70788) 

		Error:
			java.io.FileReader:close():void (70789) 

	
	d. Individual Data for each API method in "AssocMethodIds.txt" is found in folders "AssocMiner_IDs" and "AssocMiner_Data".
		AssocMiner_IDs/<MethodId>.txt : Contains method ids for all related methods of this API method.
		AssocMiner_Data/<MethodId>.txt : Contains sequences related to each API method.
		
4. Open Cygwin and run "PostProcess_Main.pl". This can be run using "frequent sub-sequence miner" or "frequent item-set miner" using the mode flag set inside the perl script. 
