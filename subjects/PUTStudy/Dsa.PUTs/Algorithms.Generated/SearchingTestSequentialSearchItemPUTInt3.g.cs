// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using Dsa.PUTs.Algorithms;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;

namespace Dsa.PUTs.Algorithms
{
    public partial class SearchingTest
    {
        [Test]
        [PexGeneratedBy(typeof(SearchingTest), IsCustomInput = true)]
        [ExpectedException(typeof(ArgumentNullException))]
        public void SequentialSearchItemPUT07()
        {
            this.SequentialSearchItemPUT((int[])null, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SearchingTest), IsCustomInput = true)]
        public void SequentialSearchItemPUT08()
        {
            int[] ints = new int[0];
            this.SequentialSearchItemPUT(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SearchingTest), IsCustomInput = true)]
        public void SequentialSearchItemPUT09()
        {
            int[] ints = new int[1];
            this.SequentialSearchItemPUT(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SearchingTest), IsCustomInput = true)]
        public void SequentialSearchItemPUT10()
        {
            int[] ints = new int[1];
            ints[0] = 1;
            this.SequentialSearchItemPUT(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SearchingTest), IsCustomInput = true)]
        public void SequentialSearchItemPUT11()
        {
            int[] ints = new int[2];
            ints[0] = 1;
            this.SequentialSearchItemPUT(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SearchingTest), IsCustomInput = true)]
        public void SequentialSearchItemPUT12()
        {
            int[] ints = new int[2];
            this.SequentialSearchItemPUT(ints, 1);
        }
    }
}
