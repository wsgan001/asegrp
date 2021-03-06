// <copyright file="HashtableFactory.cs" company=""></copyright>

using System;
using Microsoft.Pex.Framework;
using System.Collections;

namespace System.Collections
{
    /// <summary>A factory for System.Collections.Hashtable instances</summary>
    public static partial class HashtableFactory
    {
        /// <summary>A factory for System.Collections.Hashtable instances</summary>
        [PexFactoryMethod(typeof(Hashtable))]
        public static Hashtable Create()
        {
            Hashtable hashtable = new Hashtable();
            return hashtable;

            // TODO: Edit factory method of Hashtable
            // This method should be able to configure the object in all possible ways.
            // Add as many parameters as needed,
            // and assign their values to each field by using the API.
        }
    }
}
