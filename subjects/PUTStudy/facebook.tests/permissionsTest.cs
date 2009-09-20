using System.Collections.Generic;
using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for permissionsTest and is intended
	///to contain all permissionsTest Unit Tests
	///</summary>
	[TestFixture]
	public class permissionsTest
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
			       		IsDesktopApplication = false,
			       		ApplicationKey = Constants.FBSamples_WebApplicationKey,
			       		Secret = Constants.FBSamples_WebSecret
			       	};
		}

		/// <summary>
		///A test for revokeApiAccess
		///</summary>
		[Test]
		public void revokeApiAccessTest()
		{
            var target = _api.permissions;
            var api = Constants.FBSamples_WebApplicationKey2;
			var expected = true;
			var actual = target.revokeApiAccess(api);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for grantApiAccess
		///</summary>
		[Test]
		public void grantApiAccessTest()
		{
			var target = _api.permissions;
			var api = Constants.FBSamples_WebApplicationKey2;
			var method_arr = new List<string> {"admin"};
			var expected = false;
			var actual = target.grantApiAccess(api, method_arr);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for checkGrantedApiAccess
		///</summary>
		[Test]
		public void checkGrantedApiAccessTest()
		{
			var target = _api.permissions;
			var api = Constants.FBSamples_WebApplicationKey2;
			var actual = target.checkGrantedApiAccess(api);
			Assert.IsTrue(actual.Count == 0);
		}

		/// <summary>
		///A test for checkAvailableApiAccess
		///</summary>
		[Test]
		public void checkAvailableApiAccessTest()
		{
            var target = _api.permissions;
            var api = Constants.FBSamples_WebApplicationKey2;
			var actual = target.checkAvailableApiAccess(api);
			Assert.IsTrue(actual.Count == 0);
		}

		///// <summary>
		/////A test for checkAvailableApiAccess
		/////</summary>
		//[Test]
		//public void permissionsModeTest()
		//{
		//    API parent = _api;
		//    permissions target = new permissions(parent);
		//    string permissions_apikey = string.Empty; // TODO: Initialize to an appropriate value
		//    IList<string> expected = null; // TODO: Initialize to an appropriate value
		//    IList<string> actual;
		//    actual = target.checkAvailableApiAccess(permissions_apikey);
		//    Assert.AreEqual(expected, actual);

		//}
	}
}