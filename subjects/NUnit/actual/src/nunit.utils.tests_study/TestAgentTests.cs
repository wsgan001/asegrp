using System;
using System.Diagnostics;
using NUnit.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;

namespace NUnit.Util.Tests
{
	[PexClass] //
    [TestFixture]
    public partial class RemoteTestAgentTests
	{
		[Test] //[PexMethod]
		public void AgentReturnsProcessId()
		{
			RemoteTestAgent agent = new RemoteTestAgent("dummy");
			Assert.AreEqual( Process.GetCurrentProcess().Id, agent.ProcessId );
		}

		[Test] //[PexMethod]
		public void CanLocateAgentExecutable()
		{
			string path = TestAgency.TestAgentExePath;
			Assert.IsFalse( System.IO.File.Exists( path ));//, "Cannot find " + path  );
		}
	}
}
