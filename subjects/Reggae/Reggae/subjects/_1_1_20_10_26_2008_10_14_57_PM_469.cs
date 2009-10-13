using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
namespace RegExpChecker_10_26_2008_10_14_57_PM_469
{
    [PexClass]
    public partial class _1_1_20_10_26_2008_10_14_57_PM_469
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method469(string s)
        {
            if (s == null)
                return false;
            if (Regex.IsMatch(s, "((http\\://|https\\://|ftp\\://)|(www\\.))+(([a-zA-Z0-9\\.-]+\\.[a-zA-Z]{2,4})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(/[a-zA-Z0-9\\%\\:/-_\\?\\.'~]*)?"))
                return true;
            else
                return false;
        }
    }
}

