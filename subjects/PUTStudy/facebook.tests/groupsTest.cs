using System.Collections.Generic;
using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for groupsTest and is intended
	///to contain all groupsTest Unit Tests
	///</summary>
	[TestFixture]
	public class groupsTest
	{
		private API _api;

		/// <summary>
		///Gets or sets the test context which provides
		///information about and functionality for the current test run.
		///</summary>
		

		[SetUp]
		public void Initialize()
		{
			_api = new API
			       	{
			       		IsDesktopApplication = true,
			       		ApplicationKey = Constants.FBSamples_ApplicationKey,
			       		Secret = Constants.FBSamples_Secret,
			       		SessionKey = Constants.FBSamples_SessionKey,
			       		uid = Constants.FBSamples_UserId
			       	};
		}

		/// <summary>
		///A test for getMembers
		///</summary>
		[Test]
		public void getMembersTest()
		{
			var target = _api.groups;
			var gid = Constants.FBSamples_gid;
			var actual = target.getMembers(gid);
			Assert.IsTrue(actual.members.uid.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest3()
		{
            var target = _api.groups;
            var uid = _api.uid;
			var actual = target.get(uid);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest2()
		{
            var target = _api.groups;
            var actual = target.get();
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest1()
		{
            var target = _api.groups;
            var uid = _api.uid;
			var gids = new List<long> {Constants.FBSamples_gid};
			var actual = target.get(uid, gids);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest()
		{
            var target = _api.groups;
            var gids = new List<long> { Constants.FBSamples_gid };
			var actual = target.get(gids);
			Assert.IsTrue(actual.Count > 0);
		}
	}
}