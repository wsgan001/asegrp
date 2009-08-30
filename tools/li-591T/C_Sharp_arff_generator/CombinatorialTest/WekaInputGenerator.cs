using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace CombinatorialTest
{
    public class WekaInputGenerator
    {
        private StringBuilder tc_counter = new StringBuilder();
        private String folderName = "";
        private int tcNum = 0;
        private int resultNum = 0;
        private string head =
                "@relation test-failure\r\n" +
                "@attribute Cur_Vertical_Sep { 299, 300, 601 }\r\n" +
                "@attribute High_Confidence {0, 1}\r\n" +
                "@attribute Two_of_Three_Reports_Valid {0, 1}\r\n" +
                "@attribute Own_Tracked_Alt { 1, 2 }\r\n" +
                "@attribute Own_Tracked_Alt_Rate { 600, 601 }\r\n" +
                "@attribute Other_Tracked_Alt { 1, 2 }\r\n" +
                "@attribute Alt_Layer_Value {0,1,2,3}\r\n" +
                "@attribute Up_Separation { 0, 399, 400, 499, 500, 639, 640, 739, 740, 840 }\r\n" +
                "@attribute Down_Separation { 0, 399, 400, 499, 500, 639, 640, 739, 740, 840 }\r\n" +
                "@attribute Other_RAC  { 0, 1, 2 }\r\n" +
                "@attribute Other_Capability { 1, 2 }\r\n" +
                "@attribute Climb_Inhibit {0,1}\r\n" +
                "@attribute Failure {0,1}\r\n" +
                "\r\n" +
                "@data\r\n";

        public void SynthesizeArffFromTesterdeskOutputs(String fileName)
        {
            //read the test case file
            TextReader tr = new StreamReader(fileName);
            string line = "";
            string resultFileName = "";

            List<string> tc = new List<string>();
            while ((line = tr.ReadLine()) != null)
            {
                string[] words = line.Split('\t');
                StringBuilder aTC = new StringBuilder();
                for (int i = 1; i < words.Length - 1; i++)
                {
                    aTC.Append(words[i] + ",");
                }
                tc.Add(aTC.ToString());
                if (resultFileName.Equals(""))
                    resultFileName = words[words.Length - 1].Substring(words[words.Length - 1].LastIndexOf(">") + 1, words[words.Length - 1].Length - words[words.Length - 1].LastIndexOf(">") - 1);
            }
        }

        public void ReadTcasResultFolder(string InputFolder, string t_way)
        {
            FileUtils fileUtils = new FileUtils();
            fileUtils.SetHowDeepToScan(2);
            fileUtils.ProcessDir(InputFolder, 1, true, "", ".c");
            List<string> fileNames = fileUtils.GetFileNames();

            for (int i = 0; i < fileNames.Count; i++)
            {
                Console.Out.WriteLine(i + "/" + fileNames.Count);
                if (fileNames[i].Contains("run"))
                {
                    if (t_way.Equals("all") || fileNames[i].Contains(t_way))
                        TcasToArff(fileNames[i], t_way);
                }
            }
        }

        private void TcasToArff(string fileName, string t_way)
        {
            if (!this.folderName.Equals(fileName.Substring(0, fileName.LastIndexOf(@"\"))))
            {
                tc_counter.Append(this.folderName + "  TC#:" + this.tcNum + ";   Result#: " + this.resultNum + "\r\n"); 
                this.folderName = fileName.Substring(0, fileName.LastIndexOf(@"\"));
                this.tcNum = 0;
                this.resultNum = 0;
            }            

            //read the test case file
            TextReader tr = new StreamReader(fileName);
            string line = "";
            string resultFileName = "";

            List<string> tc = new List<string>();
            while ((line = tr.ReadLine()) != null)
            {
                string[] words = line.Split(' ');
                StringBuilder aTC = new StringBuilder();
                for (int i = 1; i < words.Length - 1; i++)
                {
                    aTC.Append(words[i] + ",");
                }
                tc.Add(aTC.ToString());
                if(resultFileName.Equals(""))
                    resultFileName = words[words.Length - 1].Substring(words[words.Length - 1].LastIndexOf(">") + 1, words[words.Length - 1].Length - words[words.Length - 1].LastIndexOf(">") - 1);
            }

            //read its coresponding result file
            List<string> result = new List<string>();
            tr = new StreamReader(fileName.Substring(0,fileName.LastIndexOf("\\")+1) + resultFileName);
            int expectedResult = 0;//unre
            if (resultFileName.EndsWith("up"))
                expectedResult = 1;
            else if (resultFileName.EndsWith("down"))
                expectedResult = 2;
            while ((line = tr.ReadLine()) != null)
            {
                int actualResult = Int16.Parse(line);
                if ((actualResult - expectedResult) == 0)
                    result.Add("0");
                else
                    result.Add("1");
            }

            this.tcNum = this.tcNum + tc.Count;
            this.resultNum = this.resultNum + result.Count;
            
            if (tc.Count != result.Count)
            {
                FileUtils.AppendText(@"C:\MyWorkspace\li-591T\data\error test\error tests.txt", fileName + ": " + tc.Count + "\t" + resultFileName + ": " + result.Count);
                return;
            }

            
            //construct arff file
            StringBuilder sb = new StringBuilder();
            

            sb.Append(head);
            for(int i=0;i<tc.Count;i++){
                sb.Append(tc[i] + result[i] + "\r\n");
            }

            FileUtils.WriteTxt(sb.ToString(), fileName.Replace("cleaned-tcas-results", "arff-inputs-" + t_way) + ".arff");
             
        }

        public void CombineDifferentRuns(string folder)
        {
            string[] subdirEntries = Directory.GetDirectories(folder);
           
            foreach (string subdir in subdirEntries)
            {
                if (subdir.EndsWith("CVS"))
                    continue;
                string[] fileEntries = Directory.GetFiles(subdir);
                StringBuilder sb = new StringBuilder();
                sb.Append(head);

                foreach (string fileName in fileEntries)
                {
                    if (!fileName.Contains("run"))
                        continue;
                    TextReader tr = new StreamReader(fileName);
                    string line = "";
                    while ((line = tr.ReadLine()) != null)
                    {
                        if (line.Trim().Length > 0 && !line.StartsWith("@"))
                            sb.Append(line + "\r\n");
                    }
                }

                FileUtils.WriteTxt(sb.ToString(), subdir+subdir.Substring(subdir.LastIndexOf("\\"),subdir.Length-subdir.LastIndexOf("\\"))+ ".arff");
            }
        }


        static void Main(string[] args)
        {
            //TCAS tcas = new TCAS();
            //tcas.TCASMain(new int[] { -1, 601, 1, 1, 2, 600, 1, 0, 499, 639, 2, 2, 0 });
            
            WekaInputGenerator wekaGen = new WekaInputGenerator();
            //wekaGen.ReadTcasResultFolder(@"C:\MyWorkspace\li-591T\data\cleaned-tcas-results", "run02");//"all" is for all of the 2-way, 3-way, ... 6-way test cases
            wekaGen.CombineDifferentRuns(@"C:\MyWorkspace\li-591T\data\arff-inputs-run02");
            //FileUtils.WriteTxt(wekaGen.tc_counter.ToString(), @"C:\MyWorkspace\li-591T\data\cleaned-tcas-results\TC_Count.txt");
            //wekaGen.SynthesizeArffFromTesterdeskOutputs(@"C:\MyWorkspace\li-591T\data\testerdesk\Pairwise_TCAS.txt");
            
        }
    }
}
