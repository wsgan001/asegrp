using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;
using System.Text.RegularExpressions;

namespace inject.faults.email2
{

    [PexClass]
    public partial class Email2
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method1(string s)
        {
            if (s == null)
                return false;
            if (System.Text.RegularExpressions.Regex.IsMatch(s, @"^[A-Za-z0-9](([_\.\- ]?[a-zA-Z0-9]+)*)@([A-Za-z0-9]+)(([\.\-_]?[a-zA-Z0-9]+)*)\.([A-Za-z0-9][A-Za-z0-9]+)$"))
                return true;
            else
                return false;
        }        
    }



}


