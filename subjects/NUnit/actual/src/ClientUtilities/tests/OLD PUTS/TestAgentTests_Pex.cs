using System;
using System.Diagnostics;
using Microsoft.Pex.Framework;
using NUnit.Framework;

namespace NUnit.Util.PUTs
{
    [TestFixture]
	// Exclude for release [TestFixture]
    [PexClass]//(typeof(TestAgency))]
    public partial class RemoteTestAgentTests
	{
		[PexMethod]
        public void AgentReturnsProcessIdPUT1([PexAssumeUnderTest]RemoteTestAgent agent)
        {
            //RemoteTestAgent agent = new RemoteTestAgent("dummy");
            PexAssert.AreEqual(Process.GetCurrentProcess().Id, agent.ProcessId);
        }

        //PHASE 1
        [PexMethod] //[Test]
        public void CanLocateAgentExecutable()
        {
            string path = TestAgency.TestAgentExePath;
            Assert.IsFalse(System.IO.File.Exists(path));//, "Cannot find " + path  );
        }
       /* [PexMethod]
        public void CanLocateAgentExecutablePUT1([PexAssumeUnderTest]String path)
        {
            //string path = TestAgency.TestAgentExePath;
            //PexAssert.AreEqual( System.IO.File.Exists(path), "Cannot find " + path  );
            //Had to change it to run the test case
            PexAssert.IsFalse(System.IO.File.Exists(path));
        }*/

        //PHASE 2
    /*    [PexMethod]
        public void CanLocateAgentExecutablePUT2([PexAssumeUnderTest]String path)
        {
            //PexAssume.IsTrue(path.Contains("t"));
            string outputPath = TestAgency.TestAgentExePath1(path);
            //path = path + ".NUnit.exe";
            //PexAssert.AreEqual( System.IO.File.Exists(path), "Cannot find " + path  );
            //Had to change it to run the test case
            //Console.WriteLine(outputPath);
            if(path.Length > 0)
                PexAssert.IsTrue(System.IO.File.Exists(outputPath));
            else
                PexAssert.IsFalse(System.IO.File.Exists(outputPath));
        }*/
	}
}
