using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework.Explorable;
using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;

namespace AutomatonToCode
{
    [PexClass]
    public partial class sampletest2
    {        
        
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method1(string s)
        {
            if (s == null)
                return false;
            if (Regex.IsMatch(s, "^[a-zA-Z]\\w{3,14}$"))
                return true;
            else
                return false;
        }
    }
}
