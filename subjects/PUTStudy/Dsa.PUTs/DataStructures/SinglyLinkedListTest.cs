using System;
using System.Collections;
using System.Collections.Generic;
using Dsa.DataStructures;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Dsa.Test.DataStructures
{
    /// <summary>
    /// Tests for SinglyLinkedList.
    /// </summary>
    [TestClass]
    [PexClass]
    public sealed partial class SinglyLinkedListTest
    {
        /// <summary>
        /// Check to see that the SinglyLinkedListCollectionCollection reports as empty when it is.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 3 min 41 sec
        /// Pattern: AAA, Constructor Test, State Relation        
        public void IsEmptyTest(SinglyLinkedList<int> sll)
        {
            if (sll == null)
            {
                sll = new SinglyLinkedList<int>();
                PexAssert.IsTrue(sll.IsEmpty());
            }
        }

        /// <summary>
        /// Check to see that nodes are added correctly to the tail of the SinglyLinkedListCollection.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 5 min 35 sec
        /// Pattern: AAAA, State Relation        
        /// Combines TailTest into this. Combines two tests into a single PUT
        public void AddLastTest([PexAssumeUnderTest]SinglyLinkedList<int> sll, int valueToAdd)
        {
            PexAssume.IsTrue(!sll.IsEmpty());
            sll.Add(valueToAdd);
            PexAssert.AreEqual(valueToAdd, sll.Tail.Value);
        }

        /// <summary>
        /// Check to see that nodes are added correctly to the head of the SinglyLinkedListCollection.
        /// </summary>
        [PexMethod(MaxRunsWithoutNewTests = 200)]
        /// Summary
        /// Time: 10 min 14 sec
        /// Pattern: AAAA, State Relation, RoundTrip
        /// Combines four tests into one test
        public void AddFirstTest([PexAssumeUnderTest]SinglyLinkedList<int> sll, [PexAssumeUnderTest]int[] newElements)
        {
            PexAssume.IsNotNull(newElements);
            PexAssume.IsTrue(newElements.Length > 1);
            PexAssume.IsTrue(sll.Count == 0);

            for (int i = 0; i < newElements.Length; i++)
                sll.AddFirst(newElements[i]);

            PexAssert.AreEqual(newElements[newElements.Length - 1], sll.Head.Value);
            PexAssert.AreEqual(newElements[newElements.Length - 2], sll.Head.Next.Value);
            PexAssert.AreEqual(newElements[0], sll.Tail.Value);
            PexAssert.AreEqual(newElements.Length, sll.Count);
            
            for (int i = 0; i < newElements.Length; i++)
                PexAssert.IsTrue(sll.Contains(newElements[i]));
        }

        /// <summary>
        /// Check to see that the value of the Head node of the SinglyLinkedListCollection is as expected.
        /// </summary>
        //[TestMethod]
        //public void HeadTest()
        //{
        //    SinglyLinkedList<string> sll = new SinglyLinkedList<string> {"Granville"};

        //    Assert.AreEqual("Granville", sll.Head.Value);
        //}

        /// <summary>
        /// Check to see that the value of the Tail node of the SinglyLinkedListCollection is as expected.
        /// </summary>
        //[TestMethod]
        //public void TailTest()
        //{
        //    SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10};

        //    Assert.AreEqual(10, sll.Tail.Value);
        //}

        /// <summary>
        /// Check to see that the Count property of the SinglyLinkedList returns the correct number.
        /// </summary>
        //[TestMethod]
        //public void CountTest()
        //{
        //    SinglyLinkedList<string> sll = new SinglyLinkedList<string> {"Granville", "Barnett"};

        //    Assert.AreEqual(2, sll.Count);
        //}

        /// <summary>
        /// Check to see that SinglyLinkedListCollection returns the correct items from the collection.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 5 min 4 sec
        /// Pattern: Constructor Test, Commutative diagram        
        public void EnumeratorTest([PexAssumeUnderTest]int[] newElements)
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int>(newElements);
            SinglyLinkedList<int> expected = new SinglyLinkedList<int>(newElements);
            CollectionAssert.AreEqual(expected, sll);
        }

        /// <summary>
        /// Check to see that the expected array is returned from a SinglyLinkedListCollection that contains nodes.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 46 sec
        /// Pattern: Constructor Test, State Relation
        /// Combines two tests into a single test
        public void ToArrayTest([PexAssumeUnderTest]int[] expected)
        {
            PexAssume.IsNotNull(expected);
            SinglyLinkedList<int> sll = new SinglyLinkedList<int>(expected);
            CollectionAssert.AreEqual(expected, sll.ToArray());
            PexAssert.AreEqual(expected.Length, sll.ToArray().Length);
        }

        /// <summary>
        /// Check to see that the expected exception is raised when ToArray is called on a SinglyLinkedListCollection
        /// that has no nodes.
        /// </summary>
        //[TestMethod]
        //public void ToArrayEmptyListTest()
        //{
        //    SinglyLinkedList<int> sll = new SinglyLinkedList<int>();

        //    Assert.AreEqual(0, sll.ToArray().Length);
        //}

        /// <summary>
        /// Check to make sure that removing the only node from the SinglyLinkedListCollection results in the expected behaviour.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 10 min 54 sec
        /// Pattern: State Relation
        /// Combines three unit tests into a single PUT
        public void RemoveLastSingleItemInListTest([PexAssumeUnderTest]SinglyLinkedList<int> sll)
        {
            if (sll.Count == 0)
            {
                PexAssert.IsFalse(sll.RemoveLast());
                PexAssert.IsFalse(sll.RemoveFirst());
            }
            else
            {
                int prevCount = sll.Count;
                PexAssert.IsTrue(sll.RemoveLast());
                PexAssert.AreEqual(prevCount - 1, sll.Count);

                if (prevCount == 1)
                {
                    PexAssert.IsTrue(sll.IsEmpty());
                    PexAssert.IsNull(sll.Head);
                    PexAssert.IsNull(sll.Tail);
                }
            }
        }

        /// <summary>
        /// Check to make sure that removing the last node from the SinglyLinkedListCollection results in the expected behaviour.
        /// </summary>
        //[TestMethod]
        //public void RemoveLastTest()
        //{
        //    SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

        //    sll.RemoveLast();

        //    Assert.AreEqual(20, sll.Tail.Value);
        //    Assert.AreEqual(2, sll.Count);
        //    Assert.IsNull(sll.Tail.Next);
        //}

        /// <summary>
        /// Check to see that the correct value is returned when removing from an empty list.
        /// </summary>
        //[TestMethod]
        //public void RemoveLastEmptyListTest()
        //{
        //    SinglyLinkedList<int> actual = new SinglyLinkedList<int>();

        //    Assert.IsFalse(actual.RemoveLast());
        //}

        /// <summary>
        /// Check to see that calling RemoveFirst on SinglyLinkedListCollection with only 1 node results in the expected behaviour.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 8 min 1 sec
        /// Pattern: State Relation, AAAA        
        public void RemoveFirstOneItemInListTest([PexAssumeUnderTest]SinglyLinkedList<int> sll)
        {
            PexAssume.IsTrue(sll.Count > 0);
            int prevCount = sll.Count;

            int nextFirst = 0;
            if (sll.Count > 1)
            {
                nextFirst = sll.Head.Next.Value;
            }

            PexAssert.IsTrue(sll.RemoveFirst());
            PexAssert.AreEqual(prevCount - 1, sll.Count);

            if (prevCount == 1)
            {
                PexAssert.IsNull(sll.Head);
                PexAssert.IsNull(sll.Tail);
            }
            else
            {
                PexAssert.IsTrue(nextFirst == sll.Head.Value);
            }
        }

        /// <summary>
        /// Check to see that when removing the only node in the list that the head and tail pointers are update correctly.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 2 min 11 sec
        /// Pattern: State Relation, AAAA
        public void RemoveOnlyNodeInListTest([PexAssumeUnderTest] SinglyLinkedList<int> actual, int newNode )
        {
            PexAssume.IsTrue(actual.Count == 0);

            actual.AddFirst(newNode);
            actual.Remove(newNode);

            PexAssert.IsNull(actual.Head);
            PexAssert.IsNull(actual.Tail);
        }

        /// <summary>
        /// Check to see that when calling the RemoveFirst method on a SinglyLinkedListCollection with more than one node
        /// results in the expected object state.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 8 min 23 sec
        /// Pattern: State Relation, AAAA
        public void RemoveFirstTest([PexAssumeUnderTest] SinglyLinkedList<int> sll, [PexAssumeUnderTest]int[] newElements)
        {
            PexAssume.IsTrue(sll.Count == 0);
            PexAssume.IsTrue(newElements.Length > 1);

            int currentLength = sll.Count;

            for (int i = 0; i < newElements.Length; i++)
            {
                sll.AddFirst(newElements[i]);
            }
            bool bRetVal = sll.RemoveFirst();
            PexAssert.AreEqual(newElements[newElements.Length - 2], sll.Head.Value);
            PexAssert.AreEqual(newElements[0], sll.Tail.Value);
            PexAssert.AreEqual(newElements.Length + currentLength - 1, sll.Count);
        }

        /// <summary>
        /// Check to see that the correct value is returned when there is nothing to remove.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 0 min 36 sec
        /// Pattern: Constructor Test
        public void RemoveFirstEmptyListTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int>();
            PexAssert.IsFalse(sll.RemoveFirst());
        }

        /// <summary>
        /// Check to see that calling the Clear method resets the SinglyLinkedListCollection object's internal state.
        /// </summary>
        [TestMethod]
        public void ClearTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            sll.Clear();

            Assert.AreEqual(0, sll.Count);
            Assert.IsNull(sll.Head);
            Assert.IsNull(sll.Tail);
        }

        /// <summary>
        /// Check to see that the contains method returns the correct bool depending on whether the item is in the 
        /// SinglyLinkedListCollection or not.
        /// </summary>
        //[TestMethod]
        //public void ContainsTest()
        //{
        //    SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

        //    Assert.IsTrue(sll.Contains(20));
        //    Assert.IsFalse(sll.Contains(40));
        //}

        /// <summary>
        /// Check to see that the correct exception is raised when using a negative index for the arrayIndex parameter
        /// for CopyTo.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void CopyToInvalidIndexTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};
            int[] myArray = new int[sll.Count];

            sll.CopyTo(myArray, -1);
        }

        /// <summary>
        /// Check to see that passing in an index that is equal to or greater than the array size throws
        /// the correct exception.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void CopyToIndexGteThanArrayTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};
            int[] myArray = new int[sll.Count];

            sll.CopyTo(myArray, 2);
        }

        /// <summary>
        /// Check to see that CopyTo copies all items of SinglyLinkedListCollection to an array beginning at a specified index.
        /// </summary>
        [TestMethod]
        public void ArrayCopyWithDefinedStartIndexTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};
            int[] actual = new int[10];

            sll.CopyTo(actual, 6);

            Assert.AreEqual(10, actual[6]);
            Assert.AreEqual(20, actual[7]);
            Assert.AreEqual(30, actual[8]);
        }

        /// <summary>
        /// Check to see that the correct exception is raised when attempting to remove an item from an empty
        /// SinglyLinkedListCollection.
        /// </summary>
        [TestMethod]
        public void RemoveItemFromEmptySinglyLinkedListCollection()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int>();
            
            Assert.IsFalse(sll.Remove(10));
        }

        /// <summary>
        /// Check to see that Remove leaves the SinglyLinkedListCollection in the correct state where the value of Remove
        /// is equal to that of the head node.
        /// </summary>
        [TestMethod]
        public void RemoveHeadItemTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            bool actual = sll.Remove(10);

            Assert.AreEqual(20, sll.Head.Value);
            Assert.AreEqual(30, sll.Tail.Value);
            Assert.AreEqual(30, sll.Head.Next.Value);
            Assert.AreEqual(2, sll.Count);
            Assert.IsTrue(actual);
        }

        /// <summary>
        /// Check to see that Remove leaves the SinglyLinkedListCollection in the correct state, when remove is any node but head or tail.
        /// </summary>
        [TestMethod]
        public void RemoveMiddleItemTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            bool actual = sll.Remove(20);

            Assert.AreEqual(30, sll.Head.Next.Value);
            Assert.AreEqual(10, sll.Head.Value);
            Assert.AreEqual(30, sll.Tail.Value);
            Assert.AreEqual(2, sll.Count);
            Assert.IsTrue(actual);
        }

        /// <summary>
        /// Check to see that Remove leaves the SinglyLinkedListCollection in the correct state where the value of Remove
        /// is equal to that of the tail node.
        /// </summary>
        [TestMethod]
        public void RemoveTailItemTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            bool actual = sll.Remove(30);

            Assert.AreEqual(10, sll.Head.Value);
            Assert.AreEqual(20, sll.Head.Next.Value);
            Assert.AreEqual(20, sll.Tail.Value);
            Assert.AreEqual(2, sll.Count);
            Assert.IsNull(sll.Tail.Next);
            Assert.IsTrue(actual);
        }

        /// <summary>
        /// Check to see that when calling the Remove method passing in a value that is not contained in the SinglyLinkedListCollection
        /// returns false.
        /// </summary>
        [TestMethod]
        public void RemoveWithNoMatchTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {20, 30, 50};

            Assert.AreEqual(3, sll.Count);
            Assert.IsFalse(sll.Remove(110));
        }

        /// <summary>
        /// Check to see that the head and tail nodes are correct after adding a node after the only node in the SinglyLinkedListCollection.
        /// </summary>
        [TestMethod]
        public void AddAfterOnlyOneNodeInListTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10};

            sll.AddAfter(sll.Head, 20);

            Assert.AreEqual(20, sll.Tail.Value);
            Assert.AreEqual(10, sll.Head.Value);
            Assert.AreEqual(20, sll.Head.Next.Value);
            Assert.AreEqual(2, sll.Count);
        }

        /// <summary>
        /// Check to see that the tail node is updated after adding a node after the tail in the SinglyLinkedListCollection.
        /// </summary>
        [TestMethod]
        public void AddAfterTailTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20};

            sll.AddAfter(sll.Tail, 30);

            Assert.AreEqual(30, sll.Tail.Value);
            Assert.AreEqual(30, sll.Head.Next.Next.Value);
            Assert.AreEqual(3, sll.Count);
        }

        /// <summary>
        /// Check to see that adding a node somewhere in the middle of the SinglyLinkedListCollection leaves the SinglyLinkedListCollection
        /// in the correct state.
        /// </summary>
        [TestMethod]
        public void AddAfterMiddleNodeTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            sll.AddAfter(sll.Head.Next, 25);

            Assert.AreEqual(25, sll.Head.Next.Next.Value);
            Assert.AreEqual(30, sll.Head.Next.Next.Next.Value);
            Assert.AreEqual(4, sll.Count);
        }

        /// <summary>
        /// Check to see that the correct exception is raised when trying to add after a null node (in this case the list is empty so head is null).
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void AddAfterEmptyNullNodeTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int>();

            sll.AddAfter(sll.Head, 10);
        }

        /// <summary>
        /// Check to see that AddBefore when passing in the head node of the SinglyLinkedListCollection results in the expected state.
        /// </summary>
        [TestMethod]
        public void AddBeforeHeadTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            sll.AddBefore(sll.Head, 5);

            Assert.AreEqual(5, sll.Head.Value);
            Assert.AreEqual(10, sll.Head.Next.Value);
            Assert.AreEqual(4, sll.Count);
        }

        /// <summary>
        /// Check to make sure that adding before tail results in the expected object state.
        /// </summary>
        [TestMethod]
        public void AddBeforeTailTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30, 40};

            sll.AddBefore(sll.Head.Next.Next.Next, 35);

            Assert.AreEqual(35, sll.Head.Next.Next.Next.Value);
        }

        /// <summary>
        /// Check to see that AddBefore a middle node results in the expected state of the SinglyLinkedListCollection.
        /// </summary>
        [TestMethod]
        public void AddBeforeMiddleNodeTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            sll.AddBefore(sll.Head.Next, 15);

            Assert.AreEqual(15, sll.Head.Next.Value);
            Assert.AreEqual(20, sll.Head.Next.Next.Value);
            Assert.AreEqual(4, sll.Count);
        }

        /// <summary>
        /// Check to see that the correct exception is raised when calling AddBefore on a SinglyLinkedListCollection with no nodes.
        /// </summary>
        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void AddBeforeEmptyListTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int>();

            sll.AddBefore(sll.Head, 10);
        }

        /// <summary>
        /// Check to make sure that IEnumerable.GetEnumerator returns an IEnumerator that is not null.
        /// </summary>
        [TestMethod]
        public void GetEnumeratorTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int>();
            IEnumerable enumerSll = sll;

            sll.Add(10);

            Assert.IsNotNull(enumerSll.GetEnumerator());
        }

        /// <summary>
        /// Check to make sure that GetReverseEnumerator returns a non-null IEnumerator(Of T) object.
        /// </summary>
        [TestMethod]
        public void GetReverseEnumeratorTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            Assert.IsNotNull(sll.GetReverseEnumerator());
        }

        /// <summary>
        /// Check to see that the correct array is returned from a call to ToReverseArray.
        /// </summary>
        [TestMethod]
        public void ToReverseArrayTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int> {10, 20, 30};

            int[] expected = { 30, 20, 10 };

            CollectionAssert.AreEqual(expected, sll.ToReverseArray());
        }

        /// <summary>
        /// Check to see that calling ToReverseArray on a SinglyLinkedListCollection with no items raises the correct
        /// exception.
        /// </summary>
        [TestMethod]
        public void ToReverseArrayNoItemsTest()
        {
            SinglyLinkedList<int> sll = new SinglyLinkedList<int>();

            Assert.AreEqual(0, sll.ToReverseArray().Length);
        }

        /// <summary>
        /// Check to see that the linked list is in the correct state when passing in an IEnumerable.
        /// </summary>
        [TestMethod]
        public void CopyConstructorTest()
        {
            List<string> collection = new List<string> { "Granville", "Barnett", "Luca", "Del", "Tongo" };
            SinglyLinkedList<string> actual = new SinglyLinkedList<string>(collection);

            Assert.AreEqual(5, actual.Count);
        }
    }
}