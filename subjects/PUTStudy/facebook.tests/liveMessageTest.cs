using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for liveMessageTest and is intended
	///to contain all liveMessageTest Unit Tests
	///</summary>
	[TestFixture]
	public class liveMessageTest
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
		///A test for send
		///</summary>
		[Test]
		public void sendTest()
		{
			var parent = _api;
			var target = new liveMessage(parent);
			var uid = Constants.FBSamples_UserId;
			var eventName = "send";
			var message = "Testing from the test harness.";
			var expected = true;
			var actual = target.send(uid, eventName, message);
			Assert.AreEqual(expected, actual);
		}
	}
}