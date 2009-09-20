using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for fqlTest and is intended
	///to contain all fqlTest Unit Tests
	///</summary>
	[TestFixture]
	public class fqlTest
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
		///A test for query
		///</summary>
		[Test]
		public void queryTest()
		{
			var parent = _api;
			var target = _api.fql;
			var query = "SELECT uid, name FROM user WHERE uid IN (" + Constants.FBSamples_UserId + ")";
			var actual = target.query(query);
			Assert.IsNotNull(actual);
		}
	}
}