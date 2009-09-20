using System.Collections.Generic;
using NUnit.Framework;

namespace facebook.tests
{
	/// <summary>
	///This is a test class for marketplaceTest and is intended
	///to contain all marketplaceTest Unit Tests
	///</summary>
	[TestFixture]
	public class marketplaceTest
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
		///A test for getCategories
		///</summary>
		[Test]
		public void getCategoriesTest()
		{
			var target = _api.marketplace;
			var actual = target.getCategories();
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0]);
		}

		/// <summary>
		///A test for getSubCategories
		///</summary>
		[Test]
		public void getSubCategoriesTest()
		{
            var target = _api.marketplace;
            var category = "FORSALE";
			var actual = target.getSubCategories(category);
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0]);
		}


		/// <summary>
		///A test for search
		///</summary>
		[Test]
		public void searchTest()
		{
            var target = _api.marketplace;
            var category = "OTHER";
			var subcategory = "GENERAL";
			var query = string.Empty;
			var actual = target.search(category, subcategory, query);
			Assert.IsTrue(actual.Count > 0);
			Assert.IsNotNull(actual[0].category);
		}


		/// <summary>
		///A test for createListing
		///</summary>
		[Test]
		public void createGetRemoveListingTest()
		{
            var target = _api.marketplace;

			var create_attrs = new Dictionary<string, string>
			                   	{
			                   		{"category", "OTHER"},
			                   		{"subcategory", "GENERAL"},
			                   		{"title", "Facbook Toolkit Test Listing"},
			                   		{"description", "Testing out the facebook developers toolkit."}
			                   	};
			var newListingID = target.createListing(0, false, create_attrs);
			Assert.IsTrue(newListingID > 0);

			var listing_ids = new List<long> {Constants.FBSamples_listing1};
			var actual = target.getListings(listing_ids, null);
			Assert.IsTrue(actual.Count > 0);

			var isRemoved = target.removeListing(newListingID, "DEFAULT");
			Assert.IsTrue(isRemoved);
		}

		/// <summary>
		///A test for getListings
		///</summary>
		[Test]
		public void getListingsTest()
		{
            var target = _api.marketplace;
            var listing_ids = new List<long> { Constants.FBSamples_listing1 };
			var actual = target.getListings(listing_ids, null);
			Assert.IsTrue(actual.Count > 0);
		}
	}
}