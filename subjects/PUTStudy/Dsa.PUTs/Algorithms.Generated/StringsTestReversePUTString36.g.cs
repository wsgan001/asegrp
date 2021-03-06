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
    public partial class StringsTest
    {
        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ReversePUT06()
        {
            this.ReversePUT((string)null);
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void ReversePUT07()
        {
            this.ReversePUT("");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void ReversePUT08()
        {
            this.ReversePUT("\n");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void ReversePUT09()
        {
            this.ReversePUT("a");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void ReversePUT10()
        {
            this.ReversePUT("a\n");
        }
    }
}
