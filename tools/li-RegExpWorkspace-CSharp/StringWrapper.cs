using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework.Suppression;
//using Microsoft.Pex.Framework;
//using Microsoft.Pex.Engine;

namespace AutomatonToCode
{
    [PexIgnore("ignore explanation")]
    public class StringWrapper
    {
        private String[] s;

        public StringWrapper()
        {
            s = new String[100];
        }

        public void setS(String[] str){
            s = str;
        }

        [PexIgnore("ignore explanation")]
        public String getS()
        {
            return s[(new Random()).Next(0,99)];
        }
    }
}
