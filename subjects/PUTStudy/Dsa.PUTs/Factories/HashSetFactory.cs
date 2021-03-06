// <copyright file="HashSetFactory.cs" company="">Copyright ©  2009</copyright>

using System;
using Microsoft.Pex.Framework;
using System.Collections.Generic;

namespace System.Collections.Generic
{
    /// <summary>A factory for System.Collections.Generic.HashSet`1[System.Int32] instances</summary>
    public static partial class HashSetFactory
    {
        /// <summary>A factory for System.Collections.Generic.HashSet`1[System.Int32] instances</summary>
        [PexFactoryMethod(typeof(HashSet<int>))]
        public static HashSet<int> Create(int[] elements)
        {
            PexAssume.IsNotNull(elements);
            HashSet<int> hashSet = new HashSet<int>(elements);            
            return hashSet;           
        }
    }
}
