// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using System.Collections.Generic;
using Dsa.PUTs.DataStructures;
using Microsoft.Pex.Framework.Exceptions;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    public partial class HeapTest
    {
        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        [PexRaisedException(typeof(IndexOutOfRangeException))]
        public void RemoveItemPUT18()
        {
            List<int> list;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT19()
        {
            List<int> list;
            int[] ints = new int[0];
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT20()
        {
            List<int> list;
            int[] ints = new int[1];
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT21()
        {
            List<int> list;
            int[] ints = new int[1];
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT22()
        {
            List<int> list;
            int[] ints = new int[2];
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT23()
        {
            List<int> list;
            int[] ints = new int[2];
            ints[0] = 1;
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT24()
        {
            List<int> list;
            int[] ints = new int[3];
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT25()
        {
            List<int> list;
            int[] ints = new int[5];
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT26()
        {
            List<int> list;
            int[] ints = new int[3];
            ints[0] = 1;
            ints[2] = 2;
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT27()
        {
            List<int> list;
            int[] ints = new int[5];
            ints[0] = 1;
            ints[2] = 2;
            ints[3] = 2;
            ints[4] = 2;
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void RemoveItemPUT28()
        {
            List<int> list;
            int[] ints = new int[3];
            ints[0] = 2;
            ints[1] = 1;
            ints[2] = int.MinValue;
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT29()
        {
            List<int> list;
            int[] ints = new int[4];
            ints[0] = int.MinValue;
            ints[1] = 1;
            ints[3] = 134217728;
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, int.MinValue);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT30()
        {
            List<int> list;
            int[] ints = new int[6];
            ints[0] = 2;
            ints[2] = 262144;
            ints[3] = 262144;
            ints[4] = 3;
            ints[5] = 262144;
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT31()
        {
            List<int> list;
            int[] ints = new int[5];
            ints[1] = 1;
            ints[2] = 524288;
            ints[3] = 2;
            ints[4] = 3;
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(HeapTest))]
        public void RemoveItemPUT32()
        {
            List<int> list;
            int[] ints = new int[9];
            list = new List<int>((IEnumerable<int>)ints);
            this.RemoveItemPUT(list, 0);
        }
    }
}
