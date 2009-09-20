using System.Collections.Generic;
using System.Collections.ObjectModel;
using facebook.Schema;
using NUnit.Framework;
using facebook;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for feedTest and is intended
	///to contain all feedTest Unit Tests
	///</summary>
	[TestFixture]
	public class feedTest
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
		///A test for publishTemplatizedAction
		///</summary>
		[Test]
		public void publishTemplatizedActionTest1()
		{
			var parent = _api;
			var target = parent.feed;
			var title_template = "{actor} reviewed the application {app}";
			var title_data = new Dictionary<string, string> {{"app", "Facebook Developer Toolkit"}};
			var body_template = "{app} has received a rating of {num_stars} from the users of BookApplication";
			var body_data = new Dictionary<string, string> {{"app", "Facebook Developer Toolkit"}, {"num_stars", "5"}};
			var body_general = "<fb:name uid{{=}}\"620749458\" firstnameonly{{=}}true /> said \"This app rocks.\"";
			var page_actor_id = 0;
			Collection<feed_image> images = null;
			Collection<string> target_ids = null;
			var expected = true;
			var actual = target.publishTemplatizedAction(title_template, title_data, body_template, body_data, body_general,
			                                         page_actor_id, images, target_ids);
			Assert.AreEqual(expected, actual);
		}

		/// <summary>
		///A test for publishTemplatizedAction
		///</summary>
		[Test]
		public void publishTemplatizedActionTest()
		{
			var parent = _api;
            var target = parent.feed;
			var title_template = "{actor} reviewed the application {app}";
			Dictionary<string, string> title_data = new Dictionary<string, string> { { "app", "Facebook Developer Toolkit" } };
			var body_template = "{app} has received a rating of {num_stars} from the users of BookApplication";
			Dictionary<string, string> body_data = new Dictionary<string, string> { { "app", "Facebook Developer Toolkit" }, { "num_stars", "5" } };
			Collection<feed_image> images = null;
			var expected = true;
			var actual = target.publishTemplatizedAction(title_template, title_data, body_template, body_data, images);
			Assert.AreEqual(expected, actual);
		}

        /// <summary>
        ///A test for registerTemplateBundle
        ///</summary>
        [Test]
        //[DeploymentItem("facebook.dll")]
        public void registerTemplateBundleTest()
        {
            var parent = _api;
            var target = parent.feed;
            string oneLineStoryTemplate = "{*actor*} has been playing.";
            string shortStoryTemplateTitle = "{*actor*} has been <a href='http://www.facebook.com/apps/application.php?id=xxx>Playing Poker!</a>";
            string shortStoryTemplateBody = "short story body";
            string fullStoryTemplateTitle = "{*actor*} has been <a href='http://www.facebook.com/apps/application.php?id=xxx>Playing Poker!</a>";
            string fullStoryTemplateBody = "full story body";
            template_bundle expected;
            long actual;
            List<string> oneLineTemplates = new List<string> { oneLineStoryTemplate };
            feedTemplate shortStoryTemplate = new feedTemplate { PreferredLayout = "1", TemplateBody = shortStoryTemplateBody, TemplateTitle = shortStoryTemplateTitle };
            List<feedTemplate> shortStoryTemplates = new List<feedTemplate> { shortStoryTemplate };
            feedTemplate fullStoryTemplate = new feedTemplate { PreferredLayout = "1", TemplateBody = fullStoryTemplateBody, TemplateTitle = fullStoryTemplateTitle };

            actual = target.registerTemplateBundle(oneLineTemplates, shortStoryTemplates, fullStoryTemplate);
            expected = target.getRegisteredTemplateBundleByID(actual);
            Assert.AreEqual(expected.template_bundle_id, actual);
        }
	}
}