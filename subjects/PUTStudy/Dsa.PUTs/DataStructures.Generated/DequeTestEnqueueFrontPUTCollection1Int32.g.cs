// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System.Collections.ObjectModel;
using Dsa.PUTs.DataStructures;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    public partial class DequeTest
    {
        [Test]
        [PexGeneratedBy(typeof(DequeTest))]
        public void EnqueueFrontPUT06()
        {
            Collection<int> collection;
            int[] ints = new int[0];
            collection = CollectionFactory.Create(ints);
            this.EnqueueFrontPUT(collection, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(DequeTest))]
        public void EnqueueFrontPUT07()
        {
            Collection<int> collection;
            int[] ints = new int[1];
            collection = CollectionFactory.Create(ints);
            this.EnqueueFrontPUT(collection, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(DequeTest))]
        public void EnqueueFrontPUT08()
        {
            Collection<int> collection;
            int[] ints = new int[2];
            collection = CollectionFactory.Create(ints);
            this.EnqueueFrontPUT(collection, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(DequeTest))]
        public void EnqueueFrontPUT09()
        {
            Collection<int> collection;
            int[] ints = new int[3];
            collection = CollectionFactory.Create(ints);
            this.EnqueueFrontPUT(collection, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(DequeTest))]
        public void EnqueueFrontPUT10()
        {
            Collection<int> collection;
            int[] ints = new int[5];
            collection = CollectionFactory.Create(ints);
            this.EnqueueFrontPUT(collection, 0);
        }
    }
}
