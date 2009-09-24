// <copyright file="SinglyLinkedListFactory.cs" company="Microsoft">Copyright © Microsoft 2007</copyright>

using System;
using Microsoft.Pex.Framework;
using Dsa.DataStructures;

namespace Dsa.DataStructures
{
    /// <summary>A factory for Dsa.DataStructures.SinglyLinkedList`1[System.Int32] instances</summary>
    public static partial class SinglyLinkedListFactory
    {
        /// <summary>A factory for Dsa.DataStructures.SinglyLinkedList`1[System.Int32] instances</summary>
        [PexFactoryMethod(typeof(SinglyLinkedList<int>))]
        public static SinglyLinkedList<int> Create(
            SinglyLinkedListNode<int> node_singlyLinkedListNode,
            int[] elements
        )
        {
            PexAssume.IsNotNull(elements);
            SinglyLinkedList<int> singlyLinkedList = new SinglyLinkedList<int>(elements);            
            return singlyLinkedList;
        }
    }
}
