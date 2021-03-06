// This file contains automatically generated unit tests.
// Do NOT modify this file manually.
// 
// When Pex is invoked again,
// it might remove or update any previously generated unit tests.
// 
// If the contents of this file becomes outdated, e.g. if it does not
// compile anymore, you may delete this file and invoke Pex again.
using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework.Generated;

namespace AutomatonToCode
{
    public partial class sampletest2
    {
        [TestMethod]
        [PexGeneratedBy(typeof(sampletest2))]
        public void Method101()
        {
            bool b;
            b = this.Method1((string)null);
            Assert.AreEqual<bool>(false, b);
        }

        [TestMethod]
        [PexGeneratedBy(typeof(sampletest2))]
        public void Method102()
        {
            bool b;
            b = this.Method1("");
            Assert.AreEqual<bool>(false, b);
        }

        [TestMethod]
        [PexGeneratedBy(typeof(sampletest2))]
        public void Method103()
        {
            bool b;
            b = this.Method1("\0");
            Assert.AreEqual<bool>(false, b);
        }

        [TestMethod]
        [PexGeneratedBy(typeof(sampletest2))]
        public void Method104()
        {
            bool b;
            b = this.Method1("O");
            Assert.AreEqual<bool>(false, b);
        }

        [TestMethod]
        [PexGeneratedBy(typeof(sampletest2))]
        public void Method105()
        {
            bool b;
            b = this.Method1("O\0\0\0\0\0");
            Assert.AreEqual<bool>(false, b);
        }

        [TestMethod]
        [PexGeneratedBy(typeof(sampletest2))]
        public void Method106()
        {
            bool b;
            b = this.Method1("P\u00f4\u00f4\u00f4\u00f4\u00f4");
            Assert.AreEqual<bool>(true, b);
        }

        [TestMethod]
        [PexGeneratedBy(typeof(sampletest2))]
        public void Method107()
        {
            bool b;
            b = this.Method1(
            "P\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4"
            );
            Assert.AreEqual<bool>(false, b);
        }

    }
}
