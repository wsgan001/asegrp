using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
namespace RegExpChecker_10_26_2008_10_14_58_PM_510
{
    [PexClass]
    public partial class _1_1_20_10_26_2008_10_14_58_PM_510
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method510(string s)
        {
            if (s == null)
                return false;
            if (Regex.IsMatch(s, "^((CN=((\\'|\\w|\\d|\\s|\\-|\\.)+(/)*(,)*)+,\\s*)*(OU=((\\'|\\w|\\d|\\s|\\-|\\.)+(/)*(,)*)+,\\s*)*(DC=(\\'|\\w|\\d|\\s|\\-)+[,]*\\s*){1,}(DC=(\\'|\\w|\\d|\\s|\\-)+\\s*){1})$"))
                return true;
            else
                return false;
        }

    }
}

