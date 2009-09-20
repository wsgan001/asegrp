using System.Collections.Generic;
using facebook.Schema;
using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for friendsTest and is intended
	///to contain all friendsTest Unit Tests
	///</summary>
	[TestFixture]
	public class friendsTest
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
		///A test for getUserObjects
		///</summary>
		[Test]
		public void getUserObjectsTest1()
		{
			var target = _api.friends;
			var flid = Constants.FBSamples_flid;
			var actual = target.getUserObjects(flid);
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0].name);
		}

		/// <summary>
		///A test for getUserObjects
		///</summary>
		[Test]
		public void getUserObjectsTest()
		{
            var target = _api.friends;
			var actual = target.getUserObjects();
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0].name);
		}

		/// <summary>
		///A test for getLists
		///</summary>
		[Test]
		public void getListsTest()
		{
            var target = _api.friends;
			var actual = target.getLists();
			Assert.IsTrue(actual.Count > 0);
			Assert.AreEqual(actual[0].flid, Constants.FBSamples_flid);
		}

		/// <summary>
		///A test for getAppUsersObjects
		///</summary>
		[Test]
		public void getAppUsersObjectsTest()
		{
            var target = _api.friends;
			var actual = target.getAppUsersObjects();
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0].name);
		}

		/// <summary>
		///A test for getAppUsers
		///</summary>
		[Test]
		public void getAppUsersTest()
		{
            var target = _api.friends;
			var actual = target.getAppUsers();
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest1()
		{
            var target = _api.friends;
			var flid = Constants.FBSamples_flid;
			var actual = target.get(flid);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest()
		{
            var target = _api.friends;
			var actual = target.get();
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for areFriends
		///</summary>
		[Test]
		public void areFriendsTest3()
		{
            var target = _api.friends;
			var uid1 = Constants.FBSamples_friend1;
			var uid2 = Constants.FBSamples_friend2;
			var actual = target.areFriends(uid1, uid2);
			Assert.IsTrue(actual[0].are_friends.Value);
		}

		/// <summary>
		///A test for areFriends
		///</summary>
		[Test]
		public void areFriendsTest2()
		{
            var target = _api.friends;
            var users1 = new List<user>();
			var users2 = new List<user>();
			users1.Add(new user {uid = Constants.FBSamples_friend1});
			users2.Add(new user {uid = Constants.FBSamples_friend2});

			var actual = target.areFriends(users1, users2);
			Assert.IsTrue(actual[0].are_friends.Value);
		}

		/// <summary>
		///A test for areFriends
		///</summary>
		[Test]
		public void areFriendsTest1()
		{
            var target = _api.friends;
            var user1 = new user { uid = Constants.FBSamples_friend1 };
			var user2 = new user {uid = Constants.FBSamples_friend2};
			var actual = target.areFriends(user1, user2);
			Assert.IsTrue(actual[0].are_friends.Value);
		}

		/// <summary>
		///A test for areFriends
		///</summary>
		[Test]
		public void areFriendsTest()
		{
            var target = _api.friends;
            var uids1 = new List<long> { Constants.FBSamples_friend1 };
			var uids2 = new List<long> {Constants.FBSamples_friend2};
			var actual = target.areFriends(uids1, uids2);
			Assert.IsTrue(actual[0].are_friends.Value);
		}
	}
}