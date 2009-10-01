using System;
using System.Collections.Generic;
using Dsa.DataStructures;
using Microsoft.Pex.Framework;
using NUnit.Framework;
using Microsoft.Pex.Framework.Validation;
using Microsoft.Pex.Framework.Goals;

namespace Dsa.PUTs.DataStructures
{
    /// <summary>
    /// Tests for DoublyLinkedList.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class DoublyLinkedListTest
    {
    /**
     * Generalizes ItemsAreEqualTest
     * Time: 00:00:41
     * Instrumentation issue = 0
     * Patterns: AAAA
     * Pex Limitations = none
     * Failing test = none
     **/
        [PexMethod]
        public void ItemsAreEqualPUT([PexAssumeUnderTest]IList<int> values)
        {
            DoublyLinkedList<int> actual = new DoublyLinkedList<int> (values);
            DoublyLinkedList<int> expected = new DoublyLinkedList<int> (values);
            CollectionAssert.AreEqual(expected, actual);
        }

     /**
      * Generalizes AddFirstTest
      * Time: 00:01:25
      * Instrumentation issue = 0
      * Patterns: AAAA
      * Pex Limitations = none
      * Failing test = none
      **/
        [PexMethod]
        public void AddFirstPUT([PexAssumeUnderTest]IList<int> values, int toAddValue)
        {
            DoublyLinkedList<int> dll = new DoublyLinkedList<int>(values);
            dll.AddFirst(toAddValue);
            PexAssert.AreEqual(toAddValue, dll.Head.Value);
        }


        /**
       * Generalizes AddAfterTailTest
       * Time: 00:02:06
       * Instrumentation issue = 0
       * Patterns: AAAA
       * Pex Limitations = none
       * Failing test = none
       **/
        [PexMethod]
        public void AddAfterTailPUT([PexAssumeUnderTest]IList<int> values, int toAddValue)
        {
            PexAssume.IsTrue(values.Count > 1);
            PexAssume.IsFalse(values.Contains(toAddValue));
            DoublyLinkedList<int> dll = new DoublyLinkedList<int> (values);
            dll.AddAfter(dll.Tail, toAddValue);
            PexAssert.AreEqual(toAddValue, dll.Tail.Value);
            PexAssert.AreEqual(values[values.Count - 1], dll.Tail.Previous.Value);
        }

       /**
       * Generalizes AddAfterTest, AddAfterNullNodeTest, AddAfterNoNodesTest
       * Time: 00:08:23
       * Instrumentation issue = 0
       * Patterns: AAAA, Allowed Exception
       * Pex Limitations = none
       * Failing test = none
       **/
        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void AddAfterPUT([PexAssumeUnderTest]IList<int> values, int toAddValue)
        {
            PexAssume.IsFalse(values.Contains(toAddValue));
            DoublyLinkedList<int> dll = new DoublyLinkedList<int>(values);
            if (values.Count == 0)
            {
                dll.AddAfter(dll.Head, toAddValue);
                PexAssert.IsFalse(true, "InvalidOperationException was expected");
            }
            dll.AddAfter(dll.Head.Next, toAddValue);
            if (values.Count == 1)
            {
                PexAssert.IsFalse(true, "ArgumentNullException was expected");
            }
                PexAssert.AreEqual(toAddValue, dll.Head.Next.Next.Value);
                PexAssert.AreEqual(values[1], dll.Head.Next.Next.Previous.Value);
                if (values.Count > 2)
                {
                    PexAssert.AreEqual(values[2], dll.Head.Next.Next.Next.Value);
                    if (values.Count == 3)
                        PexAssert.AreEqual(toAddValue, dll.Tail.Previous.Value);
                }
        }

        /**
        * Generalizes AddBeforeNullNode, AddBeforeEmptyListTest, AddBeforeTest, AddBeforeHeadTest\
        * Time: 00:10:31
        * Instrumentation issue = 0
        * Patterns: AAAA, Allowed Exception
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void AddBeforePUT([PexAssumeUnderTest]IList<int> values, int toAddValue)
        {
            PexAssume.IsFalse(values.Contains(toAddValue));
            DoublyLinkedList<int> dll = new DoublyLinkedList<int>(values);
            if (values.Count == 0)
            {
                dll.AddBefore(dll.Head, toAddValue);
                PexAssert.IsFalse(true, "InvalidOperationException was expected");
            }
            dll.AddBefore(dll.Head.Next, toAddValue);
            if (values.Count == 1)
            {
                PexAssert.IsFalse(true, "ArgumentNullException was expected");
            }
            PexAssert.AreEqual(toAddValue, dll.Head.Next.Value);
            PexAssert.AreEqual(values[0], dll.Head.Next.Previous.Value);
            PexAssert.AreEqual(values[1], dll.Head.Next.Next.Value);
            if(values.Count == 2)
                PexAssert.AreEqual(toAddValue, dll.Tail.Previous.Value);
        }

        /**No generalization**/
        [PexMethod]
        public void IsEmptyPUT()
        {
            DoublyLinkedList<int> dll = new DoublyLinkedList<int>();

            PexAssert.IsTrue(dll.IsEmpty());
        }

        /**
        * Generalizes RemoveLastEmptyListTest,RemoveLastTestMultipleNodesTest,RemoveLastTwoNodesTest,RemoveLastTest
        * Time: 00:03:02
        * Instrumentation issue = 0
        * Patterns: AAAA
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void RemoveLastPUT([PexAssumeUnderTest]IList<int> values)
        {
            DoublyLinkedList<int> dll = new DoublyLinkedList<int>(values);
            bool returnVal = dll.RemoveLast();
            if (values.Count > 0)
            {
                PexAssert.IsTrue(returnVal);
                if (values.Count > 1)
                {
                    PexAssert.AreEqual(values[values.Count-2], dll.Tail.Value);
                    PexAssert.IsNull(dll.Tail.Next);
                }
                else
                {
                    PexAssert.IsNull(dll.Head);
                    PexAssert.IsNull(dll.Tail);
                }
            }
            else
            {
                PexAssert.IsFalse(returnVal);
            }
        }

        /**
        * Generalizes RemoveFirstEmptyListTest, RemoveFirstMoreThanTwoNodesTest, RemoveFirstTwoNodesTest, RemoveFirstSingleNodeTest
        * Time: 00:07:02
        * Instrumentation issue = 0
        * Patterns: AAAA
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void RemoveFirstPUT([PexAssumeUnderTest]IList<int> values)
        {
            DoublyLinkedList<int> dll = new DoublyLinkedList<int> (values);
            bool returnVal = dll.RemoveFirst();
            if (values.Count > 0)
            {
                PexAssert.IsTrue(returnVal);
                if (values.Count > 1)
                {
                    PexAssert.AreEqual(values[1], dll.Head.Value);
                    PexAssert.IsNull(dll.Head.Previous);
                }
                else
                {
                    PexAssert.IsNull(dll.Head);
                    PexAssert.IsNull(dll.Tail);
                }
            }
            else
            {
                PexAssert.IsFalse(returnVal);
            }
        }

        /**
         * Generalizes ClearTest
         * Time: 00:02:21
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void AddPUT([PexAssumeUnderTest]IList<int> values, int head, int tail)
        {
            DoublyLinkedList<int> dll = new DoublyLinkedList<int>(values);
            ICollection<int> actual = dll;
            actual.Add(head);
            actual.Add(tail);
            if (values.Count == 0)
            {
                PexAssert.AreEqual(head, dll.Head.Value);
                PexAssert.AreEqual(tail, dll.Tail.Value);
            }
            PexAssert.AreEqual(values.Count + 2, dll.Count);
            PexAssert.IsNull(dll.Head.Previous);
            PexAssert.IsNull(dll.Tail.Next);
        }

        /**
         * Generalizes ClearTest
         * Time: 00:00:43
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void ClearPUT([PexAssumeUnderTest]IList<int> values)
        {
            DoublyLinkedList<int> dll = new DoublyLinkedList<int> (values);
            dll.Clear();
            PexAssert.IsNull(dll.Head);
            PexAssert.IsNull(dll.Tail);
            PexAssert.AreEqual(0, dll.Count);
        }

        /**
        * Generalizes CountTest
        * Time: 00:02:55
        * Instrumentation issue = 0
        * Patterns: AAAA
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void CountPUT([PexAssumeUnderTest]IList<int> values, int randomPick1)
        {
            PexAssume.IsFalse(values.Contains(randomPick1));
            PexAssume.IsTrue(values.Count > 1);
            DoublyLinkedList<int> dll = new DoublyLinkedList<int> (values);

            dll.RemoveFirst();
            dll.RemoveLast();
            PexAssert.AreEqual(values.Count-2, dll.Count);

            dll.Add(randomPick1);
            dll.AddAfter(dll.Head, values[0]);
            dll.AddBefore(dll.Head.Next, values[values.Count-1]);
            PexAssert.AreEqual(values.Count+1, dll.Count);
        }

        /**
         * Generalizes ContainsTest
         * Time: 00:01:44
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void ContainsPUT([PexAssumeUnderTest]IList<int> values, int randomPick1, int randomPick2)
        {
            PexAssume.IsTrue(values.Contains(randomPick1));
            PexAssume.IsFalse(values.Contains(randomPick2));
            DoublyLinkedList<int> dll = new DoublyLinkedList<int> (values);
            PexAssert.IsTrue(dll.Contains(randomPick1));
            PexAssert.IsFalse(dll.Contains(randomPick2));
        }


        /**
         * Generalizes ToArrayEmptyListTest,ToArrayTest
         * Time: 00:01:23
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void ToArrayPUT([PexAssumeUnderTest]string[] values)
        {
            DoublyLinkedList<string> dll = new DoublyLinkedList<string>(values);
            CollectionAssert.AreEqual(values, dll.ToArray());
            PexAssert.AreEqual(values.Length, dll.ToArray().Length);
        }

        /**
        * Generalizes RemoveTailMultipleNodesTest, RemoveHeadMultipleNodesTest, RemoveMiddleMultipleNodesTest, 
        *             RemoveTailTwoNodes, RemoveHeadTwoNodes, RemoveSingleNodeTest
        * Time: 00:10:18
        * Instrumentation issue = 0
        * Patterns: AAAA
        * Pex Limitations = none
        * Failing test = none
        **/

        [PexMethod]
       // [PexExpectedGoals("g1;g2;g3;g4")]
        public void RemoveTailPUT([PexAssumeUnderTest]IList<int> values, int randomPick1)
        {
            PexAssume.AreDistinctValues<int>(values as int[]);
            PexAssume.IsTrue(values.Contains(randomPick1));
            DoublyLinkedList<int> dll = new DoublyLinkedList<int> (values);
            bool tail = dll.Tail.Value == randomPick1 ? true:false;
            bool head = dll.Head.Value == randomPick1 ? true : false;
            PexObserve.ValueForViewing<bool>("isTail", tail);
            PexObserve.ValueForViewing<bool>("isHead", head);
            dll.Remove(randomPick1);
            PexAssert.AreEqual(values.Count - 1, dll.Count);
            if (values.Count == 1)
            {
                PexAssert.IsTrue(dll.IsEmpty());
                //PexGoal.Reached("g1");
                return;
            }
            if (values.Count == 2)
            {
                PexAssert.AreEqual(dll.Head, dll.Tail);
                //PexGoal.Reached("g2");
            }
            if (tail)
            {
                PexAssert.AreEqual(values[values.Count - 2], dll.Tail.Value);
                //PexGoal.Reached("g3");
            }
            if (head)
            {
                PexAssert.AreEqual(values[1], dll.Head.Value);
               // PexGoal.Reached("g4");
            }
            PexAssert.IsFalse(dll.Contains(randomPick1));
        }

        /**
        * Generalize RemoveNoMatchTest, RemoveEmptyListTest
        * Time: 00:02:30
        * Instrumentation issue = 0
        * Patterns: AAAA
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void RemovePUT([PexAssumeUnderTest]IList<int> values, int randomPick)
        {
            DoublyLinkedList<int> dll = new DoublyLinkedList<int> (values);
            if (!values.Contains(randomPick))
                PexAssert.IsFalse(dll.Remove(randomPick));
            else
                PexAssert.IsTrue(dll.Remove(randomPick));
        }

        /**
         * Generalize GetEnumeratorTest
         * Time: 00:03:06
         * Instrumentation issue = 0
         * Patterns: AAA, Allowed Exception
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexAllowedException(typeof(ArgumentNullException))]
        [PexMethod]
        public void GetEnumeratorPUT(int[] values)
        {
            DoublyLinkedList<int> dll = new DoublyLinkedList<int>(values);
            PexAssert.IsNotNull(dll.GetEnumerator());
            PexObserve.ValueForViewing<DoublyLinkedList<int>>("Enumeration", dll);
        }

        /**
         * Generalize CopyConstructorTest, AddLastTest
         * Time: 00:01:40
         * Instrumentation issue = 0
         * Patterns: AAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void CopyConstructorPUT([PexAssumeUnderTest]string[] values)
        {
            DoublyLinkedList<string> actual = new DoublyLinkedList<string>(new List<string>(values));
            PexAssert.AreEqual(values.Length, actual.Count);
            CollectionAssert.AreEqual(values, actual);
        }
    }
}