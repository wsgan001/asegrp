using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Xml.Xsl;
using System.Xml;
using Microsoft.Pex.Framework;
using System.Collections;
using CloneFinder;
//using AutomatonToCode.subjects.CaseStuty;
//using AutomatonToCode.issta09_experiments;

namespace AutomatonToCode
{

    public partial class AutomatonToCode
    {
        static void Main(string[] args)
        {
            //-------------Construct tested methods from regular expressions downloaded from regexlib.com--------------
            //string key = "";
            //string type = "1";
            //int rank = 1;
            //int row = 20;
            //string regex_path = @"C:\RegExpTesting_workspace\AutomatonToCode\regexp\" + key + "_" + type + "_" + rank + "_" + row;
            //string subject_path = @"C:\RegExpTesting_workspace\AutomatonToCode\subjects\" + key + "_" + type + "_" + rank + "_" + row; 

            //RegExpToTestedMethod fetch = new RegExpToTestedMethod();
            ////Catch regular expressions from http://regexlib.com/, save as XML files
            //fetch.PrintRegExpsToFile(key, type, rank, row, regex_path + ".xml");
            ////Extract regexp from XML file, write in txt file
            //fetch.ExtractElementsFromXML("Expression", regex_path + ".xml", regex_path + ".txt");
            ////Synthesize a method for each regexp
            //fetch.SUTSynthesis(regex_path + ".txt", subject_path);
            //-----------------------------------------------------------------------------------------------------------------------


            //------------Synthesize c# code for automata------------------------------------------------------------
            string _xsltfilename = @"c:\myworkspace\li-regexpworkspace-csharp\xml_to_code.xsl";
            StringBuilder sb = new StringBuilder();
            //directoryinfo di = new directoryinfo(@"c:\myworkspace\li-regexpworkspace-csharp\inject.faults\automaton.xml");
            DirectoryInfo di = new DirectoryInfo(@"C:\MyWorkspace\li-ASE09\old14subjects\issta09-experiments\mutatedAuto\PM_692");            
            FileInfo[] rgfiles = di.GetFiles("*.xml");
            foreach (FileInfo fi in rgfiles)
            {
                string _xmlfilename = di + "\\" + fi.Name;
                string _resultfilename = _xmlfilename.Replace(".xml", ".cs");
                //_resultfilename = _resultfilename.replace(".xml", ".cs");
                using (Stream xsltdata = new FileStream(_xsltfilename, FileMode.Open))
                {
                    XslCompiledTransform tr = new XslCompiledTransform();
                    tr.Load(new XmlTextReader(xsltdata));
                    try
                    {
                        tr.Transform(_xmlfilename, _resultfilename);
                    }
                    catch (Exception e)
                    {
                        System.Console.WriteLine(e.Message);
                        System.Console.WriteLine(_xmlfilename);
                        sb.Append(e.Message + "\n\r");
                        sb.Append(_xmlfilename + "\n\r");
                        continue;
                    }
                }
            }

             //FileUtils.WriteTxt(sb.ToString(), @"C:\RegExpTesting_workspace\AutomatonToCode\automaton.cs\XML_CSharp_failedlist.txt");
            FileUtils.WriteTxt(sb.ToString(), @"C:\MyWorkspace\li-ASE09\old14subjects\issta09-experiments\mutatedAuto\XML_CSharp_failedlist.txt");

            //-----------------------------------------------------------------------------------------------------------------------

            //-------Select untestable subjects------------------                      
            //DirectoryInfo csfolder = new DirectoryInfo(@"C:\RegExpTesting_workspace\AutomatonToCode\automaton.cs");
            //FileInfo[] rgFiles = csfolder.GetFiles("Auto__*.cs");
            //foreach (FileInfo fi in rgFiles)
            //{
            //    string testablefile = @"C:\RegExpTesting_workspace\AutomatonToCode\subjects\testable\" + fi.Name.Substring(5, fi.Name.Length - 5);
            //    File.Delete(testablefile);
            //    File.Copy(@"C:\RegExpTesting_workspace\AutomatonToCode\subjects\" + fi.Name.Substring(5, fi.Name.Length - 5), testablefile);
            //}
            //---------------------------------------------------

            //------------Calculate coverage diff & get generated-tests number----------------
            //ReportAnalyzor ra = new ReportAnalyzor();
            //ra.CompareTwoCoverageLists(@"C:\RegExpTesting_workspace\AutomatonToCode\bin\Debug\reports\AutomatonToCode.81027.224448.pex\coverage", @"C:\RegExpTesting_workspace\AutomatonToCode\bin\Debug\reports\AutomatonToCode.81028.023513.pex\coverage", @"C:\RegExpTesting_workspace\AutomatonToCode\ratio.txt");
            //ra.CountTestNumber(@"C:\RegExpTesting_workspace\AutomatonToCode\bin\Debug\reports\AutomatonToCode.81028.023513.pex\report.html", @"C:\RegExpTesting_workspace\AutomatonToCode\ratio.txt", @"C:\RegExpTesting_workspace\AutomatonToCode\81028.023513.tests.num.txt");
            //ra.CountTestNumber(@"C:\RegExpTesting_workspace\AutomatonToCode\bin\Debug\reports\AutomatonToCode.81027.224448.pex\report.html", @"C:\RegExpTesting_workspace\AutomatonToCode\ratio.txt", @"C:\RegExpTesting_workspace\AutomatonToCode\81027.224448.tests.num.txt");
            //ra.SelectTests(@"C:\RegExpTesting_workspace\AutomatonToCode\bin\Debug\reports\AutomatonToCode.81027.224448.pex\tests", @"C:\RegExpTesting_workspace\AutomatonToCode\ratio.txt", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81027.224448.pex");
            //ra.SelectTests(@"C:\RegExpTesting_workspace\AutomatonToCode\bin\Debug\reports\AutomatonToCode.81028.023513.pex\tests", @"C:\RegExpTesting_workspace\AutomatonToCode\ratio.txt", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81028.023513.pex");
            //ra.GetAutomataCoverage(@"C:\RegExpTesting_workspace\AutomatonToCode\ratio.txt", @"C:\RegExpTesting_workspace\AutomatonToCode\bin\Debug\reports\AutomatonToCode.81028.023513.pex\coverage", @"C:\RegExpTesting_workspace\AutomatonToCode\auto_cov_81028.023513.txt");
            //---------------------------------------------------

            //------------Run test with NCover-------------------
            //NCoverEntry.SynthesizeMainForOpenSource(@"C:\MyWorkspace\li-RegExpWorkspace-CSharp\issta09-experiments\tests\withAuto", @"C:\MyWorkspace\li-RegExpWorkspace-CSharp\issta09-experiments\tests\withAuto\mainEntrySeg.txt");
            //NCoverEntry nce = new NCoverEntry();
            //nce.InvokeTests_81027_224448();


            //---------------------------------------------------

            //NCoverReportAnalysis ncra = new NCoverReportAnalysis();
            //           //ncra.CompareTwoCoverageLists_AutoClass(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81027.224448.pex\ncover_report.html\classes.html",
            //           //    @"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81028.023513.pex\ncover_report.html\classes.html",
            //           //    @"C:\RegExpTesting_workspace\AutomatonToCode\NCover_ratio.txt");
            //           //ncra.CompareTwoCoverageLists_AutoMethod(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81027.224448.pex\ncover_report.html\files",
            //                  //@"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81028.023513.pex\ncover_report.html\files",
            //                  //@"C:\RegExpTesting_workspace\AutomatonToCode\NCover_AutoMethod_ratio.txt");
            //ncra.CompareTwoCoverageLists_TestedMethod(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81027.224448.pex\ncover_report.html\files",
            //        @"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81028.023513.pex\ncover_report.html\files",
            //        @"C:\RegExpTesting_workspace\AutomatonToCode\NCover_TestedMethod_ratio.txt");

            //--------------------------Instrument Automata-------------------------

            //AutomatonInstrumentation ai = new AutomatonInstrumentation();
            //ai.InstrumentAllAcceptCharInFolder(@"C:\MyWorkspace\li-RegExpWorkspace-CSharp\issta09-experiments\automaton.cs");
            //ai.InstrumentAllIsMatchInFolder(@"C:\MyWorkspace\li-RegExpWorkspace-CSharp\issta09-experiments\automaton.cs");
 
            //ncra.CompareTwoCoverageLists_NodeEdge(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81027.224448.pex\AutomataCoverage",
 //@"C:\RegExpTesting_workspace\AutomatonToCode\tests\AutomatonToCode.81028.023513.pex\AutomataCoverage",
 //@"C:\RegExpTesting_workspace\AutomatonToCode");

            //----extract tests for the tested open source validators--------------------------
            //FileUtils fileUtils = new FileUtils();
            //fileUtils.SetHowDeepToScan(10);
            //fileUtils.ProcessDir(@"C:\PexValidatorTest\tests-java\JavaValidators\tests\JavaValidators.81102.002249.pex\tests", 1, true, "", ".cs", null);
            //List<string> fileNames = fileUtils.GetFileNames();

            //foreach (string fi in fileNames)
            //{
            //    File.Copy(fi, @"C:\PexValidatorTest\tests-java\JavaValidators\tests\JavaValidators.81102.002249.pex\" + fi.Substring(fi.LastIndexOf("\\") + 1, fi.Length - fi.LastIndexOf("\\") - 1));
            //}

            //SynthesizeMainForOpenSource(@"C:\PexValidatorTest\tests-java\JavaValidators\tests\JavaValidators.81102.002249.pex", @"C:\PexValidatorTest\tests-java\JavaValidators\tests\JavaValidators.81102.002249.pex\mainEntrySeg.txt");

            // RandomString.RandomStrings_settable(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.string.set.500.txt", 500, 0, 24);
            // RandomTester.SynthesizeTestsForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\experimental_data_synthesis\ratio.txt", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.string.set.500.txt", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.1");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.1", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.1\RandomEntryForSynthesizedValidators_1.cs", "RandomEntryForSynthesizedValidators_1");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.2", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.2\RandomEntryForSynthesizedValidators_2.cs", "RandomEntryForSynthesizedValidators_2");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.3", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.3\RandomEntryForSynthesizedValidators_3.cs", "RandomEntryForSynthesizedValidators_3");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.4", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.4\RandomEntryForSynthesizedValidators_4.cs", "RandomEntryForSynthesizedValidators_4");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.5", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.5\RandomEntryForSynthesizedValidators_5.cs", "RandomEntryForSynthesizedValidators_5");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.6", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.6\RandomEntryForSynthesizedValidators_6.cs", "RandomEntryForSynthesizedValidators_6");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.7", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.7\RandomEntryForSynthesizedValidators_7.cs", "RandomEntryForSynthesizedValidators_7");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.8", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.test.synthesized.validators.8\RandomEntryForSynthesizedValidators_8.cs", "RandomEntryForSynthesizedValidators_8");
            //RandomTester.SynthesizeMainEntryForSythesizedValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\small.1.1", @"C:\RegExpTesting_workspace\AutomatonToCode\tests\small.1.1\Small_1_1.cs", "Small_1_1");
            //Small_1_1.InvokeAllRandomTests();
            // RandomEntryForSynthesizedValidators_1.InvokeAllRandomTests();
            // RandomEntryForSynthesizedValidators_2.InvokeAllRandomTests();
            // RandomEntryForSynthesizedValidators_3.InvokeAllRandomTests();
            //  RandomEntryForSynthesizedValidators_4.InvokeAllRandomTests();
            //RandomEntryForSynthesizedValidators_5.InvokeAllRandomTests();
            //RandomEntryForSynthesizedValidators_6.InvokeAllRandomTests();
            //RandomEntryForSynthesizedValidators_7.InvokeAllRandomTests();
            //RandomEntryForSynthesizedValidators_8.InvokeAllRandomTests();

            //RandomTester.SynthesizeTestsForOpenSourceValidators(@"C:\RegExpTesting_workspace\AutomatonToCode\tests\random.string.set.500.txt", @"C:\PexValidatorTest\tests-java\JavaValidators\tests\random");
            // RandomTester.SynthesizeMainEntryForOpenSourceValidators(@"C:\PexValidatorTest\tests-java\JavaValidators\tests\random", @"C:\PexValidatorTest\tests-java\JavaValidators\tests\random\RandomEntryForOpenSourceValidators.cs", "RandomEntryForSynthesizedValidators");

            //TestsInvoker.WithoutAuto();
            
        }


    }
}

