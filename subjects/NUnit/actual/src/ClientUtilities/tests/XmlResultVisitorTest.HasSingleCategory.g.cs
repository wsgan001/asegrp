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
        public void HasSingleCategory01()
        {
            this.HasSingleCategory();
        }

        [TestMethod]
        [PexRaisedException(typeof(AssertionException))]
        [PexGeneratedBy(typeof(XmlResultVisitorTest))]
        public void HasSingleCategory02()
        {
            this.HasSingleCategory();
        }

    }
}
