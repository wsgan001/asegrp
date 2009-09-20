using System;
using System.Collections.Generic;
using NUnit.Framework;
using facebook.Schema;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for eventsTest and is intended
	///to contain all eventsTest Unit Tests
	///</summary>
	[TestFixture]
	public class eventsTest
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
		///A test for getMembers
		///</summary>
		[Test]
		public void getMembersTest()
		{
			var parent = _api;
			var target = parent.events;
			var eid = Constants.FBSamples_eid;
			var actual = target.getMembers(eid);
			Assert.IsTrue(actual.attending.uid.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest5()
		{
			var parent = _api;
            var target = parent.events;
			var actual = target.get();
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest4()
		{
			var parent = _api;
            var target = parent.events;
			var uid = Constants.FBSamples_UserId;
			var eids = new List<long> {Constants.FBSamples_eid};
			var start_time = DateTime.MinValue;
			var end_time = DateTime.MaxValue;
			var rsvp_status = "attending";
			var actual = target.get(uid, eids, start_time, end_time, rsvp_status);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest3()
		{
			var parent = _api;
            var target = parent.events;
			var uid = Constants.FBSamples_UserId;
			var actual = target.get(uid);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest2()
		{
			var parent = _api;
            var target = parent.events;
			var uid = Constants.FBSamples_UserId;
			var eids = new List<long> {Constants.FBSamples_eid};
			var start_time = DateTime.MinValue;
			var end_time = DateTime.MaxValue;
			var actual = target.get(uid, eids, start_time, end_time);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest1()
		{
			var parent = _api;
            var target = parent.events;
			var eids = new List<long> {Constants.FBSamples_eid};
			var actual = target.get(eids);
			Assert.IsTrue(actual.Count > 0);
		}

		/// <summary>
		///A test for get
		///</summary>
		[Test]
		public void getTest()
		{
			var parent = _api;
            var target = parent.events;
			var uid = Constants.FBSamples_UserId;
			var eids = new List<long> {Constants.FBSamples_eid};
			var actual = target.get(uid, eids);
			Assert.IsTrue(actual.Count > 0);
		}
        /// <summary>
        ///A test for get
        ///</summary>
        [Test]
        public void createEditDeleteTest()
        {
            var parent = _api;
            var target = parent.events;
            var event_info = new facebookevent { description = "event description", 
                end_date = System.DateTime.Now.AddDays(7), 
                event_subtype = "subtype", 
                event_type = "type", 
                location = "chicago", 
                start_date = System.DateTime.Now.AddDays(1), 
                name = "create event test" };
            var actual = target.create(event_info);
            Assert.IsTrue(actual > 0);

            event_info.name = "edited name";
            var e = target.edit(actual, event_info);
            Assert.IsTrue(e);
            var c = target.cancel(actual, "test cancel");
            Assert.IsTrue(c);
        }
    }
}