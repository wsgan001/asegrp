using Dsa.DataStructures;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;

namespace Dsa.Test.DataStructures
{
    /// <summary>
    /// BinaryTreeNode(Of T) tests.
    /// </summary>
    [TestClass]
    [PexClass]
    public sealed partial class BinaryTreeNodeTest
    {
        /**
        * Generalize AssignNodeTest
        * Time: 00:00:44
        * Instrumentation issue = 0
        * Patterns: AAA
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void AssignNodePUT(int parent, int child)
        {
            BinaryTreeNode<int> node = new BinaryTreeNode<int>(parent) {Right = new BinaryTreeNode<int>(child)};

            PexAssert.AreEqual(child, node.Right.Value);
        }

        /**
        * Generalize BinaryTreeNodeConstructorTest
        * Time: 00:00:39
        * Instrumentation issue = 0
        * Patterns: AAA
        * Pex Limitations = none
        * Failing test = none
        **/
        [PexMethod]
        public void BinaryTreeNodeConstructorPUT(int value)
        {
            BinaryTreeNode<int> node = new BinaryTreeNode<int>(value);

            PexAssert.AreEqual(value, node.Value);
            PexAssert.IsNull(node.Left);
            PexAssert.IsNull(node.Right);
        }

    }
}