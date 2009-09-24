using System.Collections.Generic;
using Dsa.Utility;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Dsa.Test.Utility
{
    /// <summary>
    /// Tests for the compare methods.
    /// </summary>
    [TestClass]
    [PexClass]
    public sealed partial class CompareTest
    {
        /// <summary>
        /// Check to see that IsLessThan method returns the correct value.
        /// </summary>
        [PexMethod]
        /// Summary
        /// Time: 9 min 37 sec
        /// Pattern: AAA, Roundtrip
        /// Combines three unit tests into one.
        public void ComparerPUT(int x1, int x2)
        {
            IComparer<int> comparer = Comparer<int>.Default;
            if (x1 < x2)
            {
                PexAssert.IsTrue(Compare.IsLessThan(x1, x2, comparer));
                PexAssert.IsFalse(Compare.IsGreaterThan(x1, x2, comparer));
            }
            else if (x1 > x2)
            {
                PexAssert.IsTrue(Compare.IsGreaterThan(x1, x2, comparer));
                PexAssert.IsFalse(Compare.IsLessThan(x1, x2, comparer));
                PexAssert.IsFalse(Compare.AreEqual(x1, x2, comparer));
            }
            else
            {
                PexAssert.IsTrue(Compare.AreEqual(x1, x2, comparer));
            }
        }

        /// <summary>
        /// Check to see that IsGreaterThan methods returns the correct value.
        /// </summary>
        //[Test]
        //public void IsGreaterThanTest()
        //{
        //    IComparer<int> comparer = Comparer<int>.Default;

        //    Assert.IsTrue(Compare.IsGreaterThan(4, 3, comparer));
        //    Assert.IsFalse(Compare.IsGreaterThan(3, 4, comparer));
        //}

        /// <summary>
        /// Check to see that AreEqual method returns the correct value.
        /// </summary>
        //[Test]
        //public void AreEqualTest()
        //{
        //    IComparer<int> comparer = Comparer<int>.Default;

        //    Assert.IsTrue(Compare.AreEqual(3, 3, comparer));
        //    Assert.IsFalse(Compare.AreEqual(2, 3, comparer));
        //}
    }
}