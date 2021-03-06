// <copyright file="RandomFactory.cs" company=""></copyright>

using System;
using Microsoft.Pex.Framework;

namespace System
{
    /// <summary>A factory for System.Random instances</summary>
    public static partial class RandomFactory
    {
        /// <summary>A factory for System.Random instances</summary>
        [PexFactoryMethod(typeof(Random))]
        public static Random Create(int Seed_i)
        {
            Random random = new Random(Seed_i);
            //random.NextBytes(buffer_bs);
            return random;
        }
    }
}
