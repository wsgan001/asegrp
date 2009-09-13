using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
using AutomatonToCode;

namespace RegExpChecker_10_26_2008_10_14_53_PM_1
{
    [PexClass]
    public partial class _1_1_20_10_26_2008_10_14_53_PM_1
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method1(StringWrapper s)
        {
            if (s == null)
                return false;
            if (Regex.IsMatch(s.getS(), "^[a-zA-Z]\\w{3,14}$"))
                return true;
            else
                return false;
        }
    }
}

