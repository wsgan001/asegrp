// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using Dsa.PUTs.DataStructures;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    public partial class DequeTest
    {
        [Test]
        [PexGeneratedBy(typeof(DequeTest), IsCustomInput = true)]
        [ExpectedException(typeof(NotSupportedException))]
        public void RemovePUT02()
        {
            this.RemovePUT(0);
        }
    }
}
