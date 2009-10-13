using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
namespace RegExpChecker_10_26_2008_10_14_59_PM_692
{
    [PexClass]
    public partial class _1_1_20_10_26_2008_10_14_59_PM_692
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method692(string s)
        {
            if (s == null)
                return false;
            if (Regex.IsMatch(s, "^[a-zA-Z]\\:\\\\((\\w|[\\u0621-\\u064A]|\\s)+\\\\)+(\\w|[\\u0621-\\u064A]|\\s)+(\\.jpg|\\.JPG|\\.gif|\\.GIF|\\.BNG|\\.bng)"))
                return true;
            else
                return false;
        }
    }
}

