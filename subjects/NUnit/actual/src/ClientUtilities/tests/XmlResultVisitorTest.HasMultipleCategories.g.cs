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
        public void HasMultipleCategories01()
        {
            this.HasMultipleCategories();
        }

        [TestMethod]
        [PexRaisedException(typeof(AssertionException))]
        [PexGeneratedBy(typeof(XmlResultVisitorTest))]
        public void HasMultipleCategories02()
        {
            this.HasMultipleCategories();
        }

    }
}
