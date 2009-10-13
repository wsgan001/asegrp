using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;
using System.Text.RegularExpressions;

namespace inject.faults.misc1
{
    [PexClass]
    public partial class MISC1
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method1(string s)
        {
            if (s == null)
                return false;
            if (System.Text.RegularExpressions.Regex.IsMatch(s, "^([A-Z]{2}|[a-z]{2} \\d{2} [A-Z]{1,2}|[a-z]{1,2} \\d{1,4})?([A-Z]{3}|[a-z]{3} \\d{1,4})?$"))
                return true;
            else
                return false;
        }
    }
}
