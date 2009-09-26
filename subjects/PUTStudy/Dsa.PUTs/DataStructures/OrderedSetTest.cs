using System.Collections.Generic;
using Dsa.DataStructures;
using Dsa.Test.Utility;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Validation;
using System;

namespace Dsa.Test.DataStructures
{
    /// <summary>
    /// Tests for OrderedSet.
    /// </summary>
    [TestClass]
    public sealed partial class SetTest
    {
        /// <summary>
        /// Check to see that items are added correctly and duplicate values are ignored.
        /// </summary>
        [PexMethod(MaxConditions = 1000)]        
        /// Summary
        /// Time: 14 min 15 sec
        /// Pattern: Constructor Test, State Relation
        /// Generalizes four unit tests AddTest, ClearTest, ContainsTest, CopyIEnumerableToSetTest into one PUT
        public void AddTest([PexAssumeUnderTest]HashSet<int> newelements)
        {            
            OrderedSet<int> actual = new OrderedSet<int>(newelements);
            PexAssert.AreEqual(newelements.Count, actual.Count);

            foreach (int elem in newelements)
                PexAssert.IsTrue(actual.Contains(elem));            

            actual.Clear();
            PexAssert.AreEqual(0, actual.Count);
        }

        /// <summary>
        /// Check to see that calling Clear returns the Set to its initial state.
        /// </summary>
        //[TestMethod]
        //public void ClearTest()
        //{
        //    OrderedSet<int> actual = new OrderedSet<int> {15, 16, 89};

        //    actual.Clear();

        //    Assert.AreEqual(0, actual.Count);
        //}

        /// <summary>
        /// Check to see that that Contains returns the correct value.
        /// </summary>
        //[TestMethod]
        //public void ContainsTest()
        //{
        //    OrderedSet<int> actual = new OrderedSet<int> {12, 19, 1, 23};

        //    Assert.IsTrue(actual.Contains(19));
        //    Assert.IsFalse(actual.Contains(99));
        //}

        /// <summary>
        /// Check to see that an item is removed correctly from the Set.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 16 min 1 sec
        /// Pattern: Constructor Test, State Relation, AAAA
        /// Failing Test: Exposed a NullReferenceException inside the code of BinarySearchTree
        ///               An integer value of zero is considered as a null value. However zero is a valid value
        ///               Interesting defect that can be mentioned in the paper also and gets exposed based on
        ///               how the code path is exposed
        public void RemoveTest([PexAssumeUnderTest]int[] elements, int position)
        {
            PexAssume.IsTrue(position >= 0 && position < elements.Length);
            OrderedSet<int> actual = new OrderedSet<int>(elements);
            
            PexAssert.IsTrue(actual.Remove(elements[position]));
            PexAssert.IsFalse(actual.Remove(elements[position]));
        }

        /// <summary>
        /// Check to see that an item with the correct value and indexes is returned.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 8 min 2 sec
        /// Pattern: Commutative diagram, AAAA
        /// Generalizes two tests "ToArrayTest and GetEnumeratorTest" into one PUT
        public void ToArrayTest([PexAssumeUnderTest]HashSet<int> inputElem)
        {
            OrderedSet<int> set = new OrderedSet<int>(inputElem);
            List<int> listElem = new List<int>(inputElem);
            listElem.Sort();
            PexAssert.IsNotNull(set.GetEnumerator());
            CollectionAssert.AreEqual(listElem, set);            
        }

        /// <summary>
        /// Check to see that a non-null enumerator is returned.
        /// </summary>
        //[TestMethod]
        //public void GetEnumeratorTest()
        //{
        //    OrderedSet<int> set = new OrderedSet<int> {10, 23, 1, 89, 34};
        //    List<int> expected = new List<int>();

        //    foreach (int item in set) expected.Add(item);

        //    Assert.IsNotNull(set.GetEnumerator());
        //    CollectionAssert.AreEqual(set, expected);
        //}

        /// <summary>
        /// Check to see when using an value type with the same properties twice that only one of them is added
        /// to the set.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 4 min 10 sec
        /// Pattern: AAAA, State Relation
        /// Failing Test - Exposed a NullReferenceException in Person class. This could be a minor issue as Person class is a 
        ///                temporary class written for testing purposes.
        public void DuplicateObjectTest([PexAssumeUnderTest]OrderedSet<Person> actual, string firstname, string lastname)
        {
            int prevCount = actual.Count; 
            Person p1 = new Person { FirstName = firstname, LastName = lastname };
            Person p2 = new Person { FirstName = firstname, LastName = lastname };

            actual.Add(p1);
            actual.Add(p2);

            PexAssert.AreEqual(prevCount + 1, actual.Count);
        }

        /// <summary>
        /// Check to see that the correct number of items are contained within the set after copying an IEnumerable collection to the
        /// set.
        /// </summary>
        //[TestMethod]
        //public void CopyIEnumerableToSetTest()
        //{
        //    List<string> originalCollection = new List<string> { "Granville", "John", "Granville", "Betty" };
        //    OrderedSet<string> actual = new OrderedSet<string>(originalCollection);

        //    Assert.AreEqual(3, actual.Count);
        //}
    }
}