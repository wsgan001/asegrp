using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CloneFinder;
using System.IO;

namespace AutomatonToCode
{
    class RandomTester
    {
        public static void SynthesizeTestsForSythesizedValidators(string ratioFile, string filename, string outputFolder)
        {
            StreamReader tr = new StreamReader(ratioFile);
            string ration_line = tr.ReadLine();
            HashSet<String> methodSet = new HashSet<String>();
            while (ration_line != null)
            {
                if (ration_line.StartsWith("Method") && !ration_line.StartsWith("MethodName") && !ration_line.StartsWith("Method_amount"))
                    methodSet.Add(ration_line.Substring(0, ration_line.IndexOf("\t")));
                ration_line = tr.ReadLine();
            }

            FileUtils fileUtils = new FileUtils();
            fileUtils.SetHowDeepToScan(1);
            fileUtils.ProcessDir(@"C:\RegExpTesting_workspace\AutomatonToCode\subjects\testable", 1, false, "", ".cs", null);
            List<string> subNames = fileUtils.GetFileNames();


            for (int i = 0; i < subNames.Count; i++)
            {
                string allInfo = subNames[i].Substring(subNames[i].LastIndexOf("\\") + 1, subNames[i].Length - subNames[i].LastIndexOf("\\") - 1);
                string namespaceName = "RegExpChecker" + allInfo.Substring(allInfo.IndexOf("_10_26_2008"), allInfo.IndexOf(".cs") - allInfo.IndexOf("_10_26_2008"));
                string className = namespaceName.Replace("RegExpChecker", "_1_1_20");
                string methodName = "Method" + className.Substring(className.LastIndexOf("_") + 1, className.Length - className.LastIndexOf("_") - 1);

                if (methodSet.Contains(methodName))
                {
                    TextReader tr2 = new StreamReader(filename);
                    String line = tr2.ReadLine();

                    StringBuilder sb = new StringBuilder();
                    sb.AppendLine("using " + namespaceName + ";\n");
                    sb.AppendLine("namespace " + namespaceName);
                    sb.AppendLine("{");
                    sb.AppendLine("public partial class " + className);
                    sb.AppendLine("{");

                    int count = 0;
                    while (line != null)
                    {
                        count++;
                        sb.AppendLine("public void " + methodName + "_" + count + "()");
                        sb.AppendLine("{");
                        sb.AppendLine("this." + methodName + "(\"" + line + "\");");
                        sb.AppendLine("}\n");
                        line = tr2.ReadLine();

                    }

                    sb.AppendLine("}");
                    sb.AppendLine("}");

                    // close the stream
                    tr2.Close();

                    Console.Out.WriteLine("Print " + allInfo);
                    FileUtils.WriteTxt(sb.ToString(), outputFolder + "\\" + allInfo);
                }
            }
            fileUtils.CleanFileNames();
        }

        public static void SynthesizeMainEntryForSythesizedValidators(string inputFolder, string outputFile, string entryClassName)
        {
            FileUtils fileUtils = new FileUtils();
            fileUtils.SetHowDeepToScan(1);
            fileUtils.ProcessDir(inputFolder, 1, false, "", ".cs", null);
            List<string> testNames = fileUtils.GetFileNames();

            StringBuilder sb_using = new StringBuilder();
            StringBuilder sb_body = new StringBuilder();

            sb_using.AppendLine("using System;");
            sb_body.AppendLine("namespace AutomatonToCode");
            sb_body.AppendLine("{");
            sb_body.AppendLine("class " + entryClassName);
            sb_body.AppendLine("{");
            sb_body.AppendLine("public static void InvokeAllRandomTests()");
            sb_body.AppendLine("{");


            for (int i = 0; i < testNames.Count; i++)
            {
                TextReader tr2 = new StreamReader(testNames[i]);
                String line = tr2.ReadLine();
                string className = "";
                while (line != null)
                {
                    if (line.StartsWith("using "))
                        sb_using.AppendLine(line);
                    else if (line.StartsWith("public partial class "))
                    {
                        className = line.Substring(line.IndexOf("_"), line.Length - line.IndexOf("_"));
                        sb_body.AppendLine(className + " " + className + "_object = new " + className + "();");
                    }
                    else if (line.StartsWith("public void "))
                    {
                        string methodName = line.Substring(line.IndexOf("Method"), line.LastIndexOf("(") - line.IndexOf("Method"));
                        sb_body.AppendLine("try");
                        sb_body.AppendLine("{");
                        sb_body.AppendLine(className + "_object." + methodName + "();");
                        sb_body.AppendLine("}");
                        sb_body.AppendLine("catch (Exception e)");
                        sb_body.AppendLine("{");
                        sb_body.AppendLine("Console.Out.WriteLine(e.Message);");
                        sb_body.AppendLine("}");
                    }
                    line = tr2.ReadLine();
                }
                Console.Out.WriteLine("Construct " + i + "/" + testNames.Count);
            }
            sb_body.AppendLine("}");
            sb_body.AppendLine("}");
            sb_body.AppendLine("}");

            FileUtils.WriteTxt(sb_using.ToString() + sb_body.ToString(), outputFile);
        }

        public static void SynthesizeTestsForOpenSourceValidators(string datafile, string outputFolder)
        {
            String[] namespaceList = {"com.talent.utils",
        "edu.harvard.hmdc.vdcnet.util",
        "com.duguo.dynamicmvc.model.validator",
        "com.dreams.united.client",
            "com.axiomos.util",
            "net.sf.valjax.validator",
            "net.sf.hippopotam.presentation.field.validator",
            "com.axiomos.util",
            "de.x4technology.validator",
            "com.arcmind.jsfquickstart.validation",
            "org.tigris.atlas.validate"
        };

            string[] classList ={"EmailValidator",
                                  "EmailValidator",
                                  "EmailValidator",
                                  "EmailValidator",
                              "EmailValidator",
                              "FaxValidator",
                              "PhoneValidator",
                              "UrlValidator",
                              "UrlValidator",
                              "ZipCodeValidator",
                              "ZipCodeValidator"};

            string[] methodList ={"validateEmailAddress_original",
                                  "validateEmail_original",
                                  "validate_original",
                                  "validate",
                              "isValid",
                              "validate",
                              "validate",
                              "isValid_original",
                              "isValid_original",
                              "validate",
                              "isValid"};


            for (int i = 0; i < namespaceList.Length; i++)
            {
                //string allInfo = subNames[i].Substring(subNames[i].LastIndexOf("\\") + 1, subNames[i].Length - subNames[i].LastIndexOf("\\") - 1);
                string namespaceName = namespaceList[i];
                string className = classList[i];
                string methodName = methodList[i];


                TextReader tr2 = new StreamReader(datafile);
                    String line = tr2.ReadLine();

                    StringBuilder sb = new StringBuilder();
                    sb.AppendLine("using " + namespaceName + ";\n");
                    sb.AppendLine("namespace " + namespaceName);
                    sb.AppendLine("{");
                    sb.AppendLine("public partial class " + className);
                    sb.AppendLine("{");

                    int count = 0;
                    while (line != null)
                    {
                        count++;
                        sb.AppendLine("public void " + methodName + "_" + count + "()");
                        sb.AppendLine("{");
                        sb.AppendLine("this." + methodName + "(\"" + line + "\");");
                        sb.AppendLine("}\n");
                        line = tr2.ReadLine();

                    }

                    sb.AppendLine("}");
                    sb.AppendLine("}");

                    // close the stream
                    tr2.Close();

                    Console.Out.WriteLine("Print " + namespaceList[i]);
                    FileUtils.WriteTxt(sb.ToString(), outputFolder + "\\" + namespaceList[i] + ".cs");
                
            }
        }

            
        public static void SynthesizeMainEntryForOpenSourceValidators(string inputFolder, string outputFile, string entryClassName)
        {
            FileUtils fileUtils = new FileUtils();
            fileUtils.SetHowDeepToScan(1);
            fileUtils.ProcessDir(inputFolder, 1, false, "", ".cs", null);
            List<string> testNames = fileUtils.GetFileNames();

            StringBuilder sb_using = new StringBuilder();
            StringBuilder sb_body = new StringBuilder();

            sb_using.AppendLine("using System;");
            sb_body.AppendLine("namespace AutomatonToCode");
            sb_body.AppendLine("{");
            sb_body.AppendLine("class " + entryClassName);
            sb_body.AppendLine("{");
            sb_body.AppendLine("public static void InvokeAllRandomTests()");
            sb_body.AppendLine("{");


            for (int i = 0; i < testNames.Count; i++)
            {
                TextReader tr2 = new StreamReader(testNames[i]);
                String line = tr2.ReadLine();
                string className = "";
                string namespaceName = "";
                while (line != null)
                {
                    if (line.StartsWith("using "))
                    {
                        sb_using.AppendLine(line);
                    }
                    else if (line.Contains("namespace"))
                    {
                        namespaceName = line.Substring(line.Trim().IndexOf(" ")+1, line.Trim().Length - line.Trim().IndexOf(" ")-1);
                    }
                    else if (line.StartsWith("public partial class "))
                    {
                        className = line.Substring(line.LastIndexOf(" ") + 1, line.Length - line.LastIndexOf(" ") - 1);
                        sb_body.AppendLine(namespaceName + "." + className + " " + className + "_object" +i+ " = new " + namespaceName + "." + className + "();");
                    }
                    else if (line.StartsWith("public void "))
                    {
                        string methodName = line.Substring(line.IndexOf("void ") + 5, line.LastIndexOf("(") - line.IndexOf("void ") - 5);
                        sb_body.AppendLine("try");
                        sb_body.AppendLine("{");
                        sb_body.AppendLine(className + "_object" + i + "." + methodName + "();");
                        sb_body.AppendLine("}");
                        sb_body.AppendLine("catch (Exception e)");
                        sb_body.AppendLine("{");
                        sb_body.AppendLine("Console.Out.WriteLine(e.Message);");
                        sb_body.AppendLine("}");
                    }
                    line = tr2.ReadLine();
                }
                Console.Out.WriteLine("Construct " + i + "/" + testNames.Count);
            }
            sb_body.AppendLine("}");
            sb_body.AppendLine("}");
            sb_body.AppendLine("}");

            FileUtils.WriteTxt(sb_using.ToString() + sb_body.ToString(), outputFile);
        }

            
       

    }
}
