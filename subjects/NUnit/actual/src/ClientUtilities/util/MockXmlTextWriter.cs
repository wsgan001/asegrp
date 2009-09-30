using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Xml;
using System.Collections.Generic;

namespace NUnit.Util
{
    public static class CreatedProjects
    {
        //public static HashTable table;

        public static String currentProject;

        /*public String XmlString
        {
            get { return currentProject; }
        }*/
    }


    public partial class MockXmlTextWriter
    {
        String fileName;
        String xmlString;
        String startString;

        public Formatting Formatting;

        public MockXmlTextWriter(string filename, Encoding encoding)
        {
            this.fileName = filename;
        }

        public void WriteAttributeString(string attributeName, string value)
        {
            xmlString = xmlString + " " + attributeName + "=" + "\"" + value + "\"";
        }

        public void WriteEndElement()
        {
            xmlString = xmlString + " />";
        }
        
        public void Close()
        {
            xmlString = xmlString.Replace("/> />", "/>" + System.Environment.NewLine + "</" + startString + ">");

            CreatedProjects.currentProject = xmlString;
        }

        public void WriteStartElement(string localName)
        {
            String preString = xmlString;
            if (preString != null)
            {
                xmlString = xmlString + System.Environment.NewLine ;
                xmlString = xmlString + "  ";
            }
            xmlString = xmlString+"<"+ localName;
            if (preString == null)
            {
                startString = localName;
                xmlString = xmlString + ">";
            }
        }

       

    }
}
