// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System.Collections.Generic;
using Dsa.PUTs.DataStructures;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    public partial class SetTest
    {
        [Test]
        [PexGeneratedBy(typeof(SetTest))]
        public void AddTest07()
        {
            HashSet<int> hashSet;
            int[] ints = new int[0];
            hashSet = HashSetFactory.Create(ints);
            this.AddTest(hashSet);
            PexAssert.IsNotNull((object)hashSet);
            PexAssert.AreEqual<int>(0, hashSet.Count);
            PexAssert.IsNotNull(hashSet.Comparer);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest))]
        public void AddTest08()
        {
            HashSet<int> hashSet;
            int[] ints = new int[4];
            hashSet = HashSetFactory.Create(ints);
            this.AddTest(hashSet);
            PexAssert.IsNotNull((object)hashSet);
            PexAssert.AreEqual<int>(1, hashSet.Count);
            PexAssert.IsNotNull(hashSet.Comparer);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest))]
        public void AddTest09()
        {
            HashSet<int> hashSet;
            int[] ints = new int[4];
            ints[0] = 57845396;
            ints[1] = 956301312;
            hashSet = HashSetFactory.Create(ints);
            this.AddTest(hashSet);
            PexAssert.IsNotNull((object)hashSet);
            PexAssert.AreEqual<int>(3, hashSet.Count);
            PexAssert.IsNotNull(hashSet.Comparer);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest))]
        public void AddTest10()
        {
            HashSet<int> hashSet;
            int[] ints = new int[4];
            ints[0] = 229376;
            ints[1] = -2147254272;
            hashSet = HashSetFactory.Create(ints);
            this.AddTest(hashSet);
            PexAssert.IsNotNull((object)hashSet);
            PexAssert.AreEqual<int>(3, hashSet.Count);
            PexAssert.IsNotNull(hashSet.Comparer);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest))]
        public void AddTest11()
        {
            HashSet<int> hashSet;
            int[] ints = new int[4];
            ints[0] = 704643072;
            ints[1] = -1442840576;
            ints[2] = int.MinValue;
            ints[3] = int.MinValue;
            hashSet = HashSetFactory.Create(ints);
            this.AddTest(hashSet);
            PexAssert.IsNotNull((object)hashSet);
            PexAssert.AreEqual<int>(3, hashSet.Count);
            PexAssert.IsNotNull(hashSet.Comparer);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest))]
        [Ignore("the test state was: path bounds exceeded")]
        public void AddTest12()
        {
            HashSet<int> hashSet;
            int[] ints = new int[198];
            hashSet = HashSetFactory.Create(ints);
            this.AddTest(hashSet);
        }
    }
}
