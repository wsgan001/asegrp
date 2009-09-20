using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for notificationsTest and is intended
	///to contain all notificationsTest Unit Tests
	///</summary>
	[TestFixture]
	public class notificationsTest
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
		///A test for sendEmail
		///</summary>
		[Test]
		public void sendEmailTest()
		{
            var target = _api.notifications;
            var recipients = Constants.FBSamples_UserId.ToString();
			var subject = "test SendEmail API";
			var text = "This is a test email for the notifications.SendEmail Unit Test";
			string fbml = null;
			var actual = target.sendEmail(recipients, subject, text, fbml);
			Assert.IsNotNull(actual);
		}

		/// <summary>
		///A test for send
		///</summary>
		[Test]
		public void sendTest()
		{
            var target = _api.notifications;
            var to_ids = Constants.FBSamples_UserId.ToString();
			var notification = "test";
			var actual = target.send(to_ids, notification);
			Assert.IsNotNull(actual);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest()
		{
            var target = _api.notifications;
            var actual = target.get();
			Assert.IsNotNull(actual);
		}
	}
}