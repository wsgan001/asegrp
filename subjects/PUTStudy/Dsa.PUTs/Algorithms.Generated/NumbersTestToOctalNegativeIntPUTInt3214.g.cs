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
    public partial class NumbersTest
    {
        [Test]
        [PexGeneratedBy(typeof(NumbersTest), IsCustomInput = true)]
        [PexRaisedException(typeof(FormatException))]
        public void ToOctalNegativeIntPUT04()
        {
            this.ToOctalNegativeIntPUT(0);
        }

        [Test]
        [PexGeneratedBy(typeof(NumbersTest), IsCustomInput = true)]
        public void ToOctalNegativeIntPUT05()
        {
            this.ToOctalNegativeIntPUT(1);
        }

        [Test]
        [PexGeneratedBy(typeof(NumbersTest), IsCustomInput = true)]
        public void ToOctalNegativeIntPUT06()
        {
            this.ToOctalNegativeIntPUT(int.MinValue);
        }
    }
}
