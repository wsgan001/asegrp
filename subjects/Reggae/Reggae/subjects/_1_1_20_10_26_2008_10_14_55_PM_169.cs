using System.Text.RegularExpressions;
using Microsoft.Pex.Framework;
namespace RegExpChecker_10_26_2008_10_14_55_PM_169
{
    [PexClass]
    public partial class _1_1_20_10_26_2008_10_14_55_PM_169
    {
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool method169(string s)
        {
            if (s == null)
                return false;
            if (Regex.IsMatch(s, "^(http|https|ftp)\\://([a-za-z0-9\\.\\-]+(\\:[a-za-z0-9\\.&amp;%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-za-z0-9\\-]+\\.)*[a-za-z0-9\\-]+\\.[a-za-z]{2,4})(\\:[0-9]+)?(/[^/][a-za-z0-9\\.\\,\\?\\'\\\\/\\+&amp;%\\$#\\=~_\\-@]*)*$"))
                return true;
            else
                return false;
        }

    }
}

