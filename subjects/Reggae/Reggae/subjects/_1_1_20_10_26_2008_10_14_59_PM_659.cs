using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
namespace RegExpChecker_10_26_2008_10_14_59_PM_659
{
    [PexClass]
    public partial class _1_1_20_10_26_2008_10_14_59_PM_659
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method659(string s)
        {
            if (s == null)
                return false;
            if (Regex.IsMatch(s, "^((\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)\\s*[,]{0,1}\\s*)+$"))
                return true;
            else
                return false;
        }

    }
}

