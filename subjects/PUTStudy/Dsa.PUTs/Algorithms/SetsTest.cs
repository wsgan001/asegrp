using System;
using Dsa.Algorithms;
using Dsa.DataStructures;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Validation;
using NUnit.Framework;

namespace Dsa.PUTs.Algorithms
{
    /// <summary>
    /// Tests for Set algorithms
    /// </summary>
    [TestFixture]
    [PexClass]
    public sealed partial class SetsTest
    {
        /**
         * One PUT generalizes PerumtationsEmptySetZeroItems, PermutationsEmptySetTest, PermutationSetNullTest, PermutationTest
         * Time: 00:10:56
         * Instrumentation issue = 0
         * Patterns: Manual output, AllowedException
         * Pex Limitations = none
         **/
        [PexMethod]
        [PexAllowedException(typeof(ArgumentNullException))]
        [PexAllowedException(typeof(ArgumentOutOfRangeException))]
        public void PerumtationsEmptySetPUT(OrderedSet<int> actual, int actualVal)
        {
            if (actual == null)
            {
                actual.Permutations(actualVal);
                PexAssert.IsTrue(false, "ArgumentNullException should be thrown");
            }
            else if (actualVal == 0)
            {
                actual.Permutations(actualVal);
                PexAssert.IsTrue(false, "ArgumentOutOfRangeException should be thrown");
            }
            else
            {
                PexObserve.ValueForViewing<int[]>("actual OrderedSet", actual.ToArray());
                PexObserve.ValueForViewing("Result of permutation",actual.Permutations(actualVal));
            }
        }
    }
}