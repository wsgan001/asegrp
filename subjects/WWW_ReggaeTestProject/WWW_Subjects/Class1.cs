using System;
//using System.Collections.Generic;
using System.Linq;
//using System.Text;
using Microsoft.Pex.Framework;
using RegExpChecker;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Reggae.WWW_Subjects
{
    [PexClass]
    [TestClass]
    public partial class Email
    {
        //Brics
        #region Test_com.ecyrd.jspwiki.ui.InputValidator
        /*
         *  @"^[0-9a-zA-Z-_\.\+]+@([0-9a-zA-Z-_]+\.)+[a-zA-Z]+$"
         */
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method1(string s)
        {
            if (s == null)
                return false;
            PexObserve.ValueForViewing<bool>("Actual Regex says",System.Text.RegularExpressions.Regex.IsMatch(s, @"^[0-9a-zA-Z-_\.\+]+@([0-9a-zA-Z-_]+\.)+[a-zA-Z]+$"));
            if (Regex.IsMatch(s, @"^[0-9a-zA-Z-_\.\+]+@([0-9a-zA-Z-_]+\.)+[a-zA-Z]+$"))
                return true;
            else
                return false;
        }
        #endregion

        //Brics
        #region Test_com.citep.web.gwt.validators.EmailValidator
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method2(string s)
        {
            if (s == null)
                return false;
            String pat = @"^[a-zA-Z]+(([\'\,\.\- ][a-zA-Z])?[a-zA-Z]*)*\s+<(\w[\-._\w]*\w@\w[\-._\w]*\w\.\w{2,4})>$|^(\w[\-._\w]*\w@\w[\-._\w]*\w\.\w{2,4})$";
            PexObserve.ValueForViewing<bool>("Actual Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, pat));
            if (Regex.IsMatch(s, pat))
                return true;
            else
                return false;
        }
        #endregion

        //Invalid expression!
        #region Test_com.duguo.dynamicmvc.model.validator.EmailValidator

        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method3(string s)
        {
            if (s == null)
                return false;
            String pat = @"^[\\w-\\.]+@([\\w\\-]+\\.)+[\\w\\-]{2,4}$";
            //PexObserve.ValueForViewing<bool>("Actual Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, @"^[0-9a-zA-Z-_\.\+]+@([0-9a-zA-Z-_]+\.)+[a-zA-Z]+$"));
            if (System.Text.RegularExpressions.Regex.IsMatch(s, pat))
                return true;
            else
                return false;
        }
        #endregion

        //Brics
        #region Test_com.talent.utils.EmailValidator
        /*
         *  @"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
         */
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method4(string s)
        {
            if (s == null)
                return false;
            //PexObserve.ValueForViewing<bool>("Actual Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, @"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"));
            String pat =  @"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]" + "{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
            //Console.WriteLine(System.Text.RegularExpressions.Regex.IsMatch("a@g.rr", pat));
            if (Regex.IsMatch(s,pat))
            {
                //Console.WriteLine(System.Text.RegularExpressions.Regex.IsMatch("a@g.rr", pat));
                return true;
            }
            else
                return false;

        }
        #endregion

        //Brics + Direct - Multiple regular expressions
        #region Test_Email_edu.harvard.hmdc.vdcnet.util.EmailValidator

        
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method51(string s)
        {
            if (s == null)
                return false;
            String pat = "^\\.|^\\@";
            if (Regex.IsMatch(s, pat))
            {
                PexObserve.ValueForViewing("System.Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, pat));
                return true;
            }
            else
                return false;
           
        }

        //Direct Pex
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method52(string s)
        {
            if (s == null)
                return false;
            if (System.Text.RegularExpressions.Regex.IsMatch(s, @"\\@$"))
                return true;
            else
                return false;
        }

        //Brics
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method53(string s)
        {
            if (s == null)
                return false;
            String pat = @"[^A-Za-z0-9\.\@_\-~#]+";
            PexObserve.ValueForViewing("System.Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, pat));
            if (Regex.IsMatch(s,pat))
                return true;
            else
                return false;
        }


        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method54(string s)
        {
            if (s == null)
                return false;
            String pat = "@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+";
            if (Regex.IsMatch(s, pat))
            {
                PexObserve.ValueForViewing("System.Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, pat));
                return true;
            }
            else
                return false;
        }


        #endregion

        //Brics
        #region Test_net.sf.hippopotam.presentation.field.validator.EmailValidator

        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method6(string s)
        {
            if (s == null)
                return false;
            String pat = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9]+[a-zA-Z0-9_-]*(\\.[a-zA-Z0-9_-]+)*(\\.[a-zA-Z0-9][a-zA-Z0-9-]{0,10}[a-zA-Z0-9])$";
            if (Regex.IsMatch(s, pat))
            {
                PexObserve.ValueForViewing("System.Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, pat));
                return true;
            }
            else
                return false;
        }
        #endregion


    }

    [TestClass]
    [PexClass]
    public partial class Url
    {
        //Brics
        #region Test_com.axiomos.util.UrlValidator
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method1(string s)
        {
            if (s == null)
                return false;
            PexObserve.ValueForViewing<bool>("Actual Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, "http\\:\\/\\/\\S+\\.\\S+"));
            if (RegExpCheckerForUrls.Regex.IsMatch(s, @"http\\:\\/\\/\\S+\\.\\S+"))
                return true;
            else
                return false;
        }
        #endregion

        //Brics : Problem with Regular expression - Automata is correct
        // Regular expression should have been "(http:[/][/]|www\\.)([a-z]|[A-Z]|[0-9]|[/\\.]|[~])*"
        #region Test_simple.RegExValidator
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method4(string s)
        {
            if (s == null)
                return false;
            //PexObserve.ValueForViewing<bool>("Actual Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, @"(http:[/][/]|www.)([a-z]|[A-Z]|[0-9]|[/.]|[~])*"));
            if (RegExpCheckerForUrls.Regex.IsMatch(s, "(http:[/][/]|www.)([a-z]|[A-Z]|[0-9]|[/.]|[~])*"))
                return true;
            else
                return false;
        }
        #endregion

        //Brics
        #region de.x4technology.validator.UrlValidator
        [TestMethod]
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue, Timeout = 240)]
        public bool Method3([PexAssumeUnderTest]string s)
        {
            PexObserve.ValueForViewing<bool>("Actual Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, @"^(((ht|f)tp(s?))\\://)?(www.|[a-zA-Z0-9].)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,6}(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&%\\$#\\=~_\\-]+))*$"));
            if (RegExpCheckerForUrls.Regex.IsMatch(s, @"^(((ht|f)tp(s?))\\://)?(www.|[a-zA-Z0-9].)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,6}(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&%\\$#\\=~_\\-]+))*$"))
                return true;
            else
                return false;


        }

        [TestMethod]
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue, Timeout = 240)]
        public bool Method31([PexAssumeUnderTest]string s)
        {
            // PexObserve.ValueForViewing<bool>("Actual Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, @"^(((ht|f)tp(s?))\\://)?(www.|[a-zA-Z0-9].)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,6}(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&%\\$#\\=~_\\-]+))*$"));
            if (System.Text.RegularExpressions.Regex.IsMatch(s, @"^(((ht|f)tp(s?))\\://)?(www\\.|[aA9]\\.)[aA9\\-\\.]{1,2}\\.[aA]{2,6}(\\:[9]{1,2}){0,1}(/($|[aA9\\.\\,\\;\\?\\'\\\\\\+&%\\$#\\=~_\\-]{1,2})){0,1}$"))
                return true;
            else
                return false;


        }
        #endregion

        //Brics
        #region Test_Xnfo.Validator
        [TestMethod]
        [PexMethod(MaxRuns = 1000, MaxConditions = int.MaxValue, MaxRunsWithoutNewTests = int.MaxValue, MaxBranches = int.MaxValue)]
        public bool Method5([PexAssumeUnderTest]string s)
        {
            PexObserve.ValueForViewing<bool>("Actual Regex says", System.Text.RegularExpressions.Regex.IsMatch(s, @"^(http|ftp)://[-!#$%&'*+\\0-9=?A-Z^_`a-z{|}~\./:]+$"));
            if (RegExpCheckerForUrls.Regex.IsMatch(s, @"^(http|ftp)://[-!#$%&'*+\\0-9=?A-Z^_`a-z{|}~\./:]+$"))
                return true;
            else
                return false;
        }
        #endregion

        //Not able to understand the expression: org.apache.commons.validator.UrlValidator - "/^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?/"
    }
}
