using System;
using System.Collections.Generic;
using Dsa.Algorithms;
using NUnit.Framework;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Validation;
using System.Text.RegularExpressions;
using Microsoft.Pex.Framework.Goals;
using System.Collections;

namespace Dsa.PUTs.Algorithms
{
    /// <summary>
    /// Sorting tests.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class SortingTest
    {

        /**
         * PUT generalizes BubbleSortNullArrayTest, MedianLeftLeftIsGreaterThanMidTest, MedianLeftTest, 
         * Time: 00:10:40
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing Test cases = none
         **/
        [PexMethod]
        public void BubbleSortDescPUT([PexAssumeUnderTest]IList<int> actual)
        {
            PexAssume.IsTrue(actual.Count > 1);

            IList<int> _expected = actual.ShellSort();
            int[] expected = new int[_expected.Count];
            int count = actual.Count - 1;
            for (int i = 0; i < _expected.Count; i++)
            {
                expected[count] = _expected[i];
                count--;
            }
            int[] result = actual.BubbleSort(SortType.Descending) as int[];
            for (int i = 0; i < actual.Count; i++)
            {
                PexAssert.AreEqual(expected[i], result[i]);
            }
        }
        /**
         * PUT generalizes BubbleSortNullArrayTest, MedianLeftLeftIsGreaterThanMidTest, MedianLeftTest, 
         * Time: 00:04:30
         * Instrumentation issue = 0
         * Patterns: AllowedException, AAA
         * Pex Limitations = none
         * Failing Test cases = none
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void BubbleSortAscPUT(IList<int> actual)
        {
            if (actual == null)
            {
                actual.BubbleSort(SortType.Ascending);
                PexAssert.IsTrue(false, "ArgumentNullException was expected");
            }
            IList<int> expected = actual.ShellSort();
            PexAssert.AreEqual(expected, actual.BubbleSort(SortType.Ascending));
        }

        /**
       * PUT generalizes MedianLeftArrayNullTest, MedianLeftLeftIsGreaterThanMidTest, MedianLeftTest, 
       * Time: 00:03:37
       * Instrumentation issue = 0
       * Patterns: AllowedException, Manual output
       * Pex Limitations = none
       * Failing Test cases - 1: Empty array not handled - ArgumendOutOfRangeException
       **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void MedianLeftArrayNullPUT(int[] input)
        {
            if (input == null)
            {
                input.MedianLeft();
                PexAssert.IsTrue(false, "ArgumentNullException was expected");
            }
            PexObserve.ValueForViewing<IList<int>>("MedianLeft output ",input.MedianLeft());
            
        }

        /**
        * PUT generalizes MergeOrderedArrayTwoNullTest, MergeOrderedArrayOneNullTest, MergeOrderedSecondArraySmallerTest, 
        *                 MergeOrderedFirstArraySmallerTest, MergeOrderedTest
        * Time: 00:07:53
        * Instrumentation issue = 0
        * Patterns: AAAA, AllowedException
        * Pex Limitations = none
        * Failing Test cases - 0
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void MergeOrderedArrayPUT(int[] input1, int[] input2)
        {
            if (input1 == null || input2 == null)
            {
                Sorting.MergeOrdered(input1, input2);
                PexAssert.IsTrue(false, "ArgumentNullException was expected");
            }
            input1.ShellSort();
            input2.ShellSort();
            PexObserve.ValueForViewing<IList<int>>("Sorted Input1", input1);
            PexObserve.ValueForViewing<IList<int>>("Sorted Input2", input2);
            IList<int> result = Sorting.MergeOrdered(input1, input2);

            IList<int>  expected = Sorting.Concatenate(input1, input2, new int[0]).MergeSort();
            PexObserve.ValueForViewing<IList<int>>("Sorting.MergeSort", result);
            PexObserve.ValueForViewing<IList<int>>("Concatenate Output", expected);
            for (int i = 0; i < result.Count; i++)
            {
                PexAssert.AreEqual(expected[i], result[i]);
            }
            
        }

        /**
        * PUT generalizes MergeSortTest
        * Time: 00:00:40
        * Instrumentation issue = 0
        * Patterns: AAAA
        * Pex Limitations = none
        * Failing Test cases - 0
        **/
        [PexMethod]
        public void MergeOrderedCharsPUT([PexAssumeUnderTest]int[] unsorted)
        {
            IList<int> actual = unsorted.MergeSort();
            IList<int> expected = unsorted.ShellSort();
            for (int i = 0; i < actual.Count; i++)
            {
                PexAssert.AreEqual(expected[i], actual[i]);
            }
        }

        /**
        * PUT generalizes MergeSortArrayNullTest, MergeOrderedCharsTest
        * Time: 00:05:19
        * Instrumentation issue = 0
        * Patterns: AAA, AllowedException
        * Pex Limitations = none
        * Failing Test cases - 0
        **/

        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void MergeOrderedCharsPUT(char[] unsorted)
        {
            if (unsorted == null)
            {
                unsorted.MergeSort();
                PexAssert.IsTrue(false, "ArgumentNullException was expected");
            }
            IList<char> actual = unsorted.MergeSort();
            IList<char> expected = unsorted.ShellSort();
            for (int i = 0; i < actual.Count; i++)
            {
                PexAssert.AreEqual(expected[i], actual[i]);
            }
        }

        /**
        * PUT generalizes ConcatenateTest, ConcatenateFirstListNullTest, ConcatenateSecondListNullTest, ConcatenateThirdListNullTest
        * Time: 00:15:01
        * Instrumentation issue = 0
        * Patterns: AAAA, AllowedException
        * Pex Limitations = none
        * Failing Test cases - 0
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void ConcatenatePUT(int[] inputArray1, int[] inputArray2, int[] inputArray3)
        {
           if(inputArray1 == null || inputArray1 == null || inputArray1 == null)
           {
               Sorting.Concatenate(inputArray1, inputArray2, inputArray3);
               PexAssert.IsTrue(false, "ArgumentNullException was expected"); 
           }
           PexAssume.AreNotEqual(inputArray1, inputArray2);
           PexAssume.AreNotEqual(inputArray3, inputArray2);
           IList<int> result = Sorting.Concatenate(inputArray1, inputArray2, inputArray3);
           PexAssert.AreEqual(inputArray1.Length + inputArray2.Length + inputArray3.Length, result.Count);
           int count = 0;
           for (int i = 0; i < inputArray1.Length; i++)
           {
               PexAssert.AreEqual(inputArray1[i], result[count]);
               count++;
           }
           for (int i = 0; i < inputArray2.Length; i++)
           {
               PexAssert.AreEqual(inputArray2[i], result[count]);
               count++;
           }
           for (int i = 0; i < inputArray3.Length; i++)
           {
               PexAssert.AreEqual(inputArray3[i], result[count]);
               count++;
           }
           PexObserve.ValueForViewing<IList<int>>("Concatenate Output", result);
        }

        /**
        * One PUT generalizes QuickSortListNullTest, QuickSortTest
        * Time: 00:07:37
        * Instrumentation issue = 0
        * Patterns: AAA, AllowedException
        * Pex Limitations = none
        * Failing Test cases - 0
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void QuickSortListNullPUT(char[] actual)
        {
            if (actual == null)
            {
                actual.QuickSort();
                PexAssert.IsTrue(false, "Expected ArgumentNullException");
            }
            char[] temp = new char[actual.Length];
            for (int i = 0; i < actual.Length; i++)
            {
                temp[actual.Length - (i + 1)] = actual[i];
            }
            IList<char> output = temp.QuickSort();
            IList<char> expected = actual.InsertionSort();
            for (int i = 0; i < actual.Length; i++)
            {
                PexAssert.AreEqual(expected[i], output[i]);
            }
            
        }


        /**
         * One PUT generalizes InsertionSortTest, InsertionSortListNullTest
         * Time: 00:04:59
         * Instrumentation issue = 0
         * Patterns: Manual Output,AllowedException
         * Pex Limitations = none
         * Failing Test cases - 0
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void InsertionSortPUT(int[] unsorted)
        {
            if (unsorted == null)
            {
                unsorted.InsertionSort();
                PexAssert.IsTrue(false, "Expected the exception to be thrown");
            }
            int[] temp = new int[unsorted.Length];
            for (int i = 0; i < unsorted.Length; i++)
            {
                temp[unsorted.Length - (i + 1)] = unsorted[i];
            }
            int[] actual = temp.InsertionSort() as int[];
            PexObserve.ValueForViewing<IList<int>>("Insertion sort result", actual);
            //PexAssert.AreEqual(unsorted.ShellSort(), actual);
        }

        /**
         * One PUT generalizes ShellSortListNullTest, ShellSortTest
         * Time: 00:15:59
         * Instrumentation issue = 0
         * Patterns: Manual Output, AllowedException
         * Pex Limitations = none
         * Failing Test cases - 0
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void ShellSortListPUT(char[] actual)
        {
            if (actual == null)
            {
                actual.ShellSort();
                PexAssert.IsTrue(false, "Exception was expected");
            }
            IList<char> result = actual.ShellSort();
            IList<char> expected = actual.InsertionSort();
            PexAssert.AreSame(expected, result);
            PexObserve.ValueForViewing<IList<char>>("Shell sort result", actual);
        }

        /**
        * One PUT generalizes RadixStringFixedKeyTest
        * Time: 00:16:17
        * Instrumentation issue = 0
        * Patterns: Manual Output
        * Pex Limitations = none
        * Failing Test cases - 0
        **/
        [PexMethod]
        public void RadixStringFixedKeyPUT([PexAssumeUnderTest]string[] actual, [PexAssumeUnderTest]int key)
        {
            PexAssume.IsTrue(key > 1);
            PexAssume.IsTrue(actual.Length > 1);
            Regex charPattern = new Regex(@"^[a-zA-Z]+$");
            HashSet<string> unique = new HashSet<string>();
            string[] temp = new string[actual.Length];
            for (int i = 0; i < actual.Length; i++)
            {
                PexAssume.IsNotNull(actual[i]);
                PexAssume.IsTrue(charPattern.IsMatch(actual[i]));
                PexAssume.IsTrue(actual[i].Length == key);
                unique.Add(actual[i]);
                temp[actual.Length - (i + 1)] = actual[i];
            }
            PexAssume.IsTrue(unique.Count > 1);
            PexObserve.ValueForViewing<IList<string>>("Input Array", temp);
            PexObserve.ValueForViewing<IList<string>>("RadixSort result", actual.RadixSort(key));
        }


        /**
         * One PUT generalizes RadixStringItemNullTest, RadixStringKeySizeLessThanOneTest, RadixStringListNullTest
         * Time: 00:04:17
         * Instrumentation issue = 0
         * Patterns: AllowedException, Manual Output
         * Pex Limitations = none
         * Failing Test cases - 1: IndexOutOfRangeException - When key value is greater than the strings' length
         *                      1: NullReferenceException - when any other value in the string[] is null (when the first string is null, the case is handled)
         * Comments: Classic example of how DSE based test generation can expose more defects. No assertions or assumption, 
         * simply executing the code under test results in failing test cases
         **/
        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        [PexAllowedException(typeof(ArgumentOutOfRangeException))]
        [PexAllowedException(typeof(ArgumentNullException))]
        [PexExpectedGoals]
        public void RadixStringItemPUT(string[] actual, int key)
        {
           // try
           // {
                actual.RadixSort(key);
           // }
            /*catch (InvalidOperationException ex)
            {
                PexGoal.Reached();
            }
            catch (ArgumentOutOfRangeException ex)
            {
                PexGoal.Reached();
            }
            catch (ArgumentNullException ex)
            {
                PexGoal.Reached();
            }*/
        }
    }
}