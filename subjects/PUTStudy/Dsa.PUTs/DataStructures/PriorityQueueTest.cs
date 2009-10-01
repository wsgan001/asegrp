using System;
using System.Collections.Generic;
using Dsa.DataStructures;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Validation;
using Microsoft.Pex.Framework.Goals;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    /// <summary>
    /// Tests for PriorityQueue.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class PriorityQueueTest
    {
        /// <summary>
        /// Check to see that the queue is in the correct state.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        /// Summary
        /// Time: 2 min 59 sec
        /// Pattern: Constructor Test
        public void AddTest(int[] newElements)
        {
            PriorityQueue<int> actual = new PriorityQueue<int>(newElements);
            PexAssert.AreEqual(newElements.Length, actual.Count);
        }

        /// <summary>
        /// Check to see that the correct value is returned.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        [PexAllowedException(typeof(InvalidOperationException))]
        [PexExpectedGoals("exception;normal")]
        /// Summary
        /// Time: 6 min 23 sec
        /// Pattern: Commutative diagram, Allowed Exception, Reachability
        /// Generalizes two unit tests PeekTest and PeekNoItemsInTheQueueTest into one PUT
        public void PeekTest(int[] newElements)
        {
            try
            {
                PriorityQueue<int> actual = new PriorityQueue<int>(newElements);

                //get the minimum element
                int minVal = Int32.MaxValue;
                for (int i = 0; i < newElements.Length; i++)
                    minVal = minVal > newElements[i] ? newElements[i] : minVal;

                PexAssert.AreEqual(minVal, actual.Peek());
                PexGoal.Reached("normal");
            }
            catch (InvalidOperationException)
            {
                PexGoal.Reached("exception");
                throw;
            }
        }

        /// <summary>
        /// Check to see that the correct exception is raised when peeking from an empty queue.
        /// </summary>
        //[TestMethod]
        ////[ExpectedException(typeof(InvalidOperationException), ExpectedMessage="There are no items in the queue.")]
        //public void PeekNoItemsInTheQueueTest()
        //{
        //    PriorityQueue<int> actual = new PriorityQueue<int>();

        //    actual.Peek();
        //}

        /// <summary>
        /// Removes the item at the front of the queue.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        /// Summary
        /// Time: 9 min 45 sec
        /// Pattern: Commutative diagram, Allowed Exception
        /// Two unit tests DequeueTest and DequeueEmptyQueueTest are split into multiple PUTs: DequeueTest and DequeueTestString
        public void DequeueTest([PexAssumeUnderTest]List<int> elementList)
        {            
            PriorityQueue<int> actual = new PriorityQueue<int> (elementList);

            int dequeuedElem = actual.Dequeue();
            elementList.Sort();
            PexAssert.AreEqual(elementList[0], dequeuedElem);
            if(elementList.Count > 1)
                PexAssert.AreEqual(elementList[1], actual.Peek());
            PexAssert.AreEqual(elementList.Count - 1, actual.Count);
        }

        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        public void DequeueTestString([PexAssumeUnderTest]List<string> elementList)
        {
            PriorityQueue<string> actual = new PriorityQueue<string>(elementList);

            string dequeuedElem = actual.Dequeue();
            elementList.Sort();
            PexAssert.AreEqual(elementList[0], dequeuedElem);
            if (elementList.Count > 1)
                PexAssert.AreEqual(elementList[1], actual.Peek());
            PexAssert.AreEqual(elementList.Count - 1, actual.Count);
        }

        /// <summary>
        /// Check to see that the correct exception is thrown when calling dequeue on an empty queue.
        /// </summary>
        //[TestMethod]
        //[ExpectedException(typeof(InvalidOperationException), ExpectedMessage="There are no items in the queue.")]
        //public void DequeueEmptyQueueTest()
        //{
        //    PriorityQueue<int> actual = new PriorityQueue<int>();

        //    actual.Dequeue();
        //}

        /// <summary>
        /// Check to see that the correct value is returned when checking for an item that is within the queue.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        /// Summary
        /// Time: 3 min 19 sec
        /// Pattern: Constructor Test and Allowed Exception        
        public void ContainsTest(int[] elemList)
        {
            PriorityQueue<int> actual = new PriorityQueue<int>(elemList);            
            for(int i = 0; i < elemList.Length; i++)
                PexAssert.IsTrue(actual.Contains(elemList[i]));
        }

        /// <summary>
        /// Check to see that the correct array is returned.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 7 min 21 sec
        /// Pattern: Manual Output Review
        /// Difficulties with PUTs: Missing oracle as we need to have an alternative implementation of PriorityQueue in this scenario
        public void ToArrayTest([PexAssumeUnderTest]List<int> elemList)
        {
            PriorityQueue<int> queue = new PriorityQueue<int>(elemList);
            elemList.Sort();
            int[] actual = queue.ToArray();
            PexObserve.ValueForViewing<int[]>("Actual", queue.ToArray()); 
            CollectionAssert.AreEquivalent(elemList, queue);
        }

        /// <summary>
        /// Check to see that the correct exception is raised as this is not supported on the queue, you must go through
        /// dequeueing etc.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(NotSupportedException))]
        /// Summary
        /// Time: 2 min 3 sec
        /// Pattern: ParameterizedStub, Allowed Exception
        /// Added a factory method.
        public void RemoveTest([PexAssumeUnderTest]PriorityQueue<int> actual, int newElem)
        {
            actual.Remove(newElem);
        }

        /// <summary>
        /// Check to see that the queue is in the correct state after clearing it.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 1 sec
        /// Pattern: State Relation
        public void ClearTest([PexAssumeUnderTest]PriorityQueue<int> actual)
        {          
            actual.Clear();
            PexAssert.AreEqual(0, actual.Count);
        }

        /// <summary>
        /// Check to see that the queue is correct when using a strategy that makes the higher valued
        /// values take priority.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 4 min 36 sec
        /// Pattern: Commutative diagram
        public void MaxValuesHavePriorityTest([PexAssumeUnderTest]List<int> elemList)
        {
            PexAssume.IsTrue(elemList.Count > 0);
            PriorityQueue<int> actual = new PriorityQueue<int>(elemList, Strategy.Max);
            elemList.Sort();
            Assert.AreEqual(elemList[elemList.Count - 1], actual.Peek());
        }

        /// <summary>
        /// Check to see that the correct values are returned on enumeration.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 6 min 35 sec
        /// Pattern: Manual output review
        /// Difficulties: Issue with PUTs where we cannot write test oracles
        public void GetEnumeratorTest([PexAssumeUnderTest]List<int> elemList)
        {
            PriorityQueue<int> actual = new PriorityQueue<int>(elemList, Strategy.Max);
            PexObserve.ValueForViewing<int[]>("Actual", actual.ToArray()); 
            //CollectionAssert.AreEqual(elemList.ToArray(), actual.ToArray());
        }

        /// <summary>
        /// Check to see that copying an existing collection to a priority queue results in the correct state.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        /// Summary
        /// Time: 1 min 50 sec
        /// Pattern: Constructor Test, Allowed Exception
        public void CopyConstructorTest(List<string> newelements)
        {            
            PriorityQueue<string> actual = new PriorityQueue<string>(newelements);
            PexAssert.AreEqual(newelements.Count, actual.Count);
        }

        /// <summary>
        /// Check to see that the collection is left in the correct state when using a custom strategy and the items from an existing
        /// collection to populate the queue.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        /// Summary
        /// Time: 1 min 33 sec
        /// Pattern: Constructor Test, Allowed Exception
        public void CopyConstructorCustomStrategyTest(List<string> newelements)
        {
            PriorityQueue<string> actual = new PriorityQueue<string>(newelements, Strategy.Max);
            PexAssert.AreEqual(newelements.Count, actual.Count);
        }
    }
}
