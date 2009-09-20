using System.Collections.Generic;
using System.IO; using NUnit.Framework;
using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for photosTest and is intended
	///to contain all photosTest Unit Tests
	///</summary>
	[TestFixture]
	public class photosTest
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
		///A test for upload
		///</summary>
		[Test]
		public void uploadTest()
		{
			var target = _api.photos;
			var aid = Constants.FBSamples_aid;
			var caption = "caption";
			var data = new FileInfo(@"..\..\..\facebook.tests\Data\Clarity.jpg");
			var actual = target.upload(aid, caption, data);
			Assert.IsNotNull(actual);
		}

		/// <summary>
		///A test for getTags
		///</summary>
		[Test]
		public void getTagsTest()
		{
            var target = _api.photos;
            var pids = new List<long> { Constants.FBSamples_pid };
			var actual = target.getTags(pids);
			Assert.IsNotNull(actual);
		}

		/// <summary>
		///A test for getAlbums
		///</summary>
		[Test]
		public void getAlbumsTest3()
		{
            var target = _api.photos;
            var uid = Constants.FBSamples_UserId;
			var aids = new List<long> {Constants.FBSamples_aid};
			var actual = target.getAlbums(uid, aids);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for getAlbums
		///</summary>
		[Test]
		public void getAlbumsTest2()
		{
            var target = _api.photos;
            var aids = new List<long> { Constants.FBSamples_aid };
			var actual = target.getAlbums(aids);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for getAlbums
		///</summary>
		[Test]
		public void getAlbumsTest1()
		{
            var target = _api.photos;
            var uid = Constants.FBSamples_UserId;
			var actual = target.getAlbums(uid);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for getAlbums
		///</summary>
		[Test]
		public void getAlbumsTest()
		{
            var target = _api.photos;
            var actual = target.getAlbums();
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest()
		{
            var target = _api.photos;
            var subj_id = string.Empty;
			var aid = Constants.FBSamples_aid;
			var pids = new List<long> {Constants.FBSamples_pid};
			var actual = target.get(subj_id, aid, pids);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for createAlbum
		///</summary>
		[Test]
		public void createAlbumTest()
		{
            var target = _api.photos;
            var name = "test";
			var location = "test";
			var description = "test";
			var actual = target.createAlbum(name, location, description);
			Assert.AreEqual(actual.description, "test");
		}

		/// <summary>
		///A test for addTag
		///</summary>
		[Test]
		public void addTagTest()
		{
            var target = _api.photos;
            var pid = Constants.FBSamples_pid;
			var tag_uid = Constants.FBSamples_UserId;
			var tag_text = "test";
			var x = 0F;
			var y = 0F;

			target.addTag(pid, tag_uid, tag_text, x, y);
			Assert.IsTrue(1 == 1);
		}
	}
}