using Dsa.DataStructures;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;

namespace Dsa.Test.DataStructures
{
    /// <summary>
    /// Tests for SinglyLinkedListNode.
    /// </summary>
    [TestClass]
    public sealed partial class SinglyLinkedListNodeTest
    {
        /// <summary>
        /// Check to see that the expected Int32 value of a node is returned.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 
        /// Pattern: Constructor Test
        public void ValueIntTest(int newElem)
        {
            SinglyLinkedListNode<int> n = new SinglyLinkedListNode<int>(newElem);
            PexAssert.AreEqual(newElem, n.Value);
        }

        /// <summary>
        /// Check to see that the expected string reference type value of a node is returned.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 1 min 
        /// Pattern: Constructor Test
        public void ValueStringTest(string newElem)
        {
            SinglyLinkedListNode<string> n = new SinglyLinkedListNode<string>(newElem);
            PexAssert.AreEqual(newElem, n.Value);
        }

        /// <summary>
        /// Check to see that the next node of a node is correct.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 2 min 30 sec
        /// Pattern: Round trip, constructor test
        public void NextTest([PexAssumeUnderTest]SinglyLinkedListNode<int> n2, int value)
        {            
            SinglyLinkedListNode<int> n1 = new SinglyLinkedListNode<int>(value) {Next = n2};
            PexAssert.AreEqual(n2.Value, n1.Next.Value);
        }
    }
}