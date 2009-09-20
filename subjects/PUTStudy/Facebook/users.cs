using System.Collections.Generic;
using facebook.Schema;
using facebook.Types;
using facebook.Utility;

namespace facebook
{
	/// <summary>
	/// users methods.
	/// </summary>
	public class users
	{
		#region Constructor

		private readonly API _api;

		/// <summary>
		/// Public constructor for facebook.permissions
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public users(API parent)
		{
			_api = parent;
		}

		#endregion

		/// <summary>
		/// Returns a wide array of user-specific information. The current user is determined from the session_key parameter. The only storable values returned from this call are the those under the affiliations element, the notes_count value, and the contents of the profile_update_time element.
		/// </summary>
		/// <returns>Currently logged in users' profile.</returns>
		public user getInfo()
		{
			return getInfo(_api.uid.ToString())[0];
		}

		/// <summary>
		/// Returns a wide array of user-specific information. The current user is determined from the session_key parameter. The only storable values returned from this call are the those under the affiliations element, the notes_count value, and the contents of the profile_update_time element.
		/// </summary>
		/// <returns>Currently logged in users' profile.</returns>
		public user getInfo(long uid)
		{
			return getInfo(uid.ToString())[0];
		}

		/// <summary>
		/// Returns a wide array of user-specific information for each user identifier passed, limited by the view of the current user. The current user is determined from the session_key parameter. The only storable values returned from this call are the those under the affiliations element, the notes_count value, and the contents of the profile_update_time element.
		/// </summary>
		/// <param name="userIds">List of user ids. This is a List<int> of user ids.</param>
		/// <returns>The user info elements returned are those friends visible to the Facebook Platform. If no visible users are found from the passed uids argument, the method will return an empty result element.</returns>
		public IList<user> getInfo(List<long> userIds)
		{
			return getInfo(StringHelper.ConvertToCommaSeparated(userIds));
		}

		/// <summary>
		/// Returns a wide array of user-specific information for each user identifier passed, limited by the view of the current user. The current user is determined from the session_key parameter. The only storable values returned from this call are the those under the affiliations element, the notes_count value, and the contents of the profile_update_time element.
		/// </summary>
		/// <param name="uids">List of user ids. This is a comma-separated list of user ids.</param>
		/// <returns>The user info elements returned are those friends visible to the Facebook Platform. If no visible users are found from the passed uids argument, the method will return an empty result element.</returns>
		public IList<user> getInfo(string uids)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.users.getInfo"}};
			_api.AddOptionalParameter(parameterList, "uids", uids);
			_api.AddRequiredParameter(parameterList, "fields",
			                  "first_name, last_name, name, pic_small, pic_big, pic_square, pic, affiliations, profile_update_time, timezone, religion, birthday, sex, hometown_location, meeting_sex, meeting_for, relationship_status, significant_other_id, political, current_location, activities, interests, is_app_user, music, tv, movies, books, quotes, about_me, hs_info, education_history, work_history, notes_count, wall_count, status");

            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? users_getInfo_response.Parse(response).user : null;
		}

		/// <summary>
		/// Get the facebook user id of the user associated with the current session
		/// </summary>
		/// <returns>facebook userid</returns>
		public long getLoggedInUser()
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.users.getLoggedInUser" } };

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? users_getLoggedInUser_response.Parse(response).TypedValue : 0;
		}

		/// <summary>
		/// Checks whether the user has opted in to an extended application permission.
		/// </summary>
		/// <param name="ext_perm">String identifier for the extended permission that is being checked for. Must be one of status_update or photo_upload.</param>
		/// <returns>Returns 1 or 0.</returns>
		public bool hasAppPermission(Enums.Extended_Permissions ext_perm)
		{
			var parameterList = new Dictionary<string, string>
			                    	{
			                    		{"method", "facebook.users.hasAppPermission"},
			                    		{"ext_perm", ext_perm.ToString()}
			                    	};

            var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || users_hasAppPermission_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// Returns whether the user (either the session user or user specified by uid) has authorized the calling application. 
		/// </summary>
		/// <param name="uid">The user ID of the user who may have authorized the application. If this parameter is not specified, then it defaults to the session user. This parameter applies only to Web applications and is required by them only if the session_key is not specified. Facebook ignores this parameter if it is passed by a desktop application. </param>
		/// <returns></returns>
		public bool isAppUser(long uid)
		{
			var parameterList = new Dictionary<string, string>{{"method", "facebook.users.isAppUser"}};
			_api.AddOptionalParameter(parameterList, "uid", uid);

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || users_isAppUser_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// Updates a user's Facebook status. This method requires the extended permission status_update, which the user must opt into via the Extended Permissions system. You can test for this permission by checking for the error code listed below (250), or via the users.hasAppPermission method.
		/// </summary>
		/// <param name="status">A status message</param>
		/// <returns>Returns true or false if the update succeeded</returns>
		public bool setStatus(string status)
		{
			return setStatus(status, false);
		}

		/// <summary>
		/// Updates a user's Facebook status. This method requires the extended permission status_update, which the user must opt into via the Extended Permissions system. You can test for this permission by checking for the error code listed below (250), or via the users.hasAppPermission method.
		/// </summary>
		/// <param name="status">A status message.</param>
		/// <param name="status_includes_verb">Pass True to avoid automatic "is" prepend.</param>
		/// <returns>Returns true or false if the update succeeded</returns>
		public bool setStatus(string status, bool status_includes_verb)
		{
			var parameterList = new Dictionary<string, string>
			                    	{
			                    		{"method", "facebook.users.setStatus"},
			                    		{"status", status},
			                    		{"status_includes_verb", status_includes_verb.ToString()}
			                    	};

            var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || users_setStatus_response.Parse(response).TypedValue;
		}
	}
}