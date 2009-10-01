using System;
using Dsa.Algorithms;
using System.Collections;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Validation;
using NUnit.Framework;

namespace Dsa.PUTs.Algorithms
{
    /// <summary>
    /// PUTs for Searching.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class SearchingTest
    {
        /**
         * Generalizes SequentialSearchListNullTest, SequentialSearchItemNotPresentTest, SequentialSearchItemPresentTest
         * Time: 00:03:40
         * Instrumentation issue = 0
         * Patterns: AAA, AllowedException
         * Pex Limitations = none
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void SequentialSearchItemPUT(int[] actual, int actualVal)
        {
            int position = -1;
            if (actual != null)
            {
                for (int i = 0; i < actual.Length; i++)
                {
                    if (actual[i] == actualVal)
                    {
                        position = i;
                        break;
                    }
                }
            }
            int actualPosition = actual.SequentialSearch(actualVal);
            PexAssert.AreEqual(position, actualPosition);
        }

        /**
         * The following 2 PUTs generalize ProbabilitySearchTest, ProbabilitySearchItemNotFoundTest, ProbabilitySearchItemAtFrontOfArrayTest, ProbabilitySearchListNullTest
         * Time: 00:21:31
         * Instrumentation issue = 0
         **/
        /**
         * Patterns: SanitizedRoundtrip, AAA
         * Pex Limitations = none
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void ProbabilitySearchListPUT1(int[] actual, int actualVal)
        {
            bool exists = false;
            int position = -1;
            actual.QuickSort<int>();
            for (int i = 0; i < actual.Length; i++)
            {
                if (actual[i] == actualVal)
                {
                    position = i;
                    exists = true;
                    break;
                }
            }
            if (exists)
            {
                actual.ProbabilitySearch(actualVal);
                if(position > 0)
                    PexAssert.AreEqual(actualVal, actual[position-1]);
                else
                    PexAssert.AreEqual(actualVal, actual[position]);
            }
        }

        /**Look comments for the PUT above*/
        /**
        * Patterns: AAA, AllowedException
        * Pex Limitations = none
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void ProbabilitySearchListPUT(int[] actual,int actualVal)
        {
            bool exists = false;
            actual.QuickSort<int>();
            for (int i = 0; i < actual.Length; i++)
            {
                if (actual[i] == actualVal)
                {
                    exists = true;
                    break;
                }
            }
            bool actualExists = actual.ProbabilitySearch(actualVal);
            PexAssert.AreEqual(exists, actualExists);
        }
    }
}