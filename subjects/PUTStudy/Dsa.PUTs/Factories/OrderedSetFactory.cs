// <copyright file="OrderedSetFactory.cs" company="">Copyright ©  2009</copyright>

using System;
using Microsoft.Pex.Framework;
using Dsa.DataStructures;
using Dsa.PUTs.Utility;

namespace Dsa.DataStructures
{
    /// <summary>A factory for Dsa.DataStructures.OrderedSet`1[System.Int32] instances</summary>
    public static partial class OrderedSetFactory
    {
        /// <summary>A factory for Dsa.DataStructures.OrderedSet`1[System.Int32] instances</summary>
        [PexFactoryMethod(typeof(OrderedSet<int>))]
        public static OrderedSet<int> Create(int[] item_i)
        {
            OrderedSet<int> orderedSet = new OrderedSet<int>();
            for (int i = 0; i < item_i.Length; i++)
                orderedSet.Add(item_i[i]);
            return orderedSet;
        }
        /// <summary>A factory for Dsa.DataStructures.OrderedSet`1[Dsa.Test.Utility.Person] instances</summary>
        [PexFactoryMethod(typeof(OrderedSet<Person>))]
        public static OrderedSet<Person> Create(Person item_person)
        {
            OrderedSet<Person> orderedSet = new OrderedSet<Person>();
            orderedSet.Add(item_person);
            return orderedSet;
        }
    }
}
