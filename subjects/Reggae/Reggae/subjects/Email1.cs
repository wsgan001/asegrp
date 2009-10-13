using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;
using System.Text.RegularExpressions;

namespace inject.faults.email1
{
    
        [PexClass]
        public partial class Email1
        {
            [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
            public bool Method1(string s)
            {
                if (s == null)
                    return false;
                if (Regex.IsMatch(s, @"^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*([,;]\s*\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*)*$"))
                    return true;
                else
                    return false;
            }           
        }
    


}
