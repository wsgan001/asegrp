using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using CloneFinder;

namespace AutomatonToCode
{
    public class AutomatonInstrumentation
    {
        public void InstrumentAllAcceptCharInFolder(string folder)
        {
            FileUtils fileutil = new FileUtils();
            fileutil.SetHowDeepToScan(1);
            fileutil.ProcessDir(folder, 1, true, "", ".cs", null);
            List<String> fileNames = fileutil.GetFileNames();
            for(int i=0;i<fileNames.Count;i++){
                if (!fileNames[i].Contains("RegexIsMatch_"))
                {
                    string newfile = fileNames[i].Replace("automaton.cs", "automaton.cs.instrumented");
                    InstrumentAcceptChar(fileNames[i], newfile);
                }
            }
        }

        public void InstrumentAllIsMatchInFolder(string folder)
        {
            FileUtils fileutil = new FileUtils();
            fileutil.SetHowDeepToScan(1);
            fileutil.ProcessDir(folder, 1, true, "", ".cs", null);
            List<String> fileNames = fileutil.GetFileNames();
            for (int i = 0; i < fileNames.Count; i++)
            {
                if (fileNames[i].Contains("RegexIsMatch_"))
                {
                    string newfile = fileNames[i].Replace("automaton.cs", "automaton.cs.instrumented");
                    InstrumentIsMatch_ISSTA09(fileNames[i], newfile);
                }
            }
        }


        public void InstrumentAcceptChar(string fileName, string newFilename)
        {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StreamReader tr = new StreamReader(fileName);
            string line = tr.ReadLine();

            int node_count = 0;
            int edge_count = 0;
            bool flag = false;
            while (line != null)
            {
                if (line.Trim().StartsWith("STATE"))
                {
                    while (line != null)
                    {
                        if (line.Trim().StartsWith("STATE"))
                        {
                            node_count++;
                            sb1.AppendLine(line+"\n");                            
                        }
                        else if(line.Trim().StartsWith("}"))
                        {
                            sb1.AppendLine(line + "\n");
                            sb1.AppendLine("public bool[] STATE = {");
                            for(int i=0;i<node_count-1;i++)
                                sb1.Append("false,");
                            sb1.AppendLine("false };" + "\n");
                            break;
                        }  
                        else
                            sb1.AppendLine(line + "\n");
                        line = tr.ReadLine();
                    }
                    flag = true;
                    node_count = 0;
                }
                else if (line.Contains("case States.STATE"))
                {
                    sb2.AppendLine(line + "\n");
                    sb2.AppendLine("STATE[" + node_count + "] = true;" + "\n");
                    node_count++;
                }
                else if (line.Contains("if (-1 !="))
                {
                    sb2.AppendLine(line + "\n");
                    sb2.AppendLine(tr.ReadLine() + "\n");//{
                    sb2.AppendLine("EDGE[" + edge_count + "] = true;" + "\n");
                    edge_count++;
                }
                else
                {
                    if(flag)
                        sb2.Append(line + "\n");
                    else
                        sb1.Append(line + "\n");
                }
                line = tr.ReadLine();
            }

            sb1.Append("public bool[] EDGE = {");
            for (int i = 0; i < edge_count - 1; i++)
                sb1.Append("false,");
            sb1.Append("false };" + "\n");
            sb1.Append(sb2.ToString() + "\n");

            FileUtils.WriteTxt(sb1.ToString(), newFilename);
        }

        public void InstrumentIsMatch(string fileName, string newFilename)
        {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StreamReader tr = new StreamReader(fileName);
            string line = tr.ReadLine();
            string className = "";
            while (line != null)
            {
                if (line.Trim().StartsWith("namespace"))
                {
                    sb1.AppendLine("using CloneFinder;" + "\n");
                    sb1.AppendLine("using System.Text;" + "\n");
                    sb1.AppendLine(line + "\n");
                }
                else if (line.Trim().Contains(" = new"))
                {
                    sb1.AppendLine(line + "\n");
                    line = line.Trim();
                    className = line.Substring(line.IndexOf(" "), line.IndexOf("=") - line.IndexOf(" ")).Trim();
                    //Construct the printing code                    
                    sb2.AppendLine("StringBuilder sb = new StringBuilder();" + "\n");
                    sb2.AppendLine("sb.Append(\"Node Total:\\t \" + " + className + ".STATE.Length + \"\\n\");" + "\n");
                    sb2.AppendLine("int count = 0;" + "\n");
                    sb2.AppendLine("for (int j = 0; j < " + className + ".STATE.Length; j++)" + "\n");
                    sb2.AppendLine("if (" + className + ".STATE[j])" + "\n");
                    sb2.AppendLine("count++;" + "\n");
                    sb2.AppendLine("sb.Append(\"Covered Node:\\t \" + count + \"\\n\");" + "\n");
                    sb2.AppendLine("sb.Append(\"Node Coverage:\\t \" + ((float)count / " + className + ".STATE.Length) + \"\\n\");" + "\n");
                    sb2.AppendLine("sb.Append(\"Edge Total:\\t \" + " + className + ".EDGE.Length + \"\\n\");" + "\n");
                    sb2.AppendLine("count = 0;" + "\n");
                    sb2.AppendLine("for (int j = 0; j < " + className + ".EDGE.Length; j++)" + "\n");
                    sb2.AppendLine("if (" + className + ".EDGE[j])" + "\n");
                    sb2.AppendLine("count++;" + "\n");
                    sb2.AppendLine("sb.Append(\"Covered Node:\\t \" + count + \"\\n\");" + "\n");
                    sb2.AppendLine("if (" + className + ".EDGE.Length>0)" + "\n");
                    sb2.AppendLine("sb.Append(\"EDGE Coverage:\\t \" + ((float)count / " + className + ".EDGE.Length) + \"\\n\");" + "\n");
                    sb2.AppendLine("FileUtils.WriteTxt(sb.ToString(), @\"C:\\MyWorkspace\\li-RegExpWorkspace-CSharp\\issta09-experiments\\automaton.cs.instrumented\\Node_Edge_Cov_" + className + ".txt\");" + "\n");
                }
                else if (line.Contains("(!flag)"))
                {
                    sb1.AppendLine(line + "\n");
                    sb1.AppendLine("{" + "\n");
                    sb1.AppendLine(sb2.ToString() + "\n");
                    line = tr.ReadLine();
                    while(line!=null)
                    {
                        if (line.Contains("return false"))
                        {
                            sb1.AppendLine(line + "\n");
                            break;
                        }
                        else
                            sb1.AppendLine(line + "\n");
                        line = tr.ReadLine();
                    }
                    sb1.AppendLine("}" + "\n");
                }
                else if (line.Contains("catch"))
                {
                    sb1.AppendLine(line + "\n");
                    sb1.AppendLine(sb2.ToString() + "\n");                              
                }
                else if (line.Contains(".Equals"))
                {
                    string newSb2 = sb2.ToString().Replace("sb", "sb1");
                    newSb2 = newSb2.Replace("count", "count1");
                    sb1.AppendLine(newSb2 + "\n");
                    sb1.Append(line + "\n");
                }
                else
                    sb1.Append(line + "\n");
                line = tr.ReadLine();
            }

            FileUtils.WriteTxt(sb1.ToString(), newFilename);
        }

        public void InstrumentIsMatch_ISSTA09(string fileName, string newFilename)
        {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StreamReader tr = new StreamReader(fileName);
            string line = tr.ReadLine();
            string className = "";
            while (line != null)
            {
                if (line.Trim().StartsWith("namespace"))
                {
                    sb1.AppendLine("using CloneFinder;" + "\n");
                    sb1.AppendLine("using System.Text;" + "\n");
                    sb1.AppendLine(line + "\n");
                }
                else if (line.Trim().Contains(" = new"))
                {
                    sb1.AppendLine(line + "\n");
                    line = line.Trim();
                    className = line.Substring(line.IndexOf(" "), line.IndexOf("=") - line.IndexOf(" ")).Trim();

                    sb2.AppendLine("for (int j = 0; j < " + className + ".STATE.Length; j++)" + "\n");
                    sb2.AppendLine("if (" + className + ".STATE[j])" + "\n");
                    string driverClassName = className.Substring(className.IndexOf("_") + 1, className.IndexOf("Automaton") - className.IndexOf("_") - 1);
                    if (!className.EndsWith("n"))
                        driverClassName = driverClassName + className[className.Length - 1];
                    else
                        driverClassName = driverClassName.Insert(2, "_");
                    sb2.AppendLine(driverClassName  + ".STATE[j] = true;" + "\n");
                    

                    sb2.AppendLine("for (int j = 0; j < " + className + ".EDGE.Length; j++)" + "\n");
                    sb2.AppendLine("if (" + className + ".EDGE[j])" + "\n");
                    sb2.AppendLine(driverClassName + ".EDGE[j] = true;" + "\n");
                    
                }
                else if (line.Contains("(!flag)"))
                {
                    sb1.AppendLine(line + "\n");
                    sb1.AppendLine("{" + "\n");
                    sb1.AppendLine(sb2.ToString() + "\n");
                    line = tr.ReadLine();
                    while (line != null)
                    {
                        if (line.Contains("return false"))
                        {
                            sb1.AppendLine(line + "\n");
                            break;
                        }
                        else
                            sb1.AppendLine(line + "\n");
                        line = tr.ReadLine();
                    }
                    sb1.AppendLine("}" + "\n");
                }
                else if (line.Contains("catch"))
                {
                    sb1.AppendLine(line + "\n");
                    sb1.AppendLine(sb2.ToString() + "\n");
                }
                else if (line.Contains(".Equals"))
                {
                    string newSb2 = sb2.ToString().Replace("sb", "sb1");
                    newSb2 = newSb2.Replace("count", "count1");
                    sb1.AppendLine(newSb2 + "\n");
                    sb1.Append(line + "\n");
                }
                else
                    sb1.Append(line + "\n");
                line = tr.ReadLine();
            }

            FileUtils.WriteTxt(sb1.ToString(), newFilename);
        }

    }
}
