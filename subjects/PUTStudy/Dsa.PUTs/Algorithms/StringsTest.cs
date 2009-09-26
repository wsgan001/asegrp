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
       * Generalizes 4 tests ReverseTest, ReverseEmptyStringTest, ReverseStringOfLength1Test, ReverseNullStringTest
       * Time: 00:04:49
       * Instrumentation issue = none
       * Patterns: AAAA, AllowedException
       * Pex Limitations = none
      **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void ReversePUT(string input)
        {
            if(input == null)
            {
                input.Reverse();
                PexAssume.IsFalse(true,"Exception should have been thrown");
            }
            Regex charPattern = new Regex(@"^[a-zA-Z]*$");
            PexAssume.IsTrue(charPattern.IsMatch(input));
            string actual = input.Reverse();
            string expected = "";
            foreach(char c in input.ToCharArray())
            {
                expected = c.ToString()+expected;
            }
            PexAssert.AreEqual(expected, actual);
        }

        /**
         * Generalizes 4 tests AnyMatchNullException, AnyWordNullTest, AnyNoMatchingCharTest, AnyMatchingCharTest
         * Time: 00:14:23
         * Instrumentation issue = none
         * Patterns: AAAA, AllowedException
         * Pex Limitations = none
         * Failing test case - 1:Index out of bound exception - REAL bug due to incrementing twice inside the for loop and while loop
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void AnyMatchingPUT(string basestring, string input)
        {
            if (basestring == null || input == null)
            {
                basestring.Any(input);
                PexAssert.IsTrue(false, "Exception should have been thrown");
            }
            Regex charPattern = new Regex(@"^[a-zA-Z\s]*$");
            basestring = Regex.Replace(basestring, @"[\s]+", "            ");
            input = Regex.Replace(input, @"[\s]+", "            ");
            int actual = basestring.Any(input);
            /** compute expected **/
            int expected = input.IndexOfAny(basestring.ToCharArray());
            PexAssert.AreEqual(expected, actual);
        }

    /**
     * Generalizes 1 test IsPalindromePuncAndWhitespaceIgnoredTest
     * Time: 00:02:54
     * Instrumentation issue = none
     * Patterns: AAAA
     * Pex Limitations = none
     * Failing test case - 1:Index out of bound exception
    **/
        [PexMethod]
        public void IsPalindromeWithSymbolsPUT([PexAssumeUnderTest]string actual)
        {
            Regex charPattern = new Regex(@"^[a-zA-Z\s!]*$");
            PexAssume.IsTrue(charPattern.IsMatch(actual));
            PexAssume.IsTrue(actual.Length > 0);
            bool result = actual.IsPalindrome();
            actual = Regex.Replace(actual, "\n", "");
            actual = actual.ToLower().Strip();
            string reverse = actual.Reverse();
            bool expected = actual.Equals(reverse);
            PexAssert.AreEqual(expected, result);
        }

        /**
        * Generalizes 3 tests IsPalindromeSingleWordTest, IsPalindromeCaseInsensitiveTest, IsPalindromeSingleCharTest
        * Time: 00:09:09
        * Instrumentation issue = none
        * Patterns: AAAA, AllowedException
        * Pex Limitations = none
        * Failing test case - 1: as the code under test does not handle empty strings (surprisingly handles null values)
       **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void IsPalindromePUT(string actual)
        {
            Regex charPattern = new Regex(@"^[a-zA-Z]*$");
            PexAssume.IsTrue(charPattern.IsMatch(actual));
            bool result = actual.IsPalindrome();
            actual = Regex.Replace(actual,"\n","");
            actual = actual.ToLower();
            string reverse = actual.Reverse();
            bool expected = actual.Equals(reverse);
            PexAssert.AreEqual(expected, result);
        }

      


        /**
        * Generalizes 2 tests StripNullStringTest, StripTest
        * Time: 00:10:36
        * Instrumentation issue = none
        * Patterns: AAAA, AllowedException
        * Pex Limitations = none
        * Failing test case - 1: error due to invalid characters like \0 or \u008 and likes - Not sure if it is a defect
       **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void StripPUT(string actual)
        {
            Regex charPattern = new Regex(@"[a-zA-Z]");
            PexAssume.IsTrue(charPattern.IsMatch(actual));
            PexAssume.IsFalse(actual.Contains("\0"));
            string expected = Regex.Replace(actual, "[^a-zA-Z]", "");
            PexAssert.AreEqual(expected, actual.Strip());
        }


        /**
         * Generalizes 2 tests WordCountPureWhiteSpace, WordCountWhitespaceTest
         * Time: 00:6:59
         * Instrumentation issue = none
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test case - 1 - does not treat \t as space, when input is of format \f A\t\t\ta, word count should be 2 (since there are actually 2 words, "A" and "a", but returns only 1)
        **/
        [PexMethod]
        public void WordCountSpacePUT([PexAssumeUnderTest]string actual)
        {
            Regex charPattern = new Regex(@"^[a-zA-Z\s]*$");
            PexAssume.IsTrue(charPattern.IsMatch(actual));
            PexAssume.IsTrue(actual.Length > 0);
            /** expected**/
            string temp = actual.Trim();
            int expected = 0;
            if (temp.Length != 0)
            {
                char[] splitter = { ' ','\t','\f'};
                string[] splits = temp.Split(splitter);
                foreach(string split in splits)
                {
                    if (split.Trim().Length > 0)
                        expected++;
                }
            }
                
            PexAssert.AreEqual(expected, actual.WordCount());
        }

        /**
         * Generalizes three WordCountTest, WordCountNullArgTest
         * Time: 00:10:22
         * Instrumentation issue = none
         * Patterns: AAAA, AllowedException
         * Pex Limitations = none
         * Failing test = 1 - Code under test does not deal with empty strings i.e.,""
         *                1 - Invalid string was generated, Might not be a valid error
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void WordCountPUT(string actual)
        {
            Regex nonCharPattern = new Regex(@"[^\sa-zA-Z]$");
            PexAssume.IsFalse(nonCharPattern.IsMatch(actual));
            /** actual**/
            int expected = actual.WordCount();
            /** compute expected **/
            char[] splitter = {' '};
            string[] splits = actual.Split(splitter);
            int count = 0;
            for (int i = 0; i < splits.Length; i++)
            {
                string value = splits[i].Trim();
                if (value.Length > 0)
                {
                    count++;
                }
            }
            PexAssert.AreEqual(count, actual.WordCount());
        }

        /**
        * One PUT for ReverseWordsTest, ReverseWordsStringNullTest, ReverseWordsWhiteSpaceTest
        * Time: 00:14:09
        * Instrumentation issue = none
        * Patterns: AAAA, AllowedException
        * Pex Limitations = none
        * Failing test = 1: Code under test does not deal with empty strings i.e.,"" 
        *                2: for invalid strings, not sure if we should report them
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
            Regex nonCharPattern = new Regex(@"[^a-zA-Z\s]");
            PexAssume.IsFalse(nonCharPattern.IsMatch(input));
            input = input.TrimEnd();
            char[] splitter = {' '};
            String[] splits = input.Split(splitter);
            string reverse = "";
            for(int i=0; i < splits.Length; i++)
            {
                string splitStr = splits[i];
                if (!splitStr.Equals(" ") || (reverse.Length > 0 && reverse.TrimEnd().Equals(reverse)))
                {
                    if (reverse.Length > 0)
                        reverse = splitStr + " " + reverse.TrimStart();
                    else
                        reverse = splitStr;
                }
            }
            PexAssert.AreEqual(reverse.TrimEnd(), input.ReverseWords());
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

        /**Did not generalize**/
        [PexMethod]
        public void RepeatedWordCountWithPunctuationPUT()
        {
            const string s = "Granville is hopeless. But is still persisting though! poor Granville!";

            PexAssert.AreEqual(2, s.RepeatedWordCount());
        }
       
        /*public void RepeatedWordCountWithPunctuationPUT([PexAssumeUnderTest]string s)
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
            
            
        }*/

    }
}