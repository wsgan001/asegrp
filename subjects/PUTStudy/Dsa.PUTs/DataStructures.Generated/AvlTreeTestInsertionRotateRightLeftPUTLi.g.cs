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
    public partial class AvlTreeTest
    {
        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT12()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[1];
            ints[0] = int.MinValue;
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[0];
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(4, list1.Capacity);
            PexAssert.AreEqual<int>(2, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(1, list.Capacity);
            PexAssert.AreEqual<int>(1, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT13()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[3];
            ints1[0] = -1073745921;
            ints1[1] = -4098;
            ints1[2] = -4098;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(3, list1.Capacity);
            PexAssert.AreEqual<int>(3, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        [Ignore("the test state was: path bounds exceeded")]
        public void InsertionRotateRightLeftPUT14()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[63];
            ints1[0] = int.MinValue;
            ints1[1] = 1;
            ints1[2] = 1;
            ints1[3] = 1;
            ints1[4] = 1;
            ints1[5] = 1;
            ints1[6] = 1;
            ints1[7] = 1;
            ints1[8] = 1;
            ints1[9] = 1;
            ints1[10] = 1;
            ints1[11] = 1;
            ints1[12] = 1;
            ints1[13] = 1;
            ints1[14] = 1;
            ints1[15] = 1;
            ints1[16] = 1;
            ints1[17] = 1;
            ints1[18] = 1;
            ints1[19] = 1;
            ints1[20] = 1;
            ints1[21] = 1;
            ints1[22] = 1;
            ints1[23] = 1;
            ints1[24] = 1;
            ints1[25] = 1;
            ints1[26] = 1;
            ints1[27] = 1;
            ints1[28] = 1;
            ints1[29] = 1;
            ints1[30] = 1;
            ints1[31] = 1;
            ints1[32] = 1;
            ints1[33] = 1;
            ints1[34] = 1;
            ints1[35] = 1;
            ints1[36] = 1;
            ints1[37] = 1;
            ints1[38] = 1;
            ints1[39] = 1;
            ints1[40] = 1;
            ints1[41] = 1;
            ints1[42] = 1;
            ints1[43] = 1;
            ints1[44] = 1;
            ints1[45] = 1;
            ints1[46] = 1;
            ints1[47] = 1;
            ints1[48] = 1;
            ints1[49] = 1;
            ints1[50] = 1;
            ints1[51] = 1;
            ints1[52] = 1;
            ints1[53] = 1;
            ints1[54] = 1;
            ints1[55] = 1;
            ints1[56] = 1;
            ints1[57] = 1;
            ints1[58] = 1;
            ints1[59] = 1;
            ints1[60] = 1;
            ints1[61] = 1;
            ints1[62] = 1;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT15()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[2];
            ints1[0] = 1;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(2, list1.Capacity);
            PexAssert.AreEqual<int>(2, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT16()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[3];
            ints1[0] = -2147467261;
            ints1[1] = 16386;
            ints1[2] = 16384;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(3, list1.Capacity);
            PexAssert.AreEqual<int>(3, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT17()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[4];
            ints1[0] = -2;
            ints1[1] = 1350582273;
            ints1[2] = -2;
            ints1[3] = -2;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(4, list1.Capacity);
            PexAssert.AreEqual<int>(4, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT18()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[5];
            ints1[0] = -3;
            ints1[1] = 2147483644;
            ints1[2] = -3;
            ints1[3] = -3;
            ints1[4] = 2147483640;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(5, list1.Capacity);
            PexAssert.AreEqual<int>(5, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT19()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[7];
            ints1[0] = -2088763394;
            ints1[1] = int.MinValue;
            ints1[2] = 58720253;
            ints1[3] = 54525951;
            ints1[4] = 54525951;
            ints1[5] = 54525951;
            ints1[6] = 54525951;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(7, list1.Capacity);
            PexAssert.AreEqual<int>(7, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT20()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[3];
            ints1[0] = 3;
            ints1[1] = 1;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(3, list1.Capacity);
            PexAssert.AreEqual<int>(3, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT21()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[6];
            ints1[0] = -87381;
            ints1[1] = 2147396270;
            ints1[2] = -87381;
            ints1[3] = 2147396266;
            ints1[4] = 2147396258;
            ints1[5] = 2147396256;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(6, list1.Capacity);
            PexAssert.AreEqual<int>(6, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }

        [Test]
        [PexGeneratedBy(typeof(AvlTreeTest))]
        public void InsertionRotateRightLeftPUT22()
        {
            List<int> list;
            List<int> list1;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            int[] ints1 = new int[7];
            ints1[0] = -715828223;
            ints1[1] = 1700090882;
            ints1[2] = -715828223;
            ints1[3] = 1427444738;
            ints1[4] = 1159009280;
            ints1[5] = -2147483647;
            ints1[6] = int.MinValue;
            list1 = new List<int>((IEnumerable<int>)ints1);
            this.InsertionRotateRightLeftPUT(list1, list, list);
            PexAssert.IsNotNull((object)list1);
            PexAssert.AreEqual<int>(7, list1.Capacity);
            PexAssert.AreEqual<int>(7, list1.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.AreEqual<int>(0, list.Capacity);
            PexAssert.AreEqual<int>(0, list.Count);
            PexAssert.IsNotNull((object)list);
            PexAssert.IsTrue(object.ReferenceEquals((object)list, (object)list));
        }
    }
}
