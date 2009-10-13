using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;
using System.Text.RegularExpressions;

namespace inject.faults.price1
{

    [PexClass]
    public partial class Price1
    {        
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method1(string s)
        {
            if (s == null)
                return false;
            if (System.Text.RegularExpressions.Regex.IsMatch(s, @"^\$?(\d{1,3},?(\d{3},?)*\d{1,3}(\.\d{0,2})?|\d{1,3}(\.\d{0,2})?|\.\d{1,2}?)$"))
                return true;
            else
                return false;
        }
    }
}
