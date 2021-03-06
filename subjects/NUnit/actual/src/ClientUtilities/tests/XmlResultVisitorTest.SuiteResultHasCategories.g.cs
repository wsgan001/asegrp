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
        public void SuiteResultHasCategories01()
        {
            this.SuiteResultHasCategories();
        }

        [TestMethod]
        [PexRaisedException(typeof(AssertionException))]
        [PexGeneratedBy(typeof(XmlResultVisitorTest))]
        public void SuiteResultHasCategories02()
        {
            this.SuiteResultHasCategories();
        }

    }
}
