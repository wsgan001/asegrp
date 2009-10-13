using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
namespace RegExpChecker_10_26_2008_10_14_57_PM_448
{
[PexClass]
public partial class _1_1_20_10_26_2008_10_14_57_PM_448
{
[PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
    public bool Method448(string s)
    {
        if (s == null)
            return false;
        if (Regex.IsMatch(s, "^(([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}){1,25})+([;.](([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}){1,25})+)*$"))
            return true;
        else
            return false;
    }

}
}

