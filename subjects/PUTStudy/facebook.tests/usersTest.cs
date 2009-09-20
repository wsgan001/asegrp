using facebook.Types;
using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for usersTest and is intended
	///to contain all usersTest Unit Tests
	///</summary>
	[TestFixture]
	public class usersTest
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
		///A test for isAppUser
		///</summary>
		[Test]
		public void isAppUserTest()
		{
			var target = _api.users;
			var uid = Constants.FBSamples_UserId;
			var expected = true;
			var actual = target.isAppUser(uid);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for setStatus
		///</summary>
		[Test]
		public void setStatusTest1()
		{
            var target = _api.users;
            var status = "testing setStatus API calls";
			var expected = true;
			var actual = target.setStatus(status);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for setStatus
		///</summary>
		[Test]
		public void setStatusTest()
		{
            var target = _api.users;
            var status = "just called setStatus from the test harness";
			var status_includes_verb = true;
			var expected = true;
			var actual = target.setStatus(status, status_includes_verb);
			Assert.AreEqual(expected, actual);
		}


		/// <summary>
		///A test for hasAppPermission
		///</summary>
		[Test]
		public void hasAppPermissionTest()
		{
            var target = _api.users;
            var ext_perm = Enums.Extended_Permissions.create_listing;
			var expected = true;
			var actual = target.hasAppPermission(ext_perm);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for getLoggedInUser
		///</summary>
		[Test]
		public void getLoggedInUserTest()
		{
            var target = _api.users;
            var expected = Constants.FBSamples_UserId;
			var actual = target.getLoggedInUser();
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for getInfo
		///</summary>
		[Test]
		public void getInfoTest3()
		{
            var target = _api.users;
            var uid = Constants.FBSamples_UserId;
			var actual = target.getInfo(uid);
			Assert.IsNotNull(actual);
			Assert.IsTrue(actual.name.Equals(Constants.FBSamples_Name));
		}

		/// <summary>
		///A test for getInfo
		///</summary>
		[Test]
		public void getInfoTest2()
		{
            var target = _api.users;
            var actual = target.getInfo();
			Assert.IsNotNull(actual);
			Assert.IsTrue(actual.name.Equals(Constants.FBSamples_Name));
		}

		/// <summary>
		///A test for getInfo
		///</summary>
		[Test]
		public void getInfoTest1()
		{
            var target = _api.users;
            var uids = Constants.FBSamples_UserId.ToString();
			var actual = target.getInfo(uids);
			Assert.IsNotNull(actual);
			Assert.IsTrue(actual.Count == 1);
			Assert.IsTrue(actual[0].name.Equals(Constants.FBSamples_Name));
		}

		/// <summary>
		///A test for getInfo
		///</summary>
		[Test]
		public void getInfoTest()
		{
            var target = _api.users;
            var userIds = Constants.FBSamples_uids;
			var actual = target.getInfo(userIds);
			Assert.IsNotNull(actual);
			Assert.IsTrue(actual.Count > 1);
			Assert.IsTrue(actual[0].name.Equals(Constants.FBSamples_Name));
		}
	}
}