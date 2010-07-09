
Randoop for API Mapping Tester:
===============================

This code base includes necessary changes in Randoop Java to generate test cases in the form of PUTs suitable for API Mapping testing. Hereby the tool name is referred to as RAPIMappingTester.
Below are the instructions for using the code base.

NOTE: It is harmful to the system to apply Randoop on system manipulation classes such System.setEnv(), since Randoop executes each generated test.

1. RAPIMappingTester uses environment variables to control the original Randoop. Two enviroment variables need to be set:

	a: "RANDOOP_METHOD_FILTER" -> Set this variable to the white list generated for Java2CSharp. An example format is given in "Java2Csharp_whitelist.txt"

	b: "GENERATE_PUTS" -> Set this variable to "true" to generate PUTs instead of JUnit tests, which are default of Randoop


2. Executing Randoop: The default command for running Randoop is given below. Please refer to documentation of Randoop for the definitions of the flags.

	java -classpath ./randoop.jar;./lib/jakarta-oro-2.0.8.jar;./lib/junit-4.3.1.jar;./lib/plume.jar randoop.main.Main gentests --classlist=InputClasses.txt --timelimit=5

	The file "InputClasses.txt" can be automatically generated from "Java2Csharp_whitelist.txt" using the following command. The input "Java2Csharp_whitelist.txt" is taken from the environment variable "RANDOOP_METHOD_FILTER".

	java -classpath ./randoop.jar;./lib/jakarta-oro-2.0.8.jar;./lib/junit-4.3.1.jar;./lib/plume.jar methodfilter.InpClassGenerator 
	
	A copy of the "InputClasses.txt", exclusing the unsafe classes is also available.

3. The number of generated tests can be controlled using the "timelimit" parameter.