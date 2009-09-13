

Concurrency Miner Project (CMMiner):
===================================

CMMiner project currently contains a base framework that accepts a set of APIs and various relevant code
examples for these APIs. CMMiner analyzes these code examples and generates traces that include information about
several condition checks that should be performed before and after the API call.

CMMiner can be run only from the Eclipse IDE as CMMiner uses Eclipse JDT that donot allow to run from command prompt.

Requirements:
=============
CMMiner requires Eclipse JDT plugin. CMMiner is currently tested with Eclipse 3.2.1.

How to use CMMiner?
===================
A working example is provided in the folder "WorkingExample". Assume that the "WorkingExample" folder is stored in "C:\WorkingExample"

Inputs:
	a. APIList.txt: List on input APIs. Each row contains a class name and its methods. Below is the format of the file.

		java.util.Iterator#next()#hasNext()#
		java.util.Stack#push(java.lang.Object)#pop()#
	
	b. RelevantCodeSamples: A directory where relevant code samples are stored. This contains several directories, 
	one for each class. The directory name of relevant code samples for the class "java.util.Iterator" is "java_util_Iterator".

	c. Run CMMiner using the following command.

		CMMiner <APIList> <Directory Location>

		Example: CMMiner C:\WorkingExample\APIList.txt C:\WorkingExample\RelevantCodeSamples

	d.  CMMiner generates following files:

		AssocMethodIds.txt: Unique method id for each method in APIList.txt.
		AssocMinerData.txt: Log file including all trace information. Only for debugging purposes.
		<AssocMiner_IDs> : Unique IDs assigned within the traces of each method in APIList.txt.
		<AssocMiner_Data>: Actual traces related to each method in APIList.txt.

	e.  Perl scripts are provided to analyze these traces and produce patterns. Copy the scripts (PostProcess_Main.pl, PostProcess.pl) and the underlying
	frequent itemset miner (mafia.exe) to location where the above information is generated and run the following command:

		"perl PostProcess_Main.pl"

	d.  The perl scripts generates an output file "ConsolidatedOutput.csv", that contains mined patterns. For example, one mined pattern of
	the working example is "0,java.util.Iterator:next(),0,0,java.util.Iterator:hasNext()#RETURN_PATTERNS#TRUE_FALSE_CHECK(0)&&,22,1,0,1".
	
		This pattern describes that "Iterator.next() should be preceded by a boolean check on the return value of java.util.Iterator:hasNext()#RETURN_PATTERNS#TRUE_FALSE_CHECK".


Codebase of CMMiner:
====================

1. CMMiner can be run either in a command prompt mode or as an eclipse plugin. This document mainly explains about the code bases related to running in command prompt
mode.

2. Below are the packages and their purposes:
	1. cm.common: Contains static and final constants referred in the entire code bases.
	2. cm.inpapp.holder: Code related to using CMMiner in plugin mode. This package contains classes for holding classes and methods declared by the input application.
	3. cm.inpapp.parser: Code related to using CMMiner in plugin mode. This package includes source code for parsing the input application.
	4. cm.popup.actions: 
		a. Activator.java: Code related to plugin mode
		b. AnamolyDetector.java: Initiates the analysis process from the method "startProcess".
		c. APIAnalyzer.java: Main file for running CMMiner in command line mode. Includes the main method. 
		d. RepositoryAnalyzer.java: Iterates over directory structure of code samples and invokes "GCodeAnalyzer.analyze" for analyzing each directory.
	5. cm.samples.analyzer:
		a. ASTCrawler.java: Visitor pattern for trasforming AST into a CFG.
		b. ASTCrawlerUtil.java: Contains miscellanous functions, used by ASTCrawler.
		c. CodeExampleStore.java: Class for storing code examples.
		f. GCodeAnalyzer.java: Iterates over each code sample stored in directories and invokes ASTCrawler for further analysis.
		g. TypeHolder.java: Holder receiver, argument, or return objects of a method invocation.
	
	6. cm.samples.analyzer.holder: Contains several holders for holding different constructs. Only a few important classes are described below.
		a. MethodInvocationHolder.java : Main class that stores entire information associated with a method invocation
		b. CondVarHolder.java: Holder a condition check collected from code example.
		c. PrePostPathHolder.java: Stores a single trace collected from code example.
