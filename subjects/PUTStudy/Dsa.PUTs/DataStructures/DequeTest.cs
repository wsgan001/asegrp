using System.Collections.Generic;
using Dsa.DataStructures;
using System;
using Microsoft.Pex.Framework;
using NUnit.Framework;
using Microsoft.Pex.Framework.Validation;
using System.Collections.ObjectModel;
using Microsoft.Pex.Framework.Goals;

namespace Dsa.PUTs.DataStructures
{
    [TestFixture]
    [PexClass]
    public partial class DequeTest
    {
        /*ALL PUTs Time: 00:15:10 */
        [PexMethod]
        public void EnqueueFrontPUT([PexAssumeNotNull]Collection<int> input, int val)
        {
            Deque<int> actual = new Deque<int>(input);
            int initCount = actual.Count;
            actual.EnqueueFront(val);
            PexAssert.AreEqual(initCount + 1, actual.Count);
        }

        [PexMethod]
        public void AddTest([PexAssumeNotNull]Collection<int> input)
        {
            Deque<int> actual = new Deque<int> (input);
            PexAssert.AreEqual(input.Count, actual.Count);
        }

        [PexMethod]
        public void EnqueueBackPUT(int val)
        {
            Deque<int> actual = new Deque<int>();
            actual.EnqueueBack(val);
            PexAssert.AreEqual(1, actual.Count);
        }

        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        public void DequeueFrontEmptyDequePUT([PexAssumeNotNull]Collection<int> input)
        {
            Deque<int> actual = new Deque<int>(input);
            CollectionAssert.AreEqual(input, actual);
            for (int i = 0; i < input.Count ; i++)
            {
                PexAssert.AreEqual(input[i], actual.PeekFront());
                PexAssert.AreEqual(input[i],actual.DequeueFront());
                PexAssert.AreEqual(input.Count - (i + 1), actual.Count);
            }
            
            PexObserve.ValueForViewing<Collection<int>>("Input Collection", input);
        }
        /**END ALL PUTs**/


        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        public void DequeueBackEmptyDequePUT([PexAssumeNotNull]Collection<int> input)
        {
            Deque<int> actual = new Deque<int>(input);
            int j = 0;
            for (int i = input.Count - 1; i >= 0; i--)
            {                
                PexAssert.AreEqual(input[i], actual.DequeueBack());
                PexAssert.AreEqual(input.Count - (j++ + 1), actual.Count);
            }

            PexObserve.ValueForViewing<Collection<int>>("Input Collection", input);
        }

        /**
        * Generalize PeekFrontDequeEmptyTest
        * Time: 00:01:05
        * Instrumentation issue = 0
        * Patterns: PexExpectedGoals
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        [PexExpectedGoals]
        public void PeekBackEmptyDequePUT([PexAssumeUnderTest]Collection<int> values)
        {
            Deque<int> actual = new Deque<int>(values);
            try
            {
                actual.PeekBack();
            }
            catch (InvalidOperationException ex)
            {
                PexGoal.Reached();
            }

        }

        /**
         * Generalize PeekFrontNonEmptyDequeTest
         * Time: 00:01:54
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void PeekBackDequePUT([PexAssumeUnderTest]Collection<int> values, int val)
        {
            values.Add(val);
            Deque<int> actual = new Deque<int> (values);
            PexAssert.AreEqual(val, actual.PeekBack());
        }

        /**
       * Generalize RemoveTest
       * Time: 00:01:04
       * Instrumentation issue = 0
       * Patterns: AAAA
       * Pex Limitations = none
       * Failing test = none
       **/
        [PexMethod]
        [PexAllowedException(typeof(NotSupportedException))]
        public void RemovePUT(int val)
        {
            Deque<int> actual = new Deque<int>();
            actual.Remove(val);
        }

        /**
         * Generalize ContainsTest
         * Time: 00:02:41
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void ContainsPUT([PexAssumeUnderTest]IList<int> values, int val)
        {
            PexAssume.IsTrue(values.Contains(val));
            Deque<int> actual = new Deque<int> (values);

            Assert.AreEqual(true, actual.Contains(val));
        }

       /**
       * Generalize ClearTest
       * Time: 00:00:48
       * Instrumentation issue = 0
       * Patterns: AAA
       * Pex Limitations = none
       * Failing test = none
       **/
        [PexMethod]
        public void ToArrayPUT([PexAssumeUnderTest]int[] values)
        {
            Deque<int> actual = new Deque<int>(values);
            CollectionAssert.AreEqual(values, actual.ToArray());
        }

        /**
        * Generalize ClearTest
        * Time: 00:00:56
        * Instrumentation issue = 0
        * Patterns: AAA
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void ClearPUT([PexAssumeUnderTest]int[] input)
        {
            Deque<int> actual = new Deque<int> (input);
            actual.Clear();

            PexAssert.AreEqual(0, actual.Count);
        }

        /**
         * Generalize CopyConstructorTest
         * Time: 00:05:23
         * Instrumentation issue = 0
         * Patterns: AAA, AllowedException
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void CopyConstructorPUT(int[] input)
        {
            Deque<int> actual = new Deque<int>(input);
            int[] output = new int[input.Length];
            actual.CopyTo(output, 0);

            CollectionAssert.AreEqual(input, output);
        }
    }
}
