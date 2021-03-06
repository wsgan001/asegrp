// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using Dsa.DataStructures;
using Dsa.PUTs.DataStructures;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    public partial class SinglyLinkedListTest
    {
        [Test]
        [PexGeneratedBy(typeof(SinglyLinkedListTest))]
        public void AddFirstTest05()
        {
            SinglyLinkedList<int> singlyLinkedList;
            int[] ints = new int[0];
            singlyLinkedList =
              SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
            int[] ints1 = new int[2];
            this.AddFirstTest(singlyLinkedList, ints1);
            PexAssert.IsNotNull((object)singlyLinkedList);
            PexAssert.IsNotNull(singlyLinkedList.Head);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Next.Value);
            PexAssert.IsNull(singlyLinkedList.Head.Next.Next);
            PexAssert.IsNotNull(singlyLinkedList.Tail);
            PexAssert.IsTrue
                (object.ReferenceEquals(singlyLinkedList.Tail, singlyLinkedList.Tail.Next));
            PexAssert.AreEqual<bool>
                (false, ((CollectionBase<int>)singlyLinkedList).IsSynchronized);
            PexAssert.AreEqual<int>(2, ((CollectionBase<int>)singlyLinkedList).Count);
        }

        [Test]
        [PexGeneratedBy(typeof(SinglyLinkedListTest))]
        public void AddFirstTest06()
        {
            SinglyLinkedList<int> singlyLinkedList;
            int[] ints = new int[0];
            singlyLinkedList =
              SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
            int[] ints1 = new int[2];
            ints1[0] = 1;
            this.AddFirstTest(singlyLinkedList, ints1);
            PexAssert.IsNotNull((object)singlyLinkedList);
            PexAssert.IsNotNull(singlyLinkedList.Head);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next);
            PexAssert.AreEqual<int>(1, singlyLinkedList.Head.Next.Value);
            PexAssert.IsNull(singlyLinkedList.Head.Next.Next);
            PexAssert.IsNotNull(singlyLinkedList.Tail);
            PexAssert.IsTrue
                (object.ReferenceEquals(singlyLinkedList.Tail, singlyLinkedList.Tail.Next));
            PexAssert.AreEqual<bool>
                (false, ((CollectionBase<int>)singlyLinkedList).IsSynchronized);
            PexAssert.AreEqual<int>(2, ((CollectionBase<int>)singlyLinkedList).Count);
        }

        [Test]
        [PexGeneratedBy(typeof(SinglyLinkedListTest))]
        public void AddFirstTest07()
        {
            SinglyLinkedList<int> singlyLinkedList;
            int[] ints = new int[0];
            singlyLinkedList =
              SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
            int[] ints1 = new int[3];
            this.AddFirstTest(singlyLinkedList, ints1);
            PexAssert.IsNotNull((object)singlyLinkedList);
            PexAssert.IsNotNull(singlyLinkedList.Head);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Next.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next.Next);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Next.Next.Value);
            PexAssert.IsNull(singlyLinkedList.Head.Next.Next.Next);
            PexAssert.IsNotNull(singlyLinkedList.Tail);
            PexAssert.IsTrue(object.ReferenceEquals
                (singlyLinkedList.Tail, singlyLinkedList.Tail.Next.Next));
            PexAssert.AreEqual<bool>
                (false, ((CollectionBase<int>)singlyLinkedList).IsSynchronized);
            PexAssert.AreEqual<int>(3, ((CollectionBase<int>)singlyLinkedList).Count);
        }

        [Test]
        [PexGeneratedBy(typeof(SinglyLinkedListTest))]
        public void AddFirstTest08()
        {
            SinglyLinkedList<int> singlyLinkedList;
            int[] ints = new int[0];
            singlyLinkedList =
              SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
            int[] ints1 = new int[3];
            ints1[0] = 1;
            this.AddFirstTest(singlyLinkedList, ints1);
            PexAssert.IsNotNull((object)singlyLinkedList);
            PexAssert.IsNotNull(singlyLinkedList.Head);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Next.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next.Next);
            PexAssert.AreEqual<int>(1, singlyLinkedList.Head.Next.Next.Value);
            PexAssert.IsNull(singlyLinkedList.Head.Next.Next.Next);
            PexAssert.IsNotNull(singlyLinkedList.Tail);
            PexAssert.IsTrue(object.ReferenceEquals
                (singlyLinkedList.Tail, singlyLinkedList.Tail.Next.Next));
            PexAssert.AreEqual<bool>
                (false, ((CollectionBase<int>)singlyLinkedList).IsSynchronized);
            PexAssert.AreEqual<int>(3, ((CollectionBase<int>)singlyLinkedList).Count);
        }
    }
}
