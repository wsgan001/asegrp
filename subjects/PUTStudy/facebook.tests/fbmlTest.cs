using System.Collections.Generic;
using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for fbmlTest and is intended
	///to contain all fbmlTest Unit Tests
	///</summary>
	[TestFixture]
	public class fbmlTest
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
		///A test for setRefHandle
		///</summary>
		[Test]
		public void uploadNativeStringsTest()
		{
			var target = _api.fbml;
			var native_strings = new Dictionary<string, string> { { "text", "Do you want to add a friend?"}, {"description", "text string in a popup dialog" } };
			var expected = true;
			var actual = target.uploadNativeStrings(native_strings);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for setRefHandle
		///</summary>
		[Test]
		public void setRefHandleTest()
		{
			var target = _api.fbml;
			var handle = "test";
			var fbml = "test";
			var expected = true;
			var actual = target.setRefHandle(handle, fbml);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for refreshRefUrl
		///</summary>
		[Test]
		public void refreshRefUrlTest()
		{
			var target = _api.fbml;
			var url = "http://facebook.claritycon.com/FBML/FBML.html";
			var expected = true;
			var actual = target.refreshRefUrl(url);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for refreshImgSrc
		///</summary>
		[Test]
		public void refreshImgSrcTest()
		{
			var target = _api.fbml;
			var url = "http://facebook.claritycon.com/Images/Clarity.jpg";
			var expected = false;
			var actual = target.refreshImgSrc(url);
			Assert.AreEqual(expected, actual);
		}
	}
}