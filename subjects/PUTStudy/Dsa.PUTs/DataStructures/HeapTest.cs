using System;
using System.Collections.Generic;
using Dsa.DataStructures;
using Dsa.Algorithms;
using Microsoft.Pex.Framework;
using System.Collections.ObjectModel;
using Microsoft.Pex.Framework.Validation;
using NUnit.Framework;
//using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Dsa.PUTs.DataStructures
{
    /// <summary>
    /// Tests for Heap.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class HeapTest
    {
        /**
       * Generalize AddTest
       * Time: 00:03:36
       * Instrumentation issue = 0
       * Patterns: AAA, Allowed Exception
       * Pex Limitations = none
       * Failing test = none
       **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void AddPUT(Collection<int> input)
        {
            Heap<int> actual = new Heap<int> (input);

            PexAssert.AreEqual(input.Count, actual.Count);
        }

        /**
         * Generalize MaxHeapTest
         * Time: 00:08:57
         * Instrumentation issue = 0
         * Patterns: Manual output
         * Pex Limitations = none
         * Failing test = 0
         **/
        [PexMethod]
        public void MaxHeapPUT([PexAssumeNotNull]List<int> input)
        {
            
            Heap<int> actual = new Heap<int>(input, Strategy.Max);
            PexObserve.ValueForViewing<int[]>("Actual", actual.ToArray());
        }

        /**
        * Generalize MaxHeapTest
        * Time: 00:07:30
        * Instrumentation issue = 0
        * Patterns: Manual output
        * Pex Limitations = none
        * Failing test = 0
        **/

        [PexMethod]
        public void MinHeapPUT([PexAssumeNotNull]List<int> input)
        {
            Heap<int> actual = new Heap<int>(input, Strategy.Min);
            PexObserve.ValueForViewing<int[]>("Actual", actual.ToArray());
        }

        /**
       * Generalize RemoveRootTest, RemoveItemNotPresentTest, RemoveLastItemTest
       * Time: 00:14:37
       * Instrumentation issue = 0
       * Patterns: AAA
       * Pex Limitations = none
       * Failing test = 1: Assertion failure due to a defect in the code under test - when randomPick = 0 and input does not contain 0, Remove method returns true
       * Comment: An example of how generlizing can sometimes make the test case lose its test objective. Here we cannot discrimate the test of Removing a normal item and a root!
       **/
        [PexMethod]
        public void RemoveItemPUT([PexAssumeNotNull]List<int> input, int randomPick)
        {
            //PexAssume.IsTrue(input.Contains(randomPick));
            Heap<int> actual = new Heap<int> (input);
            if (input.Contains(randomPick))
            {
                PexAssert.IsTrue(actual.Remove(randomPick));
                PexAssert.AreEqual(input.Count - 1, actual.Count);
                CollectionAssert.IsSubsetOf(actual, input);
            }
            else
            {
                PexAssert.IsFalse(actual.Remove(randomPick));
                PexAssert.AreEqual(input.Count, actual.Count);
                CollectionAssert.AreEquivalent(actual, input);
            }
        }

        /**
        * Generalize RemoveMaxHeapTest
        * Time: 00:12:26
        * Instrumentation issue = 0
        * Patterns: AAAA, Manual output
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void RemoveMaxHeapTest([PexAssumeNotNull]int[] input)
        {
            //PexAssume.IsTrue(input.Length > 0);
            PexAssume.AreDistinctValues<int>(input);
            List<int> temp = new List<int>(input);
            temp.Sort();
            List<int> inputWithoutMax = new List<int>(input);
            int max = temp.ToArray()[temp.Count-1];
            inputWithoutMax.Remove(max);
            Heap<int> actual = new Heap<int>(input, Strategy.Max);
            Heap<int> expected = new Heap<int>(inputWithoutMax, Strategy.Max);
            PexAssert.IsTrue(actual.Remove(max));
            PexObserve.ValueForViewing<int[]>("Expected", expected.ToArray());
            PexObserve.ValueForViewing<int[]>("Actual", actual.ToArray());
            CollectionAssert.AreEquivalent(expected, actual);
        }


        /**
         * Did not generalize RemoveRightChildLessThanLeftTest, RemoveMaxRightChildGreaterTest
         */
        [PexMethod]
        public void RemoveRightChildLessThanLeftTest()
        {
            Heap<int> actual = new Heap<int> { 5, 3, 8, 10, 6, 11, 12, 13 };
            Heap<int> expected = new Heap<int> { 3, 6, 8, 10, 13, 11, 12 };

            PexAssert.IsTrue(actual.Remove(5));
            PexAssert.AreEqual(7, actual.Count);
            CollectionAssert.AreEqual(expected, actual);
        }

        [PexMethod]
        public void RemoveMaxRightChildGreaterTest()
        {
            Heap<int> actual = new Heap<int>(Strategy.Max) { 46, 23, 44, 66, 51, 32, 17, 8 };
            Heap<int> expected = new Heap<int>(Strategy.Max) { 66, 46, 44, 23, 8, 32, 17 };

            PexAssert.IsTrue(actual.Remove(51));
            PexAssert.AreEqual(7, actual.Count);
            CollectionAssert.AreEqual(expected, actual);
        }
        /*************************************************************************************/


        /**
         * Generalize ToArrayTest
         * Time: 00:03:20
         * Instrumentation issue = 0
         * Patterns: AAA, Manual output
         * Pex Limitations = none
         * Failing test = none
         * Comment: Forced stop
         **/
        [PexMethod]
        public void ToArrayPUT([PexAssumeNotNull]List<int> input)
        {
            Heap<int> actual = new Heap<int>(input);
            PexObserve.ValueForViewing<int[]>("Expected", input.ToArray());
            PexObserve.ValueForViewing<int[]>("Actual", actual.ToArray());
            CollectionAssert.AreEquivalent(input.ToArray(), actual.ToArray());
        }

        /**
         * Generalize ContainsMaxHeapTest, ContainsMinHeapTest
         * Time: 00:03:26
         * Instrumentation issue = 0
         * Patterns: AAA
         * Pex Limitations = none
         * Failing test = none
         * Comment: Forced stop
         **/
        [PexMethod]
        public void ContainsPUT([PexAssumeNotNull]List<int> input, int randomPick)
        {
            Heap<int> minHeap = new Heap<int>(input);
            Heap<int> maxHeap = new Heap<int>(input,Strategy.Max); ;
            if (input.Contains(randomPick))
            {
                PexAssert.IsTrue(minHeap.Contains(randomPick));
                PexAssert.IsTrue(maxHeap.Contains(randomPick));
            }
            else
            {
                PexAssert.IsFalse(minHeap.Contains(randomPick));
                PexAssert.IsFalse(maxHeap.Contains(randomPick));
            }
        }

        /**
         * Generalize ClearTest
         * Time: 00:01:26
         * Instrumentation issue = 0
         * Patterns: AAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void ClearPUT([PexAssumeNotNull]List<int> input)
        {
            Heap<int> actual = new Heap<int> (input);
            actual.Clear();
            PexAssert.AreEqual(0, actual.Count);
        }

        /**
        * Generalizes IndexerTest, IndexOutOfRangeGreaterThanIndexTest, IndexOutOfRangeLessThanIndexTest
        * Time: 00:03:37
        * Instrumentation issue = 0
        * Patterns: AAA, Manual output
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentOutOfRangeException))]
        public void IndexerPUT([PexAssumeNotNull]List<int> input, int randomPick)
        {
            Heap<int> actual = new Heap<int>(input);
            if (randomPick > -1 && randomPick < input.Count)
            {
                PexObserve.ValueForViewing<int>("Value at index" + randomPick, actual[randomPick]);
            }
            else
            {
                int val = actual[randomPick];
                PexAssert.IsTrue(false, "ArgumentOutOfRangeException was expected");
            }
        }

        /**
        * Generalizes CopyConstructorTest, CopyConstructorWithStrategyTest
        * Time: 00:02:23
        * Instrumentation issue = 0
        * Patterns: AAA
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void CopyConstructorPUT([PexAssumeNotNull]string[] input)
        {
            List<string> collection = new List<string>(input);
            Heap<string> actual = new Heap<string>(collection);
            Heap<string> actualAnother = new Heap<string>(collection,Strategy.Max);
            PexAssert.AreEqual(input.Length, actual.Count);
            PexAssert.AreEqual(input.Length, actualAnother.Count);
        }

    }
}