using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace NUnit.Util
{
    [TestClass, PexClass]
    public partial class testSymExec
    {

        [PexMethod]
        public void testMePUT(int a, int b)
        {
            PexAssume.IsTrue(a > 0);
            Console.WriteLine(new MemorySettingsStorage().testMe(a, b));
        }
    }
}
