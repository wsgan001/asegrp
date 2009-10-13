using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
namespace RegExpChecker_10_26_2008_10_14_54_PM_105
{
[PexClass]
public partial class _1_1_20_10_26_2008_10_14_54_PM_105
{
    [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
    public bool Method105(string s)
    {
        if (s == null)
            return false;
        if (Regex.IsMatch(s, "^(([A-Za-z0-9]+_+)|([A-Za-z0-9]+\\-+)|([A-Za-z0-9]+\\.+)|([A-Za-z0-9]+\\++))*[A-Za-z0-9]+@((\\w+\\-+)|(\\w+\\.))*\\w{1,63}\\.[a-zA-Z]{2,6}$"))
            return true;
        else
            return false;
    }
}
}

