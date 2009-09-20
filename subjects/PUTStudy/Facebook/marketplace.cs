using System.Collections.Generic;
using System.Collections.ObjectModel;
using facebook.Schema;


namespace facebook
{
	/// <summary>
	/// Facebook marketplace api methods.
	/// </summary>
	public class marketplace
	{
		private readonly API _api;
		/// <summary>
		/// Public constructor for facebook.marketplace
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public marketplace(API parent)
		{
			_api = parent;
		}

		#region Facebook API

		/// <summary>
		/// Returns all the Marketplace categories. 
		/// </summary>
		/// <returns>This method returns a list of categories for use in Marketplace.</returns>
		public IList<string> getCategories()
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.marketplace.getCategories" } };
            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? marketplace_getCategories_response.Parse(response).marketplace_category : null;
		}

		/// <summary>
		/// Returns the Marketplace subcategories for a particular category.
		/// </summary>
		/// <param name="category">Filter by category. If this is not a valid category, no subcategories get returned.</param>
		/// <returns>A list of subcategories for use in Marketplace. </returns>
		public IList<string> getSubCategories(string category)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.marketplace.getSubCategories" } };
			_api.AddRequiredParameter(parameterList, "category", category);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? marketplace_getSubCategories_response.Parse(response).marketplace_subcategory : null;
		}

		/// <summary>
		/// Return all Marketplace listings either by listing ID or by user. 
		/// </summary>
		/// /// <param name="uids">Filter by a list of users. If you leave this blank, then the list is filtered only for listing IDs.</param>
		/// <param name="listing_ids">Filter by listing IDs. If you leave this blank, then the list is filtered only for user IDs.</param>
		/// <returns>This method returns all visible listings matching the criteria given. If no matching listings are found, the method returns an empty element.</returns>
		public IList<listing> getListings(List<long> listing_ids, Collection<string> uids)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.marketplace.getListings" } };
			_api.AddList(parameterList, "listing_ids", listing_ids);
			_api.AddCollection(parameterList, "uids", uids);

            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? marketplace_getListings_response.Parse(response).listing : null;
		}

		/// <summary>
		/// Will search the logged in users's networks for listings matching the category, subcategory, and query provided.
		/// </summary>
		/// <param name="category">Optional - the category to restirct search to, as returned by getCategories.</param>
		/// <param name="subcategory">Optional - the subcategory to restrict search to, as returned by getSubcategories. If a subcategory is provided, a category is also necessary.</param>
		/// <param name="query">Optional - the textual query to search the listings data.</param>
		/// <returns>Marketplace listings.</returns>
		public IList<listing> search(string category, string subcategory, string query)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.marketplace.search" } };
			_api.AddOptionalParameter(parameterList, "category", category);
			_api.AddOptionalParameter(parameterList, "subcategory", subcategory);
			_api.AddOptionalParameter(parameterList, "query", query);

            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? marketplace_search_response.Parse(response).listing : null;
		}

		/// <summary>
		/// Creates or modifies a listing in Marketplace. You may only create and modify a listing for a user 
		/// who has granted the application the extended permission create_listing.  
		/// </summary>
		/// <param name="listing_id">The listing ID to modify, or 0 if the user is creating a new listing.</param>
		/// <param name="show_on_profile">A privacy control indicating whether to display the listing on the poster's profile.</param>
		/// <param name="listing_attrs">Collection of Marketplace Listing Attributes.</param>
		/// <returns>This method returns the listing ID of the modified/created listing. If you are modifying a listing, it is the same as the listing ID provided to the method.</returns>
		public long createListing(long listing_id, bool show_on_profile, Dictionary<string, string> listing_attrs)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.marketplace.createListing" } };
			_api.AddRequiredParameter(parameterList, "listing_id", listing_id);
			_api.AddParameter(parameterList, "subcategory", show_on_profile);
            _api.AddJSONAssociativeArray(parameterList, "listing_attrs", listing_attrs);

            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? marketplace_createListing_response.Parse(response).TypedValue : 0;
		}

		/// <summary>
		/// Remove a Marketplace listing.
		/// </summary>
		/// <param name="listing_id">The listing ID to remove.</param>
		/// <param name="status">Removal status for tracking whether a Marketplace listing resulted in a successful transaction: "SUCCESS", "NOT_SUCCESS", or "DEFAULT".</param>
		/// <returns>True on success, error on failure. </returns>
		/// <remarks>The listing must be owned by loggedinuser.</remarks>
		public bool removeListing(long listing_id, string status)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.marketplace.removeListing" } };
			_api.AddRequiredParameter(parameterList, "listing_id", listing_id);
			_api.AddRequiredParameter(parameterList, "status", status);

            var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || marketplace_removeListing_response.Parse(response).TypedValue;
		}

		#endregion
	}
}
