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
        public void HasSingleProperty01()
        {
            this.HasSingleProperty();
        }

        [TestMethod]
        [PexRaisedException(typeof(AssertionException))]
        [PexGeneratedBy(typeof(XmlResultVisitorTest))]
        public void HasSingleProperty02()
        {
            this.HasSingleProperty();
        }

    }
}
