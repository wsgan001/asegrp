using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Lucene.Net.Analysis;

namespace CombinatorialTest
{
    class FileUtils
    {
        private int HowDeepToScan;
        private List<String> fileNames = new List<String>();

        //write the file names contained in the sourceDir into the filenames list
        public void ProcessDir(string sourceDir, int recursionLvl, bool recursive, String fileType, string ignoredFileType)
        {
            if (recursionLvl <= HowDeepToScan)
            {
                // Process the list of files found in the directory.
                string[] fileEntries = Directory.GetFiles(sourceDir);
                foreach (string fileName in fileEntries)
                {
                    if (ignoredFileType == null && fileName.EndsWith(fileType))                    
                        this.fileNames.Add(fileName);
                    else if(ignoredFileType != null)
                        if( fileName.EndsWith(fileType)&& !fileName.EndsWith(ignoredFileType))
                            this.fileNames.Add(fileName);
                }

                if (recursive)
                {
                    // Recurse into subdirectories of this directory.
                    string[] subdirEntries = Directory.GetDirectories(sourceDir);
                    foreach (string subdir in subdirEntries)
                        // Do not iterate through reparse points
                        if ((File.GetAttributes(subdir) &
                             FileAttributes.ReparsePoint) !=
                                 FileAttributes.ReparsePoint)

                            ProcessDir(subdir, recursionLvl + 1, recursive, fileType, null);
                }
            }
        }

        public static void WriteTxt(String contents, String fileName)
        {
            FileInfo t = new FileInfo(fileName);
            if (!System.IO.Directory.Exists(fileName.Substring(0,fileName.LastIndexOf("\\"))))
            {
                System.IO.Directory.CreateDirectory(fileName.Substring(0, fileName.LastIndexOf("\\")));
            }

            StreamWriter Tex = t.CreateText();
            Tex.Write(contents);
            Tex.Close();            
        }

        public String ReadTxt(String fileName)
        {
            // create reader & open file
            TextReader tr = new StreamReader(fileName);

            String contents = tr.ReadToEnd();

            // close the stream
            tr.Close();

            return contents;
        }

        public String[] breakStrings(String cnt1)
        {
            Analyzer ana = new SimpleAnalyzer();

            List<String> arr1 = new List<String>();

            TokenStream stream = ana.TokenStream(cnt1, new StringReader(cnt1));
            try
            {
                Token tok = stream.Next();
                while (tok != null)
                {
                    arr1.Add(tok.TermText());
                    tok = stream.Next();
                }

            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                Console.Out.Write(e.Message);
            }
            String[] s1 = new String[arr1.Count];

            s1 = arr1.ToArray();
            return s1;
        }

        public String[] BreakIntoLines(String cnt1)
        {
            List<String> arr1 = new List<String>();

            StringReader stringReader = new StringReader(cnt1);

            String line = "";

            while ((line = stringReader.ReadLine()) != null)
            {
                arr1.Add(line);
            }
            
            String[] s1 = new String[arr1.Count];

            s1 = arr1.ToArray();
            return s1;
        }


        public void SetHowDeepToScan(int deep)
        {
            this.HowDeepToScan = deep;
        }

        public List<String> GetFileNames()
        {
            return this.fileNames;
        }

        public void CleanFileNames()
        {
            this.fileNames.Clear();
        }

        public void MergeTxtFiles(String folderName, String mergedFileName)
        {
            StringBuilder strBuilder = new StringBuilder();
            this.HowDeepToScan = 1;
            ProcessDir(folderName, 1, false, ".txt", null);

            String contents;
            int counter = 0;
            for (int i = 0; i < this.fileNames.Count; i++)
            {
                TextReader tr = new StreamReader(fileNames.ElementAt(i));
                while ((contents = tr.ReadLine()) != null)
                {
                    counter++;
                    strBuilder.Append(contents + "\r\n");
                    if (counter % 65535 == 0)
                    {
                        WriteTxt(strBuilder.ToString(), mergedFileName + i + ".txt");
                        strBuilder.Remove(0, strBuilder.Length);
                    }
                }
                tr.Close();
            }
            WriteTxt(strBuilder.ToString(), mergedFileName + counter + ".txt");
        }

        public void CopyActionModulistFiles(string folderName, string outputfolder)
        {
            this.SetHowDeepToScan(2);
            this.ProcessDir(folderName, 1, true, "similar-pairs-grouped.txt", null);
            List<string> fileNames = this.GetFileNames();

            for (int i = 0; i < fileNames.Count; i++)
            {
                TextReader tr = new StreamReader(fileNames.ElementAt(i));
                string line ="";
                while ((line = tr.ReadLine()) != null)
                {
                    string path = "";
                    string path2 = "";

                    if (line.StartsWith("C:"))
                        path = line;
                    else
                        continue;

                    
                        //C:\Nuo_temp\executed_actions\Cap-TestLib-AST-TWINARC\basic_coordinated.btc.doc-1--ModuleList.txt
                        //C:\Nuo_temp\executed_testcases_command_sequence\Cap-TestLib-AST-TWINARC\basic_coordinated.btc.doc-1.txt", 
                        //C:\Nuo_temp\cosine-sim-actions\basic_coordinated.btc.doc-1.txt"
                    path = path.Replace(@"\profile\executed_testcases_command_ratio\", @"\executed_actions\");
                    path = path.Replace(".txt", "--ModuleList.txt");
                    path = path.Replace("ABB", "Nuo_temp");
                    path2 = path.Replace(@"C:\Nuo_temp\executed_actions", outputfolder);
                    try
                    {
                        //// Create the file and clean up handles.
                        //using (FileStream fs = File.Create(path2)) { }

                        // Ensure that the target does not exist.
                        File.Delete(path2);

                        // Copy the file.
                        File.Copy(path, path2);
                        Console.WriteLine("{0} copied to {1}", path, path2);
                    }

                    catch
                    {
                        Console.WriteLine("Double copy is not allowed, which was not expected.");
                    }
                }
            }
            this.CleanFileNames();

        }

        public static void AppendText(string fileName, string addedText)
        {
            // This text is added only once to the file.
            if (!File.Exists(fileName))
            {
                // Create a file to write to.
                using (StreamWriter sw = File.CreateText(fileName))
                {
                    sw.WriteLine(addedText);

                }
            }

            // This text is always added, making the file longer over time
            // if it is not deleted.
            using (StreamWriter sw = File.AppendText(fileName))
            {
                sw.WriteLine(addedText);
            }
        }

        
    }
}

