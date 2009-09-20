using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for profileTest and is intended
	///to contain all profileTest Unit Tests
	///</summary>
	[TestFixture]
	public class profileTest
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
		///A test for setFBML
		///</summary>
		[Test]
		public void setFBMLTest()
		{
			var target = _api.profile;
			var uid = Constants.FBSamples_UserId;
			var profile = Constants.FBSamples_setFBML;
			var profile_main = Constants.FBSamples_setFBML;
			var mobile_profile = Constants.FBSamples_setFBML;
			var expected = true;
			var actual = target.setFBML(uid, profile, profile_main, mobile_profile);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for getFBML
		///</summary>
		[Test]
		public void getFBMLTest()
		{
			var target = _api.profile;
			var uid = Constants.FBSamples_UserId;
			var type = 2;
			var expected = Constants.FBSamples_setFBML;
			var actual = target.getFBML(uid, type);
			Assert.IsTrue(actual.Contains(expected));
		}
	}
}