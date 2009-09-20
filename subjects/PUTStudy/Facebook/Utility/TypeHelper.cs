using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;

namespace facebook.Utility
{
    public class TypeHelper
    {
        public static Type getResponseObjectType(string response)
        {
            XDocument doc = XDocument.Parse(response);
            return Type.GetType("facebook.Schema." + doc.Root.Name.LocalName); 
        }
    }
}
