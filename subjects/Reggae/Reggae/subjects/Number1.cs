using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;
using System.Text.RegularExpressions;

namespace inject.faults.number1
{    
        [PexClass]
        public partial class Number1
        {           
           [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
           public bool Method1(string s)
           {
               if (s == null)
                   return false;
               if (System.Text.RegularExpressions.Regex.IsMatch(s, @"^[+-]?([0-9]*\.?[0-9]+|[0-9]+\.?[0-9]*)([eE][+-]?[0-9]+)?$"))
                   return true;
               else
                   return false;
           }
        }
}

