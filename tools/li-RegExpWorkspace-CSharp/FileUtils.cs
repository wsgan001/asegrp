using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;


namespace CloneFinder
{
    class FileUtils
    {
        private int HowDeepToScan;
        private List<String> fileNames = new List<String>();

        //write the file names contained in the sourceDir into the filenames list
        public void ProcessDir(string sourceDir, int recursionLvl, bool recursive, string folderPrefix, String fileType, string ignoredFileType)
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
                    {
                        string _folder = subdir.Substring(subdir.LastIndexOf("\\") + 1, subdir.Length - subdir.LastIndexOf("\\") - 1);
                        if (_folder.StartsWith(folderPrefix))
                        {
                            // Do not iterate through reparse points
                            if ((File.GetAttributes(subdir) &
                                 FileAttributes.ReparsePoint) !=
                                     FileAttributes.ReparsePoint)

                                ProcessDir(subdir, recursionLvl + 1, recursive, folderPrefix, fileType, null);
                        }
                    }
                }
            }
        }

        public static void WriteTxt(String contents, String fileName)
        {
            FileInfo t = new FileInfo(fileName);
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

      

        
        
    }
}

