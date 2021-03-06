// <copyright file="PriorityQueueFactory.cs" company="">Copyright ©  2009</copyright>

using System;
using Microsoft.Pex.Framework;
using Dsa.DataStructures;

namespace Dsa.DataStructures
{
    /// <summary>A factory for Dsa.DataStructures.PriorityQueue`1[System.Int32] instances</summary>
    public static partial class PriorityQueueFactory
    {
        /// <summary>A factory for Dsa.DataStructures.PriorityQueue`1[System.Int32] instances</summary>
        [PexFactoryMethod(typeof(PriorityQueue<int>))]
        public static PriorityQueue<int> Create(int[] elements)
        {
            PexAssume.IsNotNull(elements);
            PriorityQueue<int> priorityQueue = new PriorityQueue<int>(elements);            
            return priorityQueue;
        }
    }
}
