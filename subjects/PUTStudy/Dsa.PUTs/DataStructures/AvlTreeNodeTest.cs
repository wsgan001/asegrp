using Dsa.DataStructures;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;

namespace Dsa.PUTs.DataStructures
{
    /// <summary>
    /// Tests for AvlTreeNode.
    /// </summary>
    [TestClass]
    [PexClass]
    public sealed partial class AvlTreeNodeTest
    {
      /** PUT to generalize HeightTest - No advantage
       *  Time: 00:00:55
       * Instrumentation issue = 0
       * Patterns: Manual output, AllowedException
       * Pex Limitations = none
       * Failing test = 1 - when actual=int.MaxValue, an OverflowException is thrown
      **/
        [PexMethod]
        public void HeightPUT(int value)
        {
            AvlTreeNode<int> actual = new AvlTreeNode<int>(value);
            PexAssert.AreEqual(1, actual.Height);
        }
    }
}