using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
namespace RegExpChecker_10_26_2008_10_14_53_PM_9
{
    [PexClass]
    public partial class _1_1_20_10_26_2008_10_14_53_PM_9
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method9(string s)
        {
            if (s == null)
                return false;
            if (Regex.IsMatch(s, "^((\\w|\\d|\\-|\\.)+)\\@{1}(((\\w|\\d|\\-){1,67})|((\\w|\\d|\\-)+\\.(\\w|\\d|\\-){1,67}))\\.((([a-z]|[A-Z]|\\d){2,4})(\\.([a-z]|[A-Z]|\\d){2})?)$"))
                return true;
            else
                return false;
        }
    }
}

