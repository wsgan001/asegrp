using System.Collections.Generic;
using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for pagesTest and is intended
	///to contain all pagesTest Unit Tests
	///</summary>
	[TestFixture]
	public class pagesTest
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
		///A test for isFan
		///</summary>
		[Test]
		public void isFanTest1()
		{
            var target = _api.pages;
            var page_id = Constants.FBSamples_page;
			var expected = true;
			var actual = target.isFan(page_id);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for isFan
		///</summary>
		[Test]
		public void isFanTest()
		{
            var target = _api.pages;
            var page_id = Constants.FBSamples_page;
			var uid = Constants.FBSamples_UserId;
			var expected = true;
			var actual = target.isFan(page_id, uid);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for isAppAdded
		///</summary>
		[Test]
		public void isAppAddedTest()
		{
            var target = _api.pages;
            var page_id = Constants.FBSamples_page;
			var expected = false;
			var actual = target.isAppAdded(page_id);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for isAdmin
		///</summary>
		[Test]
		public void isAdminTest()
		{
            var target = _api.pages;
            var page_id = Constants.FBSamples_page;
			var expected = true;
			var actual = target.isAdmin(page_id);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for getInfo
		///</summary>
		[Test]
		public void getInfoTest()
		{
            var target = _api.pages;
            var fields = _api.pages.GetFields();
			var page_ids = new List<long> {Constants.FBSamples_page};
			int? uid = null;
			var actual = target.getInfo(fields, page_ids, uid);
			Assert.IsNotNull(actual);
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0].name);
		}

		[Test]
		public void getInfoTest1()
		{
            var target = _api.pages;
            var fields = _api.pages.GetFields();
			List<long> page_ids = null;
			int? uid = null;
			var actual = target.getInfo(fields, page_ids, uid);
			Assert.IsNotNull(actual);
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0].name);
		}

		[Test]
		public void getInfoTest2()
		{
			var target = _api.pages;
			var fields = _api.pages.GetFields();
			List<long> page_ids = null;
			long? uid = Constants.FBSamples_UserId;
			var actual = target.getInfo(fields, page_ids, uid);
			Assert.IsNotNull(actual);
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0].name);
		}
	}
}