using System.Collections.Generic;
using Dsa.DataStructures;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Validation;
using System;
using System.Collections;

namespace Dsa.PUTs.DataStructures
{
    /// <summary>
    /// Tests for Avl tree.
    /// </summary>
    [TestClass]
    [PexClass]
    public partial class AvlTreeTest
    {
        /**
       * Generalizes BalanceFactorTest
       * Time: 00:07:27
       * Instrumentation issue = 0
       * Patterns: AAAA
       * Pex Limitations = none
       * Failing test = none
       * Comment: Over constraining the inputs to achieve the test scenario
       **/
        [PexMethod]
        public void BalanceFactorPUT(int val1, int val2)
        {
            PexAssume.AreNotEqual(val1, val2);
            int minVal = Math.Min(val1, val2);
            int other = Math.Max(val1, val2);
            AvlTree<int> actual = new AvlTree<int> {minVal,other};
            
            AvlTreeNode<int> root = actual.FindNode(minVal);
            AvlTreeNode<int> leaf = actual.FindNode(other);
            AvlTreeNode<int> emptyNode = default(AvlTreeNode<int>);

            PexAssert.AreEqual(-1, actual.GetBalanceFactor(root));
            PexAssert.AreEqual(0, actual.GetBalanceFactor(leaf));
            PexAssert.AreEqual(0, actual.GetBalanceFactor(emptyNode));
        }

        /**
        * Generalizes SingleLeftRotationTest
        * Time: 00:09:15
        * Instrumentation issue = 0
        * Patterns: AAAA
        * Pex Limitations = none
        * Failing test = none
        * Comment: Over constraining the inputs to achieve the test scenario
        **/
        [PexMethod]
        public void SingleLeftRotationPUT(int root, int left, int right)
        {
            PexAssume.IsTrue(left < root && right > root);
            List<int> list = new List<int> {left, root, right };
            PexObserve.ValueForViewing<int[]>("Input list", list.ToArray());
            AvlTree<int> avlTree = new AvlTree<int>(list);
            PexAssert.AreEqual(root, avlTree.Root.Value);
            PexAssert.AreEqual(left, avlTree.Root.Left.Value);
            PexAssert.AreEqual(right, avlTree.Root.Right.Value);

        }

        /**
       * Generalizes SingleRightRotationTest
       * Time: 00:02:54
       * Instrumentation issue = 0
       * Patterns: AAAA
       * Pex Limitations = none
       * Failing test = none
       * Comment: Over constraining the inputs to achieve the test scenario
       **/
        [PexMethod]
        public void SingleRightRotationPUT(int root, int left, int right)
        {
            PexAssume.IsTrue(left < root && right > root);
            List<int> list = new List<int> { left, root, right };
            list.Sort();
            list.Reverse();
            PexObserve.ValueForViewing<int[]>("Input list",list.ToArray());
            AvlTree<int> avlTree = new AvlTree<int>(list);
            PexAssert.AreEqual(root, avlTree.Root.Value);
            PexAssert.AreEqual(left, avlTree.Root.Left.Value);
            PexAssert.AreEqual(right, avlTree.Root.Right.Value);
        }

     /**
      * Generalizes DoubleRightLeftRotationTest
      * Time: 00:01:14
      * Instrumentation issue = 0
      * Patterns: AAAA
      * Pex Limitations = none
      * Failing test = none
      * Comment: Over constraining the inputs to achieve the test scenario
      **/
        [PexMethod]
        public void DoubleRightLeftRotationPUT(int root, int left, int right)
        {
            PexAssume.IsTrue(left < root && right > root);
            List<int> list = new List<int> { left, right, root };
            PexObserve.ValueForViewing<int[]>("Input list", list.ToArray());
            AvlTree<int> avlTree = new AvlTree<int>(list);
            PexAssert.AreEqual(root, avlTree.Root.Value);
            PexAssert.AreEqual(left, avlTree.Root.Left.Value);
            PexAssert.AreEqual(right, avlTree.Root.Right.Value);
        }

        /**
         * Generalizes DoubleLeftRightRotationTest
         * Time: 00:01:16
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         * Comment: Over constraining the inputs to achieve the test scenario
         **/
        [PexMethod]
        public void DoubleLeftRightRotationPUT(int root, int left, int right)
        {
            PexAssume.IsTrue(left < root && right > root);
            List<int> list = new List<int> { right,left,root };
            PexObserve.ValueForViewing<int[]>("Input list", list.ToArray());
            AvlTree<int> avlTree = new AvlTree<int>(list);
            PexAssert.AreEqual(root, avlTree.Root.Value);
            PexAssert.AreEqual(left, avlTree.Root.Left.Value);
            PexAssert.AreEqual(right, avlTree.Root.Right.Value);
        }

        /**
         * Generalizes InsertionRotateRightLeftTest
         * Time: 00:09:53
         * Instrumentation issue = 0
         * Patterns: Manual output
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void InsertionRotateRightLeftPUT([PexAssumeUnderTest]List<int> list1, [PexAssumeUnderTest]List<int> list2, [PexAssumeUnderTest]List<int> list3)
        {
            //PexAssume.IsTrue(list1.Count > 0 && list2.Count > 0 && list1.Count > 0);
           // PexAssume.AreDistinctValues<int>(list1.ToArray());
           // PexAssume.AreDistinctValues<int>(list2.ToArray());
           // PexAssume.AreDistinctValues<int>(list3.ToArray());
            list2.Sort();
            list3.Sort();
            list3.Reverse();
            list1.AddRange(list3);
            list1.AddRange(list2);
            AvlTree<int> avlTree = new AvlTree<int>(list1);
            PexObserve.ValueForViewing<int[]>("Input list", list1.ToArray());
            PexObserve.ValueForViewing<int[]>("AVL Tree", avlTree.ToArray());
        }

        /**
        * Generalizes InsertionMassiveTest
        * Time: 00:03:57
        * Instrumentation issue = 0
        * Patterns: AAAA, Manual Output
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void InsertionMassivePUT([PexAssumeUnderTest]List<int> values)
        {
            PexAssume.IsTrue(values.Count > 0);
            AvlTree<int> avlTree = new AvlTree<int>();
            foreach (int i in values)
            {
                avlTree.Add(i);
            }
            PexAssert.AreEqual(values.Count, avlTree.Count);
            IEnumerable enumerator = avlTree.GetInorderEnumerator();
            foreach (int i in enumerator)
                PexObserve.ValueForViewing<int>("tree nodes", i);
        }



        /** Cannot generalize RemoveNodeSingleLeftSubTreeTest, RemoveLeftRotationTest, RemoveCascadeBalancingTest
        * involves complex assertion and complex test input requirement**/

        [PexMethod]
        public void RemoveCascadeBalancingTest()
        {
            AvlTree<int> avlTree = new AvlTree<int>() { 32, 16, 48, 8, 24, 40, 56, 28, 36, 44, 52, 60, 58, 62 };
            avlTree.Remove(8);
            Assert.AreEqual(48, avlTree.Root.Value);
            Assert.AreEqual(32, avlTree.Root.Left.Value);
            Assert.AreEqual(56, avlTree.Root.Right.Value);
            Assert.AreEqual(24, avlTree.Root.Left.Left.Value);
            Assert.AreEqual(16, avlTree.Root.Left.Left.Left.Value);
            Assert.AreEqual(40, avlTree.Root.Left.Right.Value);
            Assert.AreEqual(60, avlTree.Root.Right.Right.Value);
            Assert.AreEqual(52, avlTree.Root.Right.Left.Value);
            Assert.AreEqual(62, avlTree.Root.Right.Right.Right.Value);
        }

        [PexMethod]
        public void RemoveLeftRotationTest()
        {
            AvlTree<int> avlTree = new AvlTree<int>() { 32, 16, 48, 8, 24, 40, 56, 4, 36, 44, 52, 60, 58, 62 };
            avlTree.Remove(4);
            Assert.AreEqual(48, avlTree.Root.Value);
            Assert.AreEqual(32, avlTree.Root.Left.Value);
            Assert.AreEqual(56, avlTree.Root.Right.Value);
            Assert.AreEqual(16, avlTree.Root.Left.Left.Value);
            Assert.AreEqual(8, avlTree.Root.Left.Left.Left.Value);
            Assert.AreEqual(40, avlTree.Root.Left.Right.Value);
            Assert.AreEqual(60, avlTree.Root.Right.Right.Value);
            Assert.AreEqual(52, avlTree.Root.Right.Left.Value);
            Assert.AreEqual(62, avlTree.Root.Right.Right.Right.Value);
            Assert.AreEqual(58, avlTree.Root.Right.Right.Left.Value);
            Assert.AreEqual(13, avlTree.Count);
        }

        [PexMethod]
        public void RemoveNodeSingleLeftSubTreeTest()
        {
            AvlTree<int> avlTree = new AvlTree<int>() { 7, 6, 15, 5, 14, 16 };
            avlTree.Remove(6);
            Assert.AreEqual(7, avlTree.Root.Value);
            Assert.AreEqual(5, avlTree.Root.Left.Value);
            Assert.AreEqual(15, avlTree.Root.Right.Value);
            Assert.AreEqual(14, avlTree.Root.Right.Left.Value);
            Assert.AreEqual(16, avlTree.Root.Right.Right.Value);
        }
        /*****************************************/



        /**
         * RemoveTreeHasNoItemsTest
         * -Nothing to generalize-
         **/

        [PexMethod]
        public void RemoveTreeEmptyPUT()
        {
            AvlTree<int> avlTree = new AvlTree<int>();
            bool result = avlTree.Remove(3);
            PexAssert.IsFalse(result);
        }

        /**
         * Generalizes RemoveNodeWithChildsTest, RemoveMassiveTest
         * Time: 00:07:57
         * Instrumentation issue = 0
         * Patterns: AAAA, Manual Output
         * Pex Limitations = none
         * Failing test = none
         * Comment - Since Pex generates different inputs, it is assumed that the expected scenarios are covered
         **/
        [PexMethod]
        public void RemoveTreePUT([PexAssumeUnderTest]List<int> values, int start, int end)
        {
            PexAssume.IsFalse(start < 0 || end < 0);
            PexAssume.IsTrue(start < values.Count);
            PexAssume.IsTrue(end >= start && end < values.Count);
            AvlTree<int> avlTree = new AvlTree<int>(values);
            int toRemoveCount = (end - start) == 0 ? (values.Count > 0? 1:0) : end - start;
            IList<int> toRemove = values.GetRange(start, toRemoveCount);
            foreach (int i in toRemove)
            {
                bool result = avlTree.Remove(i);
                PexAssert.IsTrue(result);
            }
            PexAssert.AreEqual(values.Count - toRemoveCount, avlTree.Count);
            IEnumerable enumerator = avlTree.GetInorderEnumerator();
            foreach (int i in enumerator)
                PexObserve.ValueForViewing<int>("tree nodes", i);
        }

       

        /**
         * Generalizes InsertionAndDeletionTest
         * Time: 00:15:14
         * Instrumentation issue = 0
         * Patterns: AAAA, Manual Output
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void InsertionAndDeletionPUT([PexAssumeUnderTest]List<int> values, int start, int end)
        {
            PexAssume.IsFalse(start < 0 || end < 0);
            PexAssume.IsTrue(start < values.Count);
            PexAssume.IsTrue(end >= start && end < values.Count);
            PexAssume.AreDistinctValues<int>(values.ToArray());
            AvlTree<int> avlTree = new AvlTree<int>();
            foreach(int i in values)
                avlTree.Add(i);
            PexAssert.AreEqual(values.Count, avlTree.Count);
            PexObserve.ValueForViewing<int>("Root", avlTree.Root.Value);
            int toRemoveCount = (end - start)==0?1:end-start;
            IList<int> toRemove = values.GetRange(start, toRemoveCount);

            foreach (int i in toRemove)
                avlTree.Remove(i);

            PexAssert.AreEqual(values.Count - toRemoveCount, avlTree.Count);
            IEnumerable enumerator = avlTree.GetInorderEnumerator();
            foreach(int i in enumerator)
                PexObserve.ValueForViewing<int>("tree nodes",i);
        }

        /**
         * Generalizes ClearTest, CountTest
         * Time: 00:02:22
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void ClearPUT([PexAssumeUnderTest]List<int> values)
        {
            AvlTree<int> actual = new AvlTree<int>(values);
            PexAssert.AreEqual(values.Count, actual.Count);
            actual.Clear();
            PexAssert.AreEqual(0, actual.Count);
        }

        /**
         * Generalizes TraversalTest
         * Time: 00:06:17
         * Instrumentation issue = 0
         * Patterns: AAAA
         * Pex Limitations = none
         * Failing test = none
         * Comment: Force stopped after 15 tests and 211 runs
         **/
        [PexMethod]
        public void TraversalPUT([PexAssumeUnderTest]List<int> values)
        {
            PexAssume.AreDistinctValues<int>(values.ToArray());
            AvlTree<int> avlTree = new AvlTree<int> (values);
            List<int> actual = new List<int>();
            foreach (int value in avlTree.GetInorderEnumerator())
            {
                actual.Add(value);
            }
            values.Sort();
            CollectionAssert.AreEqual(values, actual);
        }



     /**
      * Generalizes ItemsAreEqualTest
      * Time: 00:05:02
      * Instrumentation issue = 0
      * Patterns: AAAA, AllowedException
      * Pex Limitations = none
      * Failing test = none
      **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        public void CopyConstructorPUT(List<int> values)
        {
            AvlTree<int> avlTree = new AvlTree<int>(values);
            PexAssume.AreDistinctValues<int>(values.ToArray());
            List<int> actual = new List<int>(values.Count);
            foreach(int i in avlTree)
            {
                actual.Add(i);
            }
            CollectionAssert.AreEquivalent(values.ToArray(), actual.ToArray());
        }
    }
}
