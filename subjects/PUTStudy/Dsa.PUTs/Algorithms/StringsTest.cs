using System;
using Dsa.Algorithms;
using Microsoft.Pex.Framework;
using System.Text.RegularExpressions;
using System.Collections;
using System.Collections.Generic;
using Microsoft.Pex.Framework.Validation;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Dsa.Test.Algorithms
{
    /// <summary>
    /// Tests for the Strings algorithms.
    /// </summary>
    [TestClass]
    [PexClass]
    public sealed partial class StringsTest 
    {
        /**
        * One PUT for ReverseWordsTest, ReverseWordsStringNullTest
        * Time: 00:14:09
        * Instrumentation issue = none
        * Patterns: AAAA, AllowedException
        * Pex Limitations = none
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void ReverseWordsPUT(string input)
        {
            if (input == null)
            {
                input.Reverse();
                PexAssert.IsFalse(true, "Null exception was supposed to be thrown");
            }
            Regex objAlphaNumericPattern = new Regex(@"[a-zA-Z\s]*");
            PexAssume.IsTrue(objAlphaNumericPattern.IsMatch(input));
            char[] splitter = { ' ' };
            String[] splits = input.Split(splitter);
            PexAssume.IsTrue(splits.Length > 2);
            string reverse = "";
            for(int i=0; i < splits.Length; i++)
            {
                string splitStr = splits[i].Trim();
                if (splitStr.Length > 0)
                    if (reverse.Length > 0)
                        reverse = splitStr + " " + reverse;
                    else if (i > 0)
                        reverse = " " + splitStr;
                    else
                        reverse = splitStr;
            }
            PexAssert.AreEqual(reverse, input.ReverseWords());
        }

        /**
         * One PUT for RepeatedWordCountTest, RepeatedWordCountStringNullTest
         * Time: 00:15:56
         * Instrumentation issue = 1 
         * Patterns: AAAA, AllowedException
         * Pex Limitations = none
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void RepeatedWordCountPUT(string actual)
        {
            if (actual == null)
            {
                actual.RepeatedWordCount();
                PexAssert.IsFalse(true, "Null exception was supposed to be thrown");
            }
            Regex objAlphaNumericPattern = new Regex(@"[a-zA-Z\s]");
            Regex nonCharPattern = new Regex(@"[^a-zA-Z]");
            PexAssume.IsTrue(objAlphaNumericPattern.IsMatch(actual));
            char[] splitter = { ' ' };
            String[] splits = actual.Split(splitter);
            PexAssume.IsTrue(splits.Length > 2);
            HashSet<string> uniques = new HashSet<string>();
            for (int i = 0; i < splits.Length; i++)
            {
                String splitStr = splits[i];
                splitStr = Regex.Replace(splitStr, nonCharPattern.ToString(), "");
                    uniques.Add(splitStr);
            }
            PexAssert.AreEqual(splits.Length - uniques.Count, actual.RepeatedWordCount());
        }

        /**
         * INCOMPLETE
         **/
        [PexMethod]
       
        public void RepeatedWordCountWithPunctuationTest([PexAssumeUnderTest]string s)
        {
            
                Regex objAlphaNumericPattern = new Regex(@"[a-zA-Z0-9[:punc]\s]*");
                Regex nonCharPattern = new Regex(@"[^a-zA-Z]");
                PexAssume.IsTrue(objAlphaNumericPattern.IsMatch(s));
                //PexAssume.IsTrue(s.Contains(" "));
                char[] splitter = { ' ' };
                String[] splits = s.Split(splitter);
                PexAssume.IsTrue(splits.Length > 2);
                HashSet<string> uniques = new HashSet<string>();
                for (int i=0; i < splits.Length; i++)
                {
                    String splitStr = splits[i];
                    splitStr = Regex.Replace(splitStr, nonCharPattern.ToString(), "");
                    uniques.Add(splitStr);
                }
                PexAssert.AreEqual(splits.Length - uniques.Count, s.RepeatedWordCount());
                return;
            
            
        }
    }
}