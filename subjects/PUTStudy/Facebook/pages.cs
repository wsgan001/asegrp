using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using facebook.Schema;

namespace facebook
{
	/// <summary>
	/// Facebook application api methods.
	/// </summary>
	public class pages
	{
		private readonly API _api;
		/// <summary>
		/// Public constructor for facebook.auth
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public pages(API parent)
		{
			_api = parent;
		}

		/// <summary>
		/// Returns all visible pages to the filters specified. This may be used to find all pages of which a user is a fan, or to query specific page_ids. The session_key parameter is optional. When the session_key parameter is not passed, you can only get information for pages that have added your application. The uid parameter is not even considered. With a session_key, normal privacy rules are put into place. 
		/// </summary>
		/// <param name="fields">List of desired fields in return. This is a comma-separated list of field strings. </param>
		/// <param name="page_ids">List of page IDs. This is a comma-separated list of page IDs. </param>
		/// <param name="uid">The ID of the user. Defaults to the logged in user if the session_key is valid, and no page_ids are passed. Used to get the pages a given user is a fan of.</param>
		/// <returns>The page info elements returned are those visible to the Facebook Platform.</returns>
		public IList<page> getInfo(List<string> fields, List<long> page_ids, long? uid)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.pages.getInfo" } };
			_api.AddList(parameterList, "fields", fields);
			_api.AddList(parameterList, "page_ids", page_ids);
			_api.AddOptionalParameter(parameterList, "uid", uid);

			var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? pages_getInfo_response.Parse(response).page : null;
		}

		/// <summary>
		/// Checks whether the page has added the application. 
		/// </summary>
		/// <param name="page_id">The id of the page </param>
		/// <returns>True or False</returns>
		public bool isAppAdded(long page_id)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.pages.isAppAdded" } };
			_api.AddOptionalParameter(parameterList, "page_id", page_id);

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || pages_isAppAdded_response.Parse(response).TypedValue;
		}

        /// <summary>
		/// Checks whether the logged-in user is the admin for a given page. 
		/// </summary>
		/// <param name="page_id">The id of the page </param>
		/// <returns>True or False</returns>
		public bool isAdmin(long page_id)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.pages.isAdmin" } };
			_api.AddRequiredParameter(parameterList, "page_id", page_id);

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || pages_isAdmin_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// Checks whether a user is a fan of a given Page. Doesn't work for Application about Pages.
		/// </summary>
		/// <param name="page_id">The id of the page </param>
		/// <param name="uid">The id of the page </param>
		/// <returns>True or False</returns>
        public bool isFan(long page_id)
        {
            return isFan(page_id, _api.uid);
        }
		/// <summary>
		/// Checks whether a user is a fan of a given Page. Doesn't work for Application about Pages.
		/// </summary>
		/// <param name="page_id">The id of the page </param>
		/// <param name="uid">The id of the page </param>
		/// <returns>True or False</returns>
		public bool isFan(long page_id, long uid)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.pages.isFan" } };
			_api.AddOptionalParameter(parameterList, "page_id", page_id);
			_api.AddOptionalParameter(parameterList, "uid", uid);

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || pages_isFan_response.Parse(response).TypedValue;
		}

        public List<string> GetFields()
        {
            return new List<string>
            {
                "page_id",
                "name",
                "pic_small",
                "pic_square",
                "pic_big",
                "pic",
                "pic_large",
                "type",
                "website",
                "location",
                "hours",
                "band_members",
                "bio",
                "hometown",
                "genre",
                "record_label",
                "influences",
                "has_added_app",
                "founded",
                "company_overview",
                "mission",
                "products",
                "release_date",
                "starring",
                "written_by",
                "directed_by",
                "produced_by",
                "studio",
                "awards",
                "plot_outline",
                "network",
                "season",
                "schedule"}; 

        }
	}
}
