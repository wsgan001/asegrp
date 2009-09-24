// <copyright file="SinglyLinkedListTest.RemoveFirstTest.g.cs" company="">Copyright ©  2009</copyright>
// <auto-generated>
// This file contains automatically generated unit tests.
// Do NOT modify this file manually.
// 
// When Pex is invoked again,
// it might remove or update any previously generated unit tests.
// 
// If the contents of this file becomes outdated, e.g. if it does not
// compile anymore, you may delete this file and invoke Pex again.
// </auto-generated>
using System;
using Dsa.DataStructures;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework.Generated;
using Microsoft.Pex.Framework.Exceptions;

namespace Dsa.Test.DataStructures
{
    public partial class SinglyLinkedListTest
    {
[TestMethod]
[PexGeneratedBy(typeof(SinglyLinkedListTest))]
public void RemoveFirstTest01()
{
    SinglyLinkedList<int> singlyLinkedList;
    int[] ints = new int[0];
    singlyLinkedList =
      SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
    int[] ints1 = new int[2];
    this.RemoveFirstTest(singlyLinkedList, ints1);
    Assert.IsNotNull((object)singlyLinkedList);
    Assert.IsNotNull(singlyLinkedList.Head);
    Assert.AreEqual<int>(0, singlyLinkedList.Head.Value);
    Assert.IsNull(singlyLinkedList.Head.Next);
    Assert.IsNotNull(singlyLinkedList.Tail);
    Assert.IsTrue
        (object.ReferenceEquals(singlyLinkedList.Tail, singlyLinkedList.Tail));
    Assert.AreEqual<bool>
        (false, ((CollectionBase<int>)singlyLinkedList).IsSynchronized);
    Assert.AreEqual<int>(1, ((CollectionBase<int>)singlyLinkedList).Count);
}
[TestMethod]
[PexGeneratedBy(typeof(SinglyLinkedListTest))]
public void RemoveFirstTest02()
{
    SinglyLinkedList<int> singlyLinkedList;
    int[] ints = new int[0];
    singlyLinkedList =
      SinglyLinkedListFactory.Create((SinglyLinkedListNode<int>)null, ints);
    int[] ints1 = new int[3];
    this.RemoveFirstTest(singlyLinkedList, ints1);
    Assert.IsNotNull((object)singlyLinkedList);
    Assert.IsNotNull(singlyLinkedList.Head);
    Assert.AreEqual<int>(0, singlyLinkedList.Head.Value);
    Assert.IsNotNull(singlyLinkedList.Head.Next);
    Assert.AreEqual<int>(0, singlyLinkedList.Head.Next.Value);
    Assert.IsNull(singlyLinkedList.Head.Next.Next);
    Assert.IsNotNull(singlyLinkedList.Tail);
    Assert.IsTrue
        (object.ReferenceEquals(singlyLinkedList.Tail, singlyLinkedList.Tail.Next));
    Assert.AreEqual<bool>
        (false, ((CollectionBase<int>)singlyLinkedList).IsSynchronized);
    Assert.AreEqual<int>(2, ((CollectionBase<int>)singlyLinkedList).Count);
}
    }
}
