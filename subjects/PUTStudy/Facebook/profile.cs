using System.Collections.Generic;
using facebook.Schema;
using facebook.Utility;


namespace facebook
{
	/// <summary>
	/// 
	/// </summary>
	public class profile
	{
        #region Constructor
        private readonly API _api;
		/// <summary>
		/// Public constructor for facebook.profile
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
        public profile(API parent)
        {
            _api = parent;
        }
        #endregion

        #region Facebook API

		/// <summary>
		/// Gets the FBML for the profile box that is currently set for a user's profile (your application set the FBML previously by calling the profile.setFBML method). See the FBML documentation for a description of the markup and its role in various contexts. 
		/// </summary>
		/// <param name="uid">The user whose profile FBML is to be fetched, or the page ID in case of a Page. This parameter applies only to Web applications and is required by them only if the session_key is not specified. Facebook ignores this parameter if it is passed by a desktop application. </param>
		/// <param name="type">The type of profile box to retrieve. Specify 1 for the original style (wide and narrow column boxes), 2 for profile_main box. (default value is 1)</param>
		/// <returns>The FBML markup from the user's profile.</returns>
		/// <remarks>It is not a violation of Facebook Privacy policy if you use this method to retrieve content originally rendered by your application from a user's profile, even if Facebook privacy restrictions would otherwise keep you from seeing that user’s profile (for example, you are not friends with the user in question). Cases where this would arise include verifying content posted by one user of your application to another user’s profile complies with the Facebook Developer Terms of Service. </remarks>
		public string getFBML(long uid, int type)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.profile.getFBML" } };
			_api.AddOptionalParameter(parameterList, "uid", uid);
			_api.AddOptionalParameter(parameterList, "type", type);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? profile_getFBML_response.Parse(response).TypedValue : null;
		}

		/// <summary>
		/// Sets the FBML for a user's profile, including the content for both the profile box and the profile actions. See the FBML documentation for a description of the markup and its role in various contexts.
		/// </summary>
		/// <param name="uid">The user whose profile is to be updated. Not allowed for desktop applications (since the application secret is essentially public).</param>
		/// <param name="profile">The FBML intended for the application profile box on the user's profile. </param>
		/// <param name="profile_main">The FBML intended for the narrow profile box on the Wall tab of the user's profile.</param>
		/// <param name="mobile_profile">The FBML intended for mobile devices. </param>
		/// <returns>True on success, false if error.</returns>
		/// <remarks>The FBML is cached on Facebook's server for that particular user and that particular application. To change it, profile.setFBML must be called through a canvas page or some other script (such as a cron job) that makes use of the Facebook API. </remarks>
		public bool setFBML(long uid, string profile, string profile_main, string mobile_profile)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.profile.setFBML" } };
			_api.AddOptionalParameter(parameterList, "uid", uid);
			_api.AddOptionalParameter(parameterList, "profile", profile);
			_api.AddOptionalParameter(parameterList, "profile_main", profile_main);
			_api.AddOptionalParameter(parameterList, "mobile_profile", mobile_profile);

            var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || profile_setFBML_response.Parse(response).TypedValue;
		}

		///// <summary>
		///// Returns the specified user's application info section for the calling application. 
		///// </summary>
		///// <returns>The content returned is specified within info_fields.</returns>
        public profile_getInfo_response getInfo(long uid)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.profile.getInfo" } };
			_api.AddRequiredParameter(parameterList, "uid", uid);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? profile_getInfo_response.Parse(response) : null;
        }

		///// <summary>
		///// Returns the options associated with the specified field for an application info section. 
		///// </summary>
		///// <returns></returns>
        public bool getInfoOptions(string field)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.profile.getInfoOptions" } };

            _api.AddRequiredParameter(parameterList, "field", field);
            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? profile_getInfoOptions_response.Parse(response).TypedValue : false;
        }

		///// <summary>
		///// Configures an application info section that the specified user can install on the Info tab of her profile. 
		///// </summary>
		///// <param name="title">The title or header of the application info section. </param>
		///// <param name="type">Specify 1 for a text-only field-item configuration or 5 for a thumbnail configuration.</param>
		///// <param name="info_fields">A JSON-encoded array of elements comprising an application info section, including the field (the title of the field) and an array of info_item objects (each object has a label and a link, and optionally contains image, description, and sublabel fields. </param>
		///// <param name="uid">The user ID of the user adding the application info section. </param>
		///// <returns></returns>
        public bool setInfo(string title, int type, List<info_field> info_fields, long uid)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.profile.setInfo" } };
            _api.AddRequiredParameter(parameterList, "title", title);
            _api.AddRequiredParameter(parameterList, "type", type);

			var fieldList = new List<string>();
			foreach (var field in info_fields)
			{
				var itemList = new List<string>();
				foreach (var item in field.items.info_item)
				{
					var itemDict = new Dictionary<string, string>{
						{"label", item.label},
						{"sublabel", item.sublabel},
						{"link", item.link},
						{"image", item.image},
						{"description", item.description}
					};
					itemList.Add(JSONHelper.ConvertToJSONAssociativeArray(itemDict));
				}

				var fieldDict = new Dictionary<string, string>{
					{"field", field.field},
					{"items", JSONHelper.ConvertToJSONArray(itemList)}
				};
				fieldList.Add(JSONHelper.ConvertToJSONAssociativeArray(fieldDict));
			}

            _api.AddJSONArray(parameterList, "info_fields", fieldList);
            _api.AddRequiredParameter(parameterList, "uid", uid);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? profile_setInfo_response.Parse(response).TypedValue : false;
        }

        public bool setInfoOptions(string field, List<info_item> options)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.profile.setInfoOptions" } };
            _api.AddRequiredParameter(parameterList, "field", field);

            var list = new List<string>();
            foreach (var item in options)
            {
                var dict = new Dictionary<string, string>{
                    {"label", item.label},
                    {"sublabel", item.sublabel},
                    {"link", item.link},
                    {"image", item.image},
                    {"description", item.description}
                };
                list.Add(JSONHelper.ConvertToJSONAssociativeArray(dict));
            }


            _api.AddJSONArray(parameterList, "options", list);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? profile_setInfoOptions_response.Parse(response).TypedValue : false;
        }


        #endregion
	}
}