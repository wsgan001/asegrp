using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Collections;
using CloneFinder;
using System.Text.RegularExpressions;

namespace AutomatonToCode
{
    class ReportAnalyzor
    {
        public string[] ExtractCoverageInfo(string filename)
        {
            string coverage = "";
            string method = "";
            string[] ht = { method, coverage };
            StreamReader tr = new StreamReader(filename);
            string line = tr.ReadLine();

            while (line != null)
            {
                if (line.StartsWith("<method name=\"Method"))
                {
                    method = line.Substring(line.IndexOf("name=\"") + 6, line.IndexOf("\" type=") - line.IndexOf("name=\"") - 6);
                }
                else if (line.StartsWith("<coverage "))
                {
                    coverage = line.Substring(line.LastIndexOf("=") + 2, line.LastIndexOf("\"") - line.LastIndexOf("=") - 2);
                    ht[0] = method;
                    ht[1] = coverage;
                    break;
                    //only the 1st coverage is for MethodX
                }
                line = tr.ReadLine();
            }
            return ht;
        }

        public Hashtable GetCoverageList(string foldername)
        {
            Hashtable ht = new Hashtable();

            FileUtils fileutil = new FileUtils();
            fileutil.SetHowDeepToScan(2);
            fileutil.ProcessDir(foldername, 1, true, "_", ".xml", null);
            List<String> fileNames = fileutil.GetFileNames();

            for (int i = 0; i < fileNames.Count; i++)
            {
                ReportAnalyzor ra = new ReportAnalyzor();
                string[] method_ratio = ra.ExtractCoverageInfo(fileNames[i]);
                ht.Add(method_ratio[0], (float)System.Convert.ToSingle(method_ratio[1]));
                Console.WriteLine(i + "/" + fileNames.Count + "     MethodName: " + method_ratio[0] + "  BlockCoverage: " + (float)System.Convert.ToSingle(method_ratio[1]));
            }
            fileutil.CleanFileNames();
            return ht;
        }

        public void CompareTwoCoverageLists(string foldername1, string foldername2, string outputfile)
        {
            Hashtable ht1 = GetCoverageList(foldername1);
            Hashtable ht2 = GetCoverageList(foldername2);
            StringBuilder sb = new StringBuilder();
            sb.Append("MethodName\t" + foldername1.Substring(foldername1.IndexOf("AutomatonToCode."), foldername1.LastIndexOf("\\") - foldername1.IndexOf("AutomatonToCode.")) + "\t" + foldername2.Substring(foldername2.IndexOf("AutomatonToCode."), foldername2.LastIndexOf("\\") - foldername2.IndexOf("AutomatonToCode.")) + "\tDiff: Col1-Col2" + " \n");
            IDictionaryEnumerator _enumerator = ht1.GetEnumerator();
            int count = 0;
            float ht1_sum = 0;
            float ht2_sum = 0;

            while (_enumerator.MoveNext())
            {
                if (ht2.ContainsKey(_enumerator.Key))
                {
                    count++;
                    ht1_sum = ht1_sum + (float)_enumerator.Value;
                    ht2_sum = ht2_sum + (float)ht2[_enumerator.Key];
                    sb.Append(_enumerator.Key + "\t" + _enumerator.Value + "\t" + ht2[_enumerator.Key] + "\t" + ((float)_enumerator.Value - (float)ht2[_enumerator.Key]) + "\n");
                }
            }

            sb.Append("Sum\t" + ht1_sum + "\t" + ht2_sum + "\n");
            sb.Append("Ave\t" + ht1_sum / count + "\t" + ht2_sum / count + "\n");
            sb.Append("Diff_sum(col1-col2)\t" + ((float)ht1_sum - (float)ht2_sum).ToString() + "\n");
            sb.Append("Diff_ave(col1-col2)\t" + ((float)ht1_sum / count - (float)ht2_sum / count).ToString() + "\n");
            sb.Append("Method_amount\t" + count + "\n");

            FileUtils.WriteTxt(sb.ToString(), outputfile);
        }

        public void CountTestNumber(string reportFile, string ratioFile, string outputfile)
        {
            StreamReader tr = new StreamReader(ratioFile);
            string line = tr.ReadLine();
            HashSet<String> methodSet = new HashSet<String>();
            while (line != null)
            {
                if (line.StartsWith("Method") && !line.StartsWith("MethodName") && !line.StartsWith("Method_amount"))
                    methodSet.Add(line.Substring(0, line.IndexOf("\t")));
                line = tr.ReadLine();
            }

            StringBuilder sb = new StringBuilder();
            sb.Append("MethodName\tTestNumber\tDuration\n");
            int sum = 0;
            int methodcount = 0;
            double msec = 0;
            double sec = 0;
            double min = 0;
            double hour = 0;     
            tr = new StreamReader(reportFile);
            line = tr.ReadLine();
            
            while (line != null)
            {
                if (!line.Contains("<td>Method"))
                {
                    line = tr.ReadLine();
                    continue;
                }
                if (Regex.IsMatch(line, @".*<td>Method\d{1,3}\(String\).*"))
                {
                    string method = line.Substring(line.IndexOf("<td>M") + 4, line.IndexOf("(String)") - line.IndexOf("<td>M") - 4);
                    if (methodSet.Contains(method))
                    {                        
                        while (line != null)
                        {
                            line = tr.ReadLine();
                            if (Regex.IsMatch(line, @".*<td>\d\d:\d\d:\d\d\.\d\d</td><td>.*"))
                            {
                                string[] sges = Regex.Split(line, @"<td>\d\d:\d\d:\d\d\.\d\d</td><td>", RegexOptions.IgnoreCase);
                                //sges.count must be 2                                
                                string testNum = sges[1];
                                testNum = testNum.Substring(0, testNum.IndexOf(","));
                                string duration = line.Substring(sges[0].Length + 4, 11);
                                msec = msec + Double.Parse(duration.Substring(9,2));
                                sec = sec + Double.Parse(duration.Substring(6,2)); 
                                min = min + Double.Parse(duration.Substring(3,2));
                                hour = hour + Double.Parse(duration.Substring(0,2));      
                                sb.Append(method + "\t" + testNum + "\t" + duration + "\n");
                                methodcount++;
                                sum = sum + Int32.Parse(testNum);
                                Console.Out.WriteLine(method + "\t" + testNum );
                                methodSet.Remove(method);
                                break;
                            }                                                        
                        }
                    }

                }
                line = tr.ReadLine();
            }
            
            sec = sec + (Int32)(msec / 100);
            msec = msec % 100;
            min = min + (Int32)(sec / 60);
            sec = sec % 60;           
            hour = hour + (Int32)(min / 60);
            min = min % 60;
            sb.Append("Sum: " + sum + "\n");
            sb.Append("MethodTotal: " + methodcount + "\n");
            sb.Append("Total duration: " + hour + ":" + min + ":" + sec + "." + msec + "\n");
            sb.Append("Left:" + "\n");
            for (int i = 0; i < methodSet.Count; i++)
            {
                sb.Append(methodSet.ElementAt(i) + "\n");
            }
                FileUtils.WriteTxt(sb.ToString(), outputfile);
        }

        public void SelectTests(string testFolder, string ratioFile, string destinationFolder)
        {
            FileUtils fileutil = new FileUtils();
            fileutil.SetHowDeepToScan(2);
            fileutil.ProcessDir(testFolder, 1, true, "", ".cs", null);
            List<String> fileNames = fileutil.GetFileNames();
            StreamReader tr = new StreamReader(ratioFile);
            string line = tr.ReadLine();
            HashSet<String> methodSet = new HashSet<String>();
            while (line != null)
            {
                if (line.StartsWith("Method") && !line.StartsWith("MethodName") && !line.StartsWith("Method_amount"))
                    methodSet.Add(line.Substring(0, line.IndexOf("\t")));
                line = tr.ReadLine();
            }

            for (int i = 0; i < fileNames.Count; i++)
            {
                string methodNum = fileNames[i].Substring(fileNames[i].IndexOf("Method"), fileNames[i].Length - fileNames[i].IndexOf("Method"));
                methodNum = methodNum.Substring(0, methodNum.IndexOf("_"));
                if (methodSet.Contains(methodNum))
                {
                    File.Copy(fileNames[i], fileNames[i].Replace(fileNames[i].Substring(0, fileNames[i].LastIndexOf("\\")), destinationFolder));
                }
            }
            fileutil.CleanFileNames();
        }


        public void GetAutomataCoverage(string ratioFile, string reportFolder, string outputfile)
        {
            FileUtils fileutil = new FileUtils();
            fileutil.SetHowDeepToScan(2);
            fileutil.ProcessDir(reportFolder, 1, true, "RegExpChecker_", ".xml", null);
            List<String> fileNames = fileutil.GetFileNames();

            StreamReader tr = new StreamReader(ratioFile);
            string line = tr.ReadLine();
            HashSet<String> methodSet = new HashSet<String>();
            while (line != null)
            {
                if (line.StartsWith("Method") && !line.StartsWith("MethodName") && !line.StartsWith("Method_amount"))
                    methodSet.Add(line.Substring(0, line.IndexOf("\t")));
                line = tr.ReadLine();
            }

            StringBuilder sb = new StringBuilder();
            float sum = 0;
            float count = 0; 
            for (int i = 0; i < fileNames.Count; i++)
            {
                tr = new StreamReader(fileNames[i]);
                line = tr.ReadLine();
                while (line != null)
                {
                    if (line.StartsWith("<method name=\"Method"))
                    {
                        string method = line.Substring(line.IndexOf("\"Method") + 1, line.IndexOf("\" type=") - line.IndexOf("\"Method") - 1);
                        if (!methodSet.Contains(method))
                            break;
                        else
                        {
                            while (line != null)
                            {
                                if (line.StartsWith("<method name=\"AcceptChar\""))
                                {
                                    while (line != null)
                                    {
                                        if (line.StartsWith("<coverage "))
                                        {
                                            string cov = line.Substring(line.IndexOf("coveredRatio="), line.Length-line.IndexOf("coveredRatio="));
                                            cov = cov.Substring(cov.IndexOf("\"")+1, cov.IndexOf("\"/>")-cov.IndexOf("\"") - 1);
                                            sb.Append(method + ".AcceptChar\t" + cov + "\n");
                                            sum = sum + float.Parse(cov);
                                            count++;
                                            methodSet.Remove(method);
                                            break;
                                        }
                                        line = tr.ReadLine();
                                    }
                                    break;
                                }
                                line = tr.ReadLine();
                            }
                        }
                        break;
                    }
                    line = tr.ReadLine();
                }
            }
            sb.Append("Sum:\t" + sum  + "\n");
            sb.Append("Count:\t" + count + "\n");
            sb.Append("Average:\t"+sum/count + "\n");
            sb.Append("Left:\n");
            for (int i = 0; i < methodSet.Count; i++)
                sb.Append(methodSet.ElementAt(i)+"\n");
            FileUtils.WriteTxt(sb.ToString(), outputfile);
            fileutil.CleanFileNames();
        }

    }
}
