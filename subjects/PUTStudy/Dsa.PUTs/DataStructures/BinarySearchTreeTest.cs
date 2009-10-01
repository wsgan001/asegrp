using System;
using System.Collections;
using System.Collections.Generic;
using Dsa.DataStructures;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Validation;
using System.Collections.ObjectModel;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    /// <summary>
    /// Tests for BinarySearchTree.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class BinarySearchTreeTest
    {
        /// <summary>
        /// Check to see that the fields are initialized correctly.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 47 sec
        /// Pattern: Constructor Test        
        public void ConstructorTest()
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>();
            PexAssert.IsNull(bst.Root);
        }

        /// <summary>
        /// Check to see that the insert asserts the correct state changes.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 25 sec
        /// Pattern: Constructor Test, Round Trip
        public void InsertRootNullTest(string input)
        {
            BinarySearchTree<string> bst = new BinarySearchTree<string> { input };
            PexAssert.AreEqual(input, bst.Root.Value);
        }

        /// <summary>
        /// Check to see that the state of the BinarySearchTree is updated correctly when inserting more than one node into the tree.
        /// </summary>
        [PexMethod]        
        /// Summary
        /// Time: 5 min 17 sec
        /// Pattern: Manual output review, AAAA, Constructor Test
        /// Combines 6 tests "InsertTest, GetEnumeratorGenericTest, GetPostorderEnumeratorTest
        /// GetInorderEnumeratorTest, CountTest, GetBreadthFirstEnumeratorTest, ToArrayNoItemsInBstTest" into one PUT
        public void InsertTest([PexAssumeUnderTest]int[] newElements)
        {            
            BinarySearchTree<int> bst = new BinarySearchTree<int>(newElements);

            PexAssert.AreEqual(newElements.Length, bst.Count);
            PexAssert.IsNotNull(bst.GetEnumerator());
            PexAssert.IsNotNull(bst.GetPostorderEnumerator());
            PexAssert.IsNotNull(bst.GetInorderEnumerator());
            PexAssert.IsNotNull(bst.GetBreadthFirstEnumerator());
            PexObserve.ValueForViewing<int[]>("newElements", newElements);
            PexObserve.ValueForViewing<int[]>("Constructed tree", bst.ToArray());
        }

        /// <summary>
        /// Check to make sure that a non-null IEnumerator object is returned when calling GetEnumerator on a bst object.
        /// </summary>
        //[TestMethod]
        //public void GetEnumeratorGenericTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 5, 3, 8, 12, 11};
        //    Assert.IsNotNull(bst.GetEnumerator());
        //}

        /// <summary>
        /// Check to make sure that a non-null IEnumerator object is returned when calling the GetPostorderEnumerator on a bst object.
        /// </summary>
        //[TestMethod]
        //public void GetPostorderEnumeratorTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 5, 3, 20, 17, 30};

        //    Assert.IsNotNull(bst.GetPostorderEnumerator());
        //}

        /// <summary>
        /// Check to see that a non-null IEnumerator object is returned when calling the GetInorderEnumerator on a bst object.
        /// </summary>
        //[TestMethod]
        //public void GetInorderEnumeratorTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 5, 3, 20, 17, 30};

        //    Assert.IsNotNull(bst.GetInorderEnumerator());
        //}

        /// <summary>
        /// Check to see that count returns the correct value.
        /// </summary>
        //[TestMethod]
        //public void CountTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 4, 67};

        //    Assert.AreEqual(3, bst.Count);
        //}

        /// <summary>
        /// Check to see that IsReadOnly property returns the correct value.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 27 sec
        /// Pattern: AAA, Constructor Test
        public void ReadOnlyTest([PexAssumeUnderTest]int[] elements)
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            ICollection<int> actual = bst;
            PexAssert.IsFalse(actual.IsReadOnly);
        }

        /// <summary>
        /// Check to see that IsSynchronized property returns the correct value.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 27 sec
        /// Pattern: AAA,Constructor Test
        public void IsSynchronizedTest([PexAssumeUnderTest]Collection<int> elements)
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            ICollection actual = bst;
            PexAssert.IsFalse(actual.IsSynchronized);
        }

        /// <summary>
        /// Check to see that a non null enumerator object is returned.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 27 sec
        /// Pattern: AAA, Constructor Test
        public void ICollectionGetEnumeratorTest([PexAssumeUnderTest]int[] elements)
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            ICollection actual = bst;
            PexAssert.IsNotNull(actual.GetEnumerator());
        }

        /// <summary>
        /// Check to see that SyncRoot returns a non null object.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 27 sec
        /// Pattern: AAA, Constructor Test
        public void SyncRootTest([PexAssumeUnderTest]int[] elements)
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            ICollection actual = bst;
            PexAssert.IsNotNull(actual.SyncRoot);
        }

        /// <summary>
        /// Check to see that calling Clear resets the collection to its default state.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        /// Summary
        /// Time: 1 min 15 sec
        /// Pattern: AAA, State Relation, AllowedException
        public void ClearTest(string[] elements)
        {
            BinarySearchTree<string> bst = new BinarySearchTree<string>(elements);
            bst.Clear();
            PexAssert.IsNull(bst.Root);
            PexAssert.AreEqual(0, bst.Count);
        }

        /// <summary>
        /// Check to see that the FindMin method returns the correct value.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        /// Summary
        /// Time: 3 min 31 sec
        /// Pattern: AAAA, Commutative Diagram, AllowedException
        /// Generalizes two tests "FindMinTest, FindMinTreeEmptyTest" into one PUT
        public void FindMinTest([PexAssumeUnderTest]int[] elements)
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);

            //Compute the minimum element
            int minValue = Int32.MaxValue;
            foreach (int val in elements)
                minValue = minValue > val ? val : minValue;

            PexAssert.AreEqual(minValue, bst.FindMin());
        }

        /// <summary>
        /// Check to see the correct exception is thrown when calling FindMin on an empty tree.
        /// </summary>
        //[TestMethod]
        //[ExpectedException(typeof(InvalidOperationException))]
        //public void FindMinTreeEmptyTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int>();

        //    bst.FindMin();
        //}

        /// <summary>
        /// Check to see that FindMax returns the largest value in the bst.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(InvalidOperationException))]
        /// Summary
        /// Time: 1 min 45 sec
        /// Pattern: AAAA, Commutative Diagram, AllowedException
        /// Generalizes two tests "FindMaxTest, FindMaxTreeEmptyTest" into one PUT
        public void FindMaxTest([PexAssumeUnderTest]int[] elements)
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            //Compute the maximum element
            int maxValue = Int32.MinValue;
            foreach (int val in elements)
                maxValue = maxValue < val ? val : maxValue;

            PexAssert.AreEqual(maxValue, bst.FindMax());
        }


        /// <summary>
        /// Check to see that the correct exception is thrown when calling FindMax on an empty tree.
        /// </summary>
        //[TestMethod]
        //[ExpectedException(typeof(InvalidOperationException))]
        //public void FindMaxTreeEmptyTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int>();

        //    bst.FindMax();
        //}

        /// <summary>
        /// Check to see that Contains returns the correct value.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 4 min 12 sec
        /// Pattern: AAAA, State Relation
        /// Generalizes two tests "ContainsTest, ContainsItemNotPresentTest" into one PUT
        public void ContainsTest([PexAssumeUnderTest]List<int> elements, int newElement)
        {
            PexAssume.IsFalse(elements.Contains(newElement));
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);

            foreach(int elem in elements)
                PexAssert.IsTrue(bst.Contains(elem));
            PexAssert.IsFalse(bst.Contains(newElement));
        }

        /// <summary>
        /// Check to see that the correct value is returned when the item is not contained within the bst.
        /// </summary>
        //[TestMethod]
        //public void ContainsItemNotPresentTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {12, 5, 3, 8, 42};

        //    Assert.IsFalse(bst.Contains(99));
        //    Assert.IsFalse(bst.Contains(1));
        //}

        /// <summary>
        /// Check to see that calling GetBreadthFirstEnumerator returns a non null enumerator.
        /// </summary>
        //[TestMethod]
        //public void GetBreadthFirstEnumeratorTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int>();
        //    Assert.IsNotNull(bst.GetBreadthFirstEnumerator());
        //}

        /// <summary>
        /// Check to see that calling ToArray returns the correct array.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 3 min 31 sec
        /// Pattern: Manual output review
        /// Difficulties with generalization - loss of test oracle
        public void ToArrayTest([PexAssumeUnderTest]int[] elements)
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            int[] actual = bst.ToArray();
            PexObserve.ValueForViewing<int[]>("BinarySearchTree Output", actual);            
        }

        /// <summary>
        /// Check to see that the correct (empty) array is returned.
        /// </summary>
        //[TestMethod]
        //public void ToArrayNoItemsInBstTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int>();
        //    int[] actual = bst.ToArray();
        //    PexAssert.AreEqual(0, actual.Length);
        //}

        /// <summary>
        /// Check to see that calling CopyTo results in the target array being updated correctly.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 4 min 00 sec
        /// Pattern: Manual output review
        /// Generalizes two unit tests "CopyToTest and CopyToStartingSpecifiedIndexTest" into one PUT
        /// Difficulties with generalization - loss of test oracle
        public void CopyToTest([PexAssumeUnderTest]int[] elements, int position)
        {
            PexAssume.IsTrue(position >= 0 && position <= 1000);
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            int[] actual = new int[bst.Count + position];

            if (position == 0)
            {
                bst.CopyTo(actual);
            }
            else
            {
                bst.CopyTo(actual, position);
            }

            //CollectionAssert.AreEqual(expected, actual);
            PexObserve.ValueForViewing<int[]>("SearchTree Contents", actual);
        }

        /// <summary>
        /// Check to see that calling CopyTo starting at specified index results in the target array being updated correctly.
        /// </summary>
        //[TestMethod]
        //public void CopyToStartingSpecifiedIndexTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> { 12, 8, 6, 11, 42 };
        //    int[] expected = { 0, 0, 0, 12, 8, 42, 6, 11 };
        //    int[] actual = new int[bst.Count + 3];

        //    bst.CopyTo(actual, 3);

        //    CollectionAssert.AreEqual(expected, actual);
        //}

        /// <summary>
        /// Check to see that calling ICollection.CopyTo throws the correct exception.
        /// </summary>
        [PexMethod]
        [PexAllowedException(typeof(NotSupportedException))]
        /// Summary
        /// Time: 0 min 40 sec
        /// Pattern: Allowed Exception        
        public void ICollectionCopyToTest([PexAssumeUnderTest]int[] elements)
        {
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            ICollection actual = bst;
            int[] array = new int[elements.Length];
            actual.CopyTo(array, 0);
        }

        /// <summary>
        /// Check to see if a non-null reference is returned for a node that is in the bst with the specified value that is located in the left subtree.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 2 min 55 sec
        /// Pattern: AAAA, Round Trip
        /// Generalizes three unit tests "FindNodeValidLeftChildTest, FindNodeValidRightChildTest, FindNodeNotInBstTest" into one PUT
        public void FindNodeValidLeftChildTest([PexAssumeUnderTest]List<int> elements, int position, int notExistingElement)
        {
            PexAssume.IsTrue(position >= 0 && position < elements.Count);
            PexAssume.IsFalse(elements.Contains(notExistingElement));
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);

            PexAssert.IsNotNull(bst.FindNode(elements[position]));
            PexAssert.AreEqual(elements[position], bst.FindNode(elements[position]).Value);
            PexAssume.IsNull(bst.FindNode(notExistingElement));
        }

        /// <summary>
        /// Check to see if a non-null reference is returned for a node that is in the bst with the specified value that is located in the right subtree.
        /// </summary>
        //[TestMethod]
        //public void FindNodeValidRightChildTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 5, 14};

        //    Assert.IsNotNull(bst.FindNode(14));
        //    Assert.AreEqual(14, bst.FindNode(14).Value);
        //}

        /// <summary>
        /// Check to see that FindNode returns null when a value that isn't in the bst is specified.
        /// </summary>
        //[TestMethod]
        //public void FindNodeNotInBstTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 5, 15};
        //    Assert.IsNull(bst.FindNode(34));
        //}

        /// <summary>
        /// Check to see that the correct node is returned when finding the parent of a node with the specified value located in the left subtree.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 4 min 06 sec
        /// Pattern: Manual output review       
        /// Generalizes 4 unit tests FindParentLeftSubTreeTest, FindParentRightSubTreeTest, FindParentRightSubTreeNodeNotPresentTest,
        ///                     FindParentLeftSubTreeNodeNotPresentTest into one PUT
        public void FindParentLeftSubTreeTest([PexAssumeUnderTest]int[] elements, int position)
        {
            PexAssume.AreDistinctValues<int>(elements);
            PexAssume.IsTrue(position >= 0 && position < elements.Length);            
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            PexAssume.AreNotEqual(bst.Root.Value, elements[position]);
            PexObserve.ValueForViewing<int>("FindParentValue", bst.FindParent(elements[position]).Value);            
        }

        /// <summary>
        /// Check to see that the correct node is returned when finding the parent of a node with
        /// the specified value located in the right subtree.
        /// </summary>
        //[TestMethod]
        //public void FindParentRightSubTreeTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 17, 4, 9, 23};

        //    Assert.AreEqual(17, bst.FindParent(23).Value);
        //    Assert.AreEqual(10, bst.FindParent(17).Value);
        //}

        /// <summary>
        /// Check to see that null is returned when looking for a value that should be located in the right subtree.
        /// </summary>
        //[TestMethod]
        //public void FindParentRightSubTreeNodeNotPresentTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {9, 23, 17, 10};

        //    Assert.IsNull(bst.FindParent(32));
        //}

        /// <summary>
        /// Check to see that null is returned when looking for a value that should be located in the left subtree.
        /// </summary>
        //[TestMethod]
        //public void FindParentLeftSubTreeNodeNotPresentTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 9, 23, 17};

        //    Assert.IsNull(bst.FindParent(4));
        //}

        /// <summary>
        /// Check to see that calling FindParent using the value of the root node returns null as the root node has no parent node.
        /// </summary>
        [PexMethod(MaxRunsWithoutNewTests = 200)]
        /// Summary
        /// Time: 4 min 09 sec
        /// Pattern: AAAA, State Relation        
        public void FindParentRootNodeTest([PexAssumeUnderTest]int[] elements)
        {
            PexAssume.IsTrue(elements.Length > 0);
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);            
            PexAssert.IsNull(bst.FindParent(bst.Root.Value));
        }

        /// <summary>
        /// Check to see that null is returned when trying to find the parent of a tree with 0 items.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 24 sec
        /// Pattern: AAAA, State Relation        
        public void FindParentNoItemsInBstTest([PexAssumeUnderTest]List<int> elements, int notexistelement)
        {
            PexAssume.IsFalse(elements.Contains(notexistelement));
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            PexAssert.IsNull(bst.FindParent(notexistelement));            
        }

        /// <summary>
        /// Check to see that trying to remove a node that is not in the bst returns the correct value.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 24 sec
        /// Pattern: AAAA, State Relation
        public void RemoveNodeNotFoundTest([PexAssumeUnderTest]List<int> elements, int notexistelement)
        {
            PexAssume.IsFalse(elements.Contains(notexistelement));
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
            PexAssert.IsFalse(bst.Remove(notexistelement));
        }

        /// <summary>
        /// Check to see that removing a leaf node with a value less than its parent leaves the bst in the correct state.
        /// </summary>
        //[TestMethod]
        //public void RemoveLeafValueLessThanParentTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 7, 12, 11};

        //    Assert.IsTrue(bst.Remove(7));
        //    Assert.IsNull(bst.Root.Left);
        //    Assert.AreEqual(3, bst.Count);
        //}

        /// <summary>
        /// Check to see taht removing a leaf node with a value greater than or equal to its parent leaves the bst in the correct state.
        /// </summary>
        //[TestMethod]
        //public void RemoveLeafValueGreaterThanOrEqualToParentTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 7, 12};

        //    Assert.IsTrue(bst.Remove(12));
        //    Assert.IsNull(bst.Root.Right);
        //    Assert.AreEqual(2, bst.Count);
        //}

        /// <summary>
        /// Check to see that removing a node that has only a right subtree leaves the bst in the correct state when the value of the 
        /// nodeToRemove is greater than or equal to the parent.
        /// </summary>
        //[TestMethod]
        //public void RemoveNodeWithRightSubtreeOnlyChildGreaterThanOrEqualToParentTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 7, 12, 13};

        //    Assert.IsTrue(bst.Remove(12));
        //    Assert.AreEqual(13, bst.Root.Right.Value);
        //    Assert.AreEqual(3, bst.Count);
        //}

        /// <summary>
        /// Check to see that removing a node that has only a right subtree leaves the bst in the correct state when the value of the 
        /// nodeToRemove is less than the parent.
        /// </summary>
        //[TestMethod]
        //public void RemoveNodeWithRightSubtreeOnlyChildLessThanParentTest()
        //{
        //    BinarySearchTree<int> bst = new BinarySearchTree<int> {10, 7, 12, 13, 8};

        //    Assert.IsTrue(bst.Remove(7));
        //    Assert.AreEqual(8, bst.Root.Left.Value);
        //    Assert.AreEqual(4, bst.Count);
        //}

        /// <summary>
        /// Check to see that removing a node that has only a left subtree leaves the bst in the correct state when the value of the 
        /// nodeToRemove is less than the parent. 
        /// </summary>
        //[PexMethod]
        //public void RemoveNodeWithLeftSubtreeOnlyChildLessThanParentTest([PexAssumeUnderTest]int[] elements, int )
        //{
        //    PexAssume.AreDistinctValues<int>(elements);

        //    BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);

        //    PexAssert.IsTrue(bst.Remove(17));
        //    Assert.AreEqual(13, bst.Root.Left.Left.Value);
        //    Assert.AreEqual(7, bst.Count);
        //}

        /// <summary>
        /// Check to see that removing a node that has only a left subtree leaves the bst in the correct state when the value of the nodeToRemove 
        /// is greater than or equal to the parent. 
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 27 sec
        /// Pattern: Manual Output Review, AAAA (A case of partial generalization)        
        /// Difficulties in generalization - loss of test oracle
        /// Failing tests : Also raises NullPointerException but the reason is the same as the test RemoveNodeWithBothSubtreesTest\
        /// Generalizes six unit tests RemoveNodeWithLeftSubtreeOnlyChildGreaterThanOrEqualToParentTest and 
        ///             RemoveNodeWithLeftSubtreeOnlyChildLessThanParentTest 
        ///             RemoveNodeWithRightSubtreeOnlyChildLessThanParentTest
        ///             RemoveNodeWithRightSubtreeOnlyChildGreaterThanOrEqualToParentTest
        ///             RemoveLeafValueGreaterThanOrEqualToParentTest
        ///             RemoveLeafValueLessThanParentTest
        public void RemoveNodeWithLeftSubtreeOnlyChildGreaterThanOrEqualToParentTest([PexAssumeUnderTest]int[] elements, int position)
        {
            PexAssume.IsTrue(position >= 0 && position < elements.Length);
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);

            PexAssert.IsTrue(bst.Remove(elements[position]));
            PexObserve.ValueForViewing<int[]>("Binary Search Contents", bst.ToArray());
            //Assert.AreEqual(61, bst.Root.Right.Right.Right.Value); //Cannot be generalized
            //Assert.AreEqual(9, bst.Count);
        }

        /// <summary>
        /// Check to see that removing a node with a left and right subtree leaves the bst in the correct state.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 18 min 23 sec
        /// Pattern: AAAA, Commutative diagram        
        /// A classic example for commutative diagram, as the operation is made in parallel using lists
        /// Notes: A hashset is taken as input as we need unique elements for insertion into BST
        /// Failing tests : Exposed a serious bug inside binary search tree implementation that throws NullPointerException
        public void RemoveNodeWithBothSubtreesTest([PexAssumeUnderTest]HashSet<int> setelem, int position)
        {
            List<int> elements = new List<int>(setelem);
            PexAssume.IsTrue(position >= 0 && position < elements.Count);
            PexAssume.IsTrue(elements.Count > 0);
                        
            BinarySearchTree<int> bst = new BinarySearchTree<int>(elements);
                       
            int targetElement = elements[position];
            PexAssert.IsTrue(bst.Remove(targetElement));
            elements.Remove(targetElement);
            
            if(elements.Contains(targetElement)) //Can happen when there are duplicate elements in the list
                PexAssert.IsNotNull(bst.FindNode(targetElement));
            else
                PexAssert.IsNull(bst.FindNode(targetElement));

            //Assert.AreEqual(19, bst.Root.Left.Value); //Cannot be generalized
            PexAssert.AreEqual(elements.Count, bst.Count);
        }

        /// <summary>
        /// Check to see that removing the root node when root is the only node in the bst leaves the bst in the correct state.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 12 sec
        /// Pattern: AAAA, State Relation
        /// Generalized two unit tests "RemoveRootNoSubtreesTest and RemoveTreeHasNoItemsTest" into one PUT
        public void RemoveRootNoSubtreesTest(int element1, int element2)
        {
            PexAssume.AreNotEqual(element1, element2);
            BinarySearchTree<int> bst = new BinarySearchTree<int> { element1 };

            PexAssert.IsTrue(bst.Remove(element1));
            PexAssert.IsNull(bst.Root);
            PexAssert.AreEqual(0, bst.Count);
            PexAssert.IsFalse(bst.Remove(element2));
        }

        /// <summary>
        /// Check to see that the correct behaviour is demonstrated when reomving an item from a tree with not items in.
        /// </summary>
        //[TestMethod]
        //public void RemoveTreeHasNoItemsTest()
        //{
        //    BinarySearchTree<int> actual = new BinarySearchTree<int>();
        //    actual.Remove(10);
        //}

        /// <summary>
        /// Check to make sure the bst is left in the correct state when copying the items from an IEnumerable to it.
        /// </summary>
        [PexMethod]        
        [PexAllowedException(typeof(ArgumentNullException))]
        /// Summary
        /// Time: 1 min 57 sec
        /// Pattern: AAA, Constructor Test        
        public void CopyContructorTest(List<string> elementList)
        {            
            BinarySearchTree<string> actual = new BinarySearchTree<string>(elementList);
            PexAssert.AreEqual(elementList.Count, actual.Count);
        }
    }
}