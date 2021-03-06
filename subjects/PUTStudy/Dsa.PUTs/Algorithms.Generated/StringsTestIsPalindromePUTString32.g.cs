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
        public void IsPalindromePUT10()
        {
            this.IsPalindromePUT((string)null);
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        [PexRaisedException(typeof(IndexOutOfRangeException))]
        public void IsPalindromePUT11()
        {
            this.IsPalindromePUT("");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        [PexRaisedException(typeof(IndexOutOfRangeException))]
        public void IsPalindromePUT12()
        {
            this.IsPalindromePUT("\n");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void IsPalindromePUT13()
        {
            this.IsPalindromePUT("r");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void IsPalindromePUT14()
        {
            this.IsPalindromePUT("cz");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void IsPalindromePUT15()
        {
            this.IsPalindromePUT("zz");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void IsPalindromePUT16()
        {
            this.IsPalindromePUT("dddd");
        }

        [Test]
        [PexGeneratedBy(typeof(StringsTest), IsCustomInput = true)]
        public void IsPalindromePUT17()
        {
            this.IsPalindromePUT("aaa\n");
        }
    }
}
