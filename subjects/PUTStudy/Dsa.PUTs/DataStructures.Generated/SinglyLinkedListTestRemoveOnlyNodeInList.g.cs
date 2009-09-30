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
        public void RemoveOnlyNodeInListTest02()
        {
            SinglyLinkedList<int> singlyLinkedList;
            int[] ints = new int[0];
            singlyLinkedList =
              SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
            this.RemoveOnlyNodeInListTest(singlyLinkedList, 0);
            PexAssert.IsNotNull((object)singlyLinkedList);
            PexAssert.IsNull(singlyLinkedList.Head);
            PexAssert.IsNull(singlyLinkedList.Tail);
            PexAssert.AreEqual<bool>
                (false, ((CollectionBase<int>)singlyLinkedList).IsSynchronized);
            PexAssert.AreEqual<int>(0, ((CollectionBase<int>)singlyLinkedList).Count);
        }
    }
}
