// <copyright file="CollectionFactory.cs" company="">Copyright ©  2009</copyright>

using System;
using Microsoft.Pex.Framework;
using System.Collections.ObjectModel;

namespace System.Collections.ObjectModel
{
    /// <summary>A factory for System.Collections.ObjectModel.Collection`1[System.Int32] instances</summary>
    public static partial class CollectionFactory
    {
        /// <summary>A factory for System.Collections.ObjectModel.Collection`1[System.Int32] instances</summary>
        [PexFactoryMethod(typeof(Collection<int>))]
        public static Collection<int> Create([PexAssumeNotNull]int[] count)
        {
            Collection<int> collection = new Collection<int>();
            foreach (int c in count)
                collection.Add(c);
            return collection;

        }
    }
}
