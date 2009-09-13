using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using CloneFinder;
using System.IO;

namespace AutomatonToCode
{
    class NCoverReportAnalysis
    {
        private string ratioFile = @"C:\RegExpTesting_workspace\AutomatonToCode\ratio.txt";

        public Hashtable GetCoverageList_AutoClass(string filename)
        {
            string coverage = "";
            string className = "";

            Hashtable ht = new Hashtable();

            StreamReader tr = new StreamReader(filename);
            string line = tr.ReadLine();

            while (line != null)
            {
                if (line.Contains("<td class=\"percentage\" title=\"Branch Coverage"))
                {
                    if (line.IndexOf("%") > 0)
                        coverage = line.Substring(line.IndexOf("Coverage: ") + 9, line.IndexOf("%") - line.IndexOf("Coverage: ") - 9);
                    else
                        coverage = "0";
                    while (line != null)
                    {
                        if (line.Contains("<span title=\"RegExpChecker_"))
                        {
                            if (!line.Contains(".Auto__") || line.Contains("+States"))
                                break;
                            else
                            {
                                className = line.Substring(line.IndexOf("Auto_"), line.IndexOf("\">") - line.IndexOf("Auto_"));
                                ht.Add(className, (float)System.Convert.ToSingle(coverage));
                                break;
                            }
                        }
                        line = tr.ReadLine();
                    }
                }
                line = tr.ReadLine();
            }
            return ht;
        }

        public void CompareTwoCoverageLists_AutoClass(string filename1, string filename2, string outputfile)
        {
            Hashtable ht1 = GetCoverageList_AutoClass(filename1);
            Hashtable ht2 = GetCoverageList_AutoClass(filename2);
            StringBuilder sb = new StringBuilder();
            sb.Append("ClassName\t" + filename1.Substring(filename1.IndexOf("AutomatonToCode."), filename1.LastIndexOf("\\") - filename1.IndexOf("AutomatonToCode.")) + "\t" + filename2.Substring(filename2.IndexOf("AutomatonToCode."), filename2.LastIndexOf("\\") - filename2.IndexOf("AutomatonToCode.")) + "\tDiff:Col1-Col2 \n");
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
            sb.Append("Class_amount\t" + count + "\n");

            FileUtils.WriteTxt(sb.ToString(), outputfile);
        }


        public Hashtable GetCoverageList_AutoMethod(string foldername)
        {
            Hashtable ht = new Hashtable();

            FileUtils fileutil = new FileUtils();
            fileutil.SetHowDeepToScan(1);
            fileutil.ProcessDir(foldername, 1, false, "", ".html", null);
            List<String> fileNames = fileutil.GetFileNames();

            for (int i = 0; i < fileNames.Count; i++)
            {
                string[] method_ratio = ExtractCoverageInfo_AutoMethod(fileNames[i]);
                if (method_ratio[0].Length > 0)
                {
                    ht.Add(method_ratio[0], (float)System.Convert.ToSingle(method_ratio[1]));
                    Console.WriteLine(i + "/" + fileNames.Count + "     MethodName: " + method_ratio[0] + "  BranchCoverage: " + (float)System.Convert.ToSingle(method_ratio[1]));
                }
            }
            fileutil.CleanFileNames();
            return ht;
        }


        public void CompareTwoCoverageLists_AutoMethod(string folder1, string folder2, string outputfile)
        {
            Hashtable ht1 = GetCoverageList_AutoMethod(folder1);
            Hashtable ht2 = GetCoverageList_AutoMethod(folder2);
            StringBuilder sb = new StringBuilder();
            sb.Append("ClassName of AcceptChar\t" + folder1.Substring(folder1.IndexOf("AutomatonToCode."), folder1.LastIndexOf("\\") - folder1.IndexOf("AutomatonToCode.")) + "\t" + folder2.Substring(folder2.IndexOf("AutomatonToCode."), folder2.LastIndexOf("\\") - folder2.IndexOf("AutomatonToCode.")) + "\tDiff:Col1-Col2 \n");
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
            sb.Append("Class_amount\t" + count + "\n");

            FileUtils.WriteTxt(sb.ToString(), outputfile);
        }


        public string[] ExtractCoverageInfo_AutoMethod(string filename)
        {
            StreamReader tr = new StreamReader(this.ratioFile);
            string line = tr.ReadLine();
            HashSet<String> methodSet = new HashSet<String>();
            while (line != null)
            {
                if (line.StartsWith("Method") && !line.StartsWith("MethodName") && !line.StartsWith("Method_amount"))
                    methodSet.Add(line.Substring(0, line.IndexOf("\t")));
                line = tr.ReadLine();
            }

            string coverage = "0";
            string method = "";
            string[] ht = { method, coverage };
            tr = new StreamReader(filename);
            line = tr.ReadLine();

            while (line != null)
            {
                if (line.Contains("<h1 title=\""))
                {
                    if (!line.Contains("Auto_") || line.Contains("+States"))
                        break;
                    else
                    {
                        method = line.Substring(line.IndexOf("Auto_"), line.LastIndexOf("\"") - line.IndexOf("Auto_"));
                        if (!methodSet.Contains("Method" + method.Substring(method.LastIndexOf("_") + 1, method.Length - method.LastIndexOf("_") - 1)))
                            break;
                        ht[0] = method;
                        while (line != null)
                        {
                            if (line.Contains("AcceptChar(System.Char c) : System.Boolean"))
                            {
                                //looking for the 4th "<div class="
                                int div = 0;
                                while (line != null)
                                {
                                    if (line.Contains("<div class="))
                                        div++;
                                    if (div == 4)
                                    {
                                        line = tr.ReadLine().Trim();
                                        coverage = line.Trim().Substring(0, line.IndexOf("%"));
                                        break;
                                    }
                                    line = tr.ReadLine();
                                }
                                ht[1] = coverage;
                                break;
                            }
                            line = tr.ReadLine();
                        }
                    }
                }
                line = tr.ReadLine();
            }
            return ht;
        }

        public string[] ExtractCoverageInfo_NodeEdge(string filename)
        {
            string nodeTotal = "";
            string covNode = "";
            string nodeCov = "";
            string edgeTotal = "";
            string covEdge = "";
            string edgeCov = "";

            StreamReader tr = new StreamReader(filename);
            string line = tr.ReadLine();
            bool isRealNode = true;
            while (line != null)
            {
                if (line.Contains("Node Total:"))
                    nodeTotal = line.Substring(line.IndexOf("\t") + 1, line.Length - line.IndexOf("\t") - 1).Trim();
                else if (isRealNode && line.Contains("Covered Node:"))
                {
                    covNode = line.Substring(line.IndexOf("\t") + 1, line.Length - line.IndexOf("\t") - 1).Trim();
                    isRealNode = false;
                }
                else if (line.Contains("Node Coverage:"))
                    nodeCov = line.Substring(line.IndexOf("\t") + 1, line.Length - line.IndexOf("\t") - 1).Trim();
                else if (line.Contains("Edge Total:"))
                    edgeTotal = line.Substring(line.IndexOf("\t") + 1, line.Length - line.IndexOf("\t") - 1).Trim();
                else if (!isRealNode && line.Contains("Covered Node:"))
                    covEdge = line.Substring(line.IndexOf("\t") + 1, line.Length - line.IndexOf("\t") - 1).Trim();
                else if (line.Contains("EDGE Coverage:"))
                    edgeCov = line.Substring(line.IndexOf("\t") + 1, line.Length - line.IndexOf("\t") - 1).Trim();
                line = tr.ReadLine();
            }
            string[] info = { nodeTotal, covNode, nodeCov, edgeTotal, covEdge, edgeCov };
            return info;
        }

        public Hashtable GetCoverageList_NodeEdge(string foldername)
        {
            Hashtable ht = new Hashtable();

            FileUtils fileutil = new FileUtils();
            fileutil.SetHowDeepToScan(1);
            fileutil.ProcessDir(foldername, 1, false, "", ".txt", null);
            List<String> fileNames = fileutil.GetFileNames();

            for (int i = 0; i < fileNames.Count; i++)
            {
                string[] info = ExtractCoverageInfo_NodeEdge(fileNames[i]);
                ht.Add(fileNames[i].Substring(fileNames[i].LastIndexOf("\\") + 1, fileNames[i].IndexOf(".txt") - fileNames[i].LastIndexOf("\\") - 1), info);

            }
            fileutil.CleanFileNames();
            return ht;
        }

        public void CompareTwoCoverageLists_NodeEdge(string folder1_27, string folder2_28, string outputFolder)
        //27-test contains less report than 28-test 
        {
            Hashtable ht1 = GetCoverageList_NodeEdge(folder1_27);
            Hashtable ht2 = GetCoverageList_NodeEdge(folder2_28);
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StringBuilder sb3 = new StringBuilder();
            sb1.Append("Automaton\tNode Total\tCovered Node\tNode Coverage\tEdge Total\tCovered Node\tEDGE Coverage\n");
            sb2.Append("Automaton\tNode Total\tCovered Node\tNode Coverage\tEdge Total\tCovered Node\tEDGE Coverage\n");
            sb3.Append("Automaton\tNode Total\tCovered Node\tNode Coverage\tEdge Total\tCovered Node\tEDGE Coverage\n");

            IDictionaryEnumerator _enumerator = ht2.GetEnumerator();
           
            while (_enumerator.MoveNext())
            {
                sb2.Append(_enumerator.Key + "\t" + ((string[])_enumerator.Value)[0]
                    + "\t" + ((string[])_enumerator.Value)[1]
                + "\t" + ((string[])_enumerator.Value)[2]
                + "\t" + ((string[])_enumerator.Value)[3]
                + "\t" + ((string[])_enumerator.Value)[4]
                + "\t" + ((string[])_enumerator.Value)[5] + "\n");

                if (ht1.ContainsKey(_enumerator.Key))
                {
                    sb3.Append((string)_enumerator.Key
                    + "\t" + (float.Parse(((string[])ht1[_enumerator.Key])[0]) - float.Parse(((string[])_enumerator.Value)[0]))
                    + "\t" + (float.Parse(((string[])ht1[_enumerator.Key])[1]) - float.Parse(((string[])_enumerator.Value)[1]))
                + "\t" + (float.Parse(((string[])ht1[_enumerator.Key])[2]) - float.Parse(((string[])_enumerator.Value)[2]))
                + "\t" + (float.Parse(((string[])ht1[_enumerator.Key])[3]) - float.Parse(((string[])_enumerator.Value)[3]))
                + "\t" + (float.Parse(((string[])ht1[_enumerator.Key])[4]) - float.Parse(((string[])_enumerator.Value)[4]))
                + "\t" + (float.Parse(((string[])ht1[_enumerator.Key])[5]) - float.Parse(((string[])_enumerator.Value)[5]))
                + "\n");

                }
            }

            IDictionaryEnumerator _enumerator1 = ht1.GetEnumerator();
            while (_enumerator1.MoveNext())
            {
                sb1.Append(_enumerator1.Key + "\t" + ((string[])_enumerator1.Value)[0]
                    + "\t" + ((string[])_enumerator1.Value)[1]
                + "\t" + ((string[])_enumerator1.Value)[2]
                + "\t" + ((string[])_enumerator1.Value)[3]
                + "\t" + ((string[])_enumerator1.Value)[4]
                + "\t" + ((string[])_enumerator1.Value)[5] + "\n");
            }

            FileUtils.WriteTxt(sb1.ToString(), outputFolder + "\\NodeEdge_ratio_27.txt");
            FileUtils.WriteTxt(sb2.ToString(), outputFolder + "\\NodeEdge_ratio_28.txt");
            FileUtils.WriteTxt(sb3.ToString(), outputFolder + "\\NodeEdge_ratio_Diff.txt");
        }


        public void CompareTwoCoverageLists_TestedMethod(string folder1, string folder2, string outputfile)
        {
            Hashtable ht1 = GetCoverageList_TestedMethod(folder1);
            Hashtable ht2 = GetCoverageList_TestedMethod(folder2);
            StringBuilder sb = new StringBuilder();
            sb.Append("ClassName of AcceptChar\t" + folder1.Substring(folder1.IndexOf("AutomatonToCode."), folder1.LastIndexOf("\\") - folder1.IndexOf("AutomatonToCode.")) + "\t" + folder2.Substring(folder2.IndexOf("AutomatonToCode."), folder2.LastIndexOf("\\") - folder2.IndexOf("AutomatonToCode.")) + "\tDiff:Col1-Col2 \n");
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
            sb.Append("Class_amount\t" + count + "\n");

            FileUtils.WriteTxt(sb.ToString(), outputfile);
        }

        private Hashtable GetCoverageList_TestedMethod(string foldername)
        {
            Hashtable ht = new Hashtable();

            FileUtils fileutil = new FileUtils();
            fileutil.SetHowDeepToScan(1);
            fileutil.ProcessDir(foldername, 1, false, "", ".html", null);
            List<String> fileNames = fileutil.GetFileNames();

            for (int i = 0; i < fileNames.Count; i++)
            {
                string[] method_ratio = ExtractCoverageInfo_TestedMethod(fileNames[i]);
                if (method_ratio[0].Length > 0)
                {
                    ht.Add(method_ratio[0], (float)System.Convert.ToSingle(method_ratio[1]));
                    Console.WriteLine(i + "/" + fileNames.Count + "     MethodName: " + method_ratio[0] + "  BranchCoverage: " + (float)System.Convert.ToSingle(method_ratio[1]));
                }
            }
            fileutil.CleanFileNames();
            return ht;
        }

        public string[] ExtractCoverageInfo_TestedMethod(string filename)
        {
            //StreamReader tr = new StreamReader(this.ratioFile);
            //string line = tr.ReadLine();
            //HashSet<String> methodSet = new HashSet<String>();
            //while (line != null)
            //{
            //    if (line.StartsWith("Method") && !line.StartsWith("MethodName") && !line.StartsWith("Method_amount"))
            //        methodSet.Add(line.Substring(0, line.IndexOf("\t")));
            //    line = tr.ReadLine();
            //}

            string coverage = "0";
            string method = "";
            string[] ht = { method, coverage };
             StreamReader tr = new StreamReader(filename);
            string line = tr.ReadLine();

            while (line != null)
            {
                if (line.Contains(@"<h1>subjects\testable\_"))
                {
                   
                        method = line.Substring(line.IndexOf(@"\_") + 2, line.IndexOf(".cs") + 3 - line.IndexOf(@"\_") - 2);
                        //if (!methodSet.Contains("Method" + method.Substring(method.LastIndexOf("_") + 1, method.Length - method.LastIndexOf("_") - 1)))
                        //    break;
                        ht[0] = method;
                        while (line != null)
                        {
                            if (line.Contains("Branch Coverage"))
                            {   
                                while (line != null)
                                {
                                    if (line.Contains("%"))                                       
                                    {
                                        line = line.Trim();
                                        coverage = line.Substring(line.IndexOf(">")+1, line.IndexOf("%")-line.IndexOf(">")-1);
                                        break;
                                    }
                                    line = tr.ReadLine();
                                }
                                ht[1] = coverage;
                                break;
                            }
                            line = tr.ReadLine();
                        }
                    }
                
                line = tr.ReadLine();
            }
            return ht;
        }


    }
}
