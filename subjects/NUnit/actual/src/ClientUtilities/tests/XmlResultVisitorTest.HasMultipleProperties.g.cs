using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;

namespace NUnit.Util.Tests
{
    public partial class XmlResultVisitorTest
    {
        [TestMethod]
        [PexRaisedException(typeof(AssertionException))]
        [PexGeneratedBy(typeof(XmlResultVisitorTest))]
        public void HasMultipleProperties01()
        {
            this.HasMultipleProperties();
        }

        [TestMethod]
        [PexRaisedException(typeof(AssertionException))]
        [PexGeneratedBy(typeof(XmlResultVisitorTest))]
        public void HasMultipleProperties02()
        {
            this.HasMultipleProperties();
        }

    }
}
