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
        public void AddAfterMiddleNodeTest03()
        {
            SinglyLinkedList<int> singlyLinkedList;
            int[] ints = new int[2];
            singlyLinkedList =
              SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
            this.AddAfterMiddleNodeTest(singlyLinkedList, 0);
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
        public void AddAfterMiddleNodeTest04()
        {
            SinglyLinkedList<int> singlyLinkedList;
            int[] ints = new int[3];
            singlyLinkedList =
              SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
            this.AddAfterMiddleNodeTest(singlyLinkedList, 0);
            PexAssert.IsNotNull((object)singlyLinkedList);
            PexAssert.IsNotNull(singlyLinkedList.Head);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Next.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next.Next);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Next.Next.Value);
            PexAssert.IsNotNull(singlyLinkedList.Head.Next.Next.Next);
            PexAssert.AreEqual<int>(0, singlyLinkedList.Head.Next.Next.Next.Value);
            PexAssert.IsNull(singlyLinkedList.Head.Next.Next.Next.Next);
            PexAssert.IsNotNull(singlyLinkedList.Tail);
            PexAssert.IsTrue(object.ReferenceEquals
                (singlyLinkedList.Tail, singlyLinkedList.Tail.Next.Next.Next));
            PexAssert.AreEqual<bool>
                (false, ((CollectionBase<int>)singlyLinkedList).IsSynchronized);
            PexAssert.AreEqual<int>(4, ((CollectionBase<int>)singlyLinkedList).Count);
        }
    }
}
