using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AutomatonToCode.ServiceReference1;
using System.Data;
using System.IO;
using System.ServiceModel;
using System.Xml;
using System.Text.RegularExpressions;
using CloneFinder;

namespace AutomatonToCode
{
    class RegExpToTestedMethod
    {
        public void PrintRegExpsToFile(string keyword, string regexp_substring, int min_rating, int howmanyrows, string filename)
        {
            WebservicesSoapClient wssc = new WebservicesSoapClient();
            System.Console.WriteLine(wssc.State);
            DataSet ds = new DataSet();

            ds = wssc.listRegExp(keyword, regexp_substring, min_rating, howmanyrows);
            ds.WriteXml(filename, System.Data.XmlWriteMode.IgnoreSchema);

            ds.Clear();
            wssc.Abort();
            wssc.Close();
        }

        public void ExtractElementsFromXML(string element, string inputFile, string outputFile)
        {
            XmlTextReader xmlReader = new XmlTextReader(inputFile);
            StringBuilder sb = new StringBuilder();
            while (xmlReader.Read())
            {
                xmlReader.MoveToElement();
                if (xmlReader.Name.Equals(element))
                {
                    sb.Append(xmlReader.ReadElementContentAsString() + "\r\n\r\n");
                }
            }
            TextWriter tw = new StreamWriter(outputFile);
            tw.WriteLine(sb.ToString());
            tw.Close();
        }

        public void SUTSynthesis(string regexFileName, string subjectPath)
        {
            StreamReader tr = new StreamReader(regexFileName);
            string line = tr.ReadLine();
            int i = 1;

            while (line != null)
            {
                if (line.Length > 0)
                {
                    StringBuilder sb = new StringBuilder();
                    string uniqueName = DateTime.Now.ToString().Replace("/", "_");
                    uniqueName = uniqueName.Replace(" ", "_");
                    uniqueName = uniqueName.Replace(":", "_");
                    uniqueName = uniqueName + "_" + i;
                    sb.Append("using System.Text.RegularExpressions;\n");
                    sb.Append("using Microsoft.Pex.Framework;\n");
                    sb.Append("namespace RegExpChecker_" + uniqueName + "\n{\n");
                    sb.Append("[PexClass]\n");
                    sb.Append("public partial class " + regexFileName.Substring(regexFileName.LastIndexOf("\\") + 1, regexFileName.LastIndexOf(".") - regexFileName.LastIndexOf("\\") - 1) + "_" + uniqueName + "\n");
                    sb.Append("{\n");
                    sb.Append("[PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]\n");
                    sb.Append("public bool Method" + i + "(string s)\n{\n");
                    sb.Append("if (s == null)\n return false;\n");
                    if (line.Contains("\\"))
                        line = line.Replace("\\", "\\\\");
                    if (line.Contains("\""))
                        line = line.Replace("\"", "\\\"");
                    sb.Append("if (Regex.IsMatch(s, \"" + line + "\"))\n");
                    sb.Append("return true;\n else \n return false;\n }\n");
                    sb.Append("}\n}\n");

                    FileUtils.WriteTxt(sb.ToString(), subjectPath + "_" + uniqueName + ".cs");
                   
                    i++;
                }
                line = tr.ReadLine();
            }
            tr.Close();
        }
    }
}
