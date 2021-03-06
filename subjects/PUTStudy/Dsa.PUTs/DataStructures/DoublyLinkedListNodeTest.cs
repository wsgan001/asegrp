﻿using Dsa.DataStructures;
using Microsoft.Pex.Framework;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    /// <summary>
    /// Tests for DoublyLinkedListNode.
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class DoublyLinkedListNodeTest
    {
        /**
         * Generalize ConstructorTest
         * Time: 00:01:32
         * Instrumentation issue = 0
         * Patterns: AAA
         * Pex Limitations = none
         * Failing test = none
         **/
        [PexMethod]
        public void ConstructorPUT(int value)
        {
            DoublyLinkedListNode<int> n = new DoublyLinkedListNode<int>(value);
            PexAssert.AreEqual(value, n.Value);
            PexAssert.IsNull(n.Previous);
            PexAssert.IsNull(n.Next);
        }
    }
}