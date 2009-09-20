using System.Collections.Generic;
using facebook.Schema;

namespace facebook
{
	/// <summary>
	/// Facebook permissions api methods.
	/// </summary>
	public class permissions
	{
		private readonly API _api;

        #region Constructor
        /// <summary>
		/// Public constructor for facebook.permissions
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public permissions(API parent)
		{
			_api = parent;
        }
        #endregion

        #region Facebook API
        /// <summary>
		/// This method gives another application access to certain API calls on behalf of the application calling it. The application granted access is specified by permissions_apikey. Which methods or namespaces can be called are specified in method_arr.
		/// </summary>
		/// <param name="method_arr">JSON array of methods and/or namespaces for which the access is granted. If this is not specified, access to all allowed methods is granted.</param>
		/// <returns>The method returns a bool value indicating whether the call succeeded or failed. </returns>
		/// <remarks>The only namespace that can be granted access at this time is admin. </remarks>
		public bool grantApiAccess(string apiKeyGrantedAccess, List<string> method_arr)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.permissions.grantApiAccess" } };
            _api.AddRequiredParameter(parameterList, "permissions_apikey", apiKeyGrantedAccess);
            _api.AddJSONArray(parameterList, "method_arr", method_arr);

            var response = _api.SendRequest(parameterList);
        	return string.IsNullOrEmpty(response) || permissions_grantApiAccess_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// This method returns the API methods to which access has been granted by the specified application. 
		/// </summary>
		/// <param name="permissions_apikey">The API key of the application for which the check is being done.</param>
		/// <returns>The method returns an array of strings listing all methods/namespaces for which access is available.</returns>
		public IList<string> checkAvailableApiAccess(string permissions_apikey)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.permissions.checkAvailableApiAccess" } };
			_api.AddRequiredParameter(parameterList, "permissions_apikey", permissions_apikey);

            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response)
			       	? permissions_checkAvailableApiAccess_response.Parse(response).
			       	  	permissions_checkAvailableApiAccess_response_elt
			       	: null;
		}

		/// <summary>
		/// This method revokes the API access granted to the specified application. 
		/// </summary>
		/// <param name="permissions_apikey">The API key for the target application. </param>
		/// <returns>The method returns a bool value indicating whether the call succeeded or failed. </returns>
		public bool revokeApiAccess(string permissions_apikey)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.permissions.revokeApiAccess" } };
			_api.AddRequiredParameter(parameterList, "permissions_apikey", permissions_apikey);

            var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || permissions_revokeApiAccess_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// This method returns the API methods to which the specified application has been given access. 
		/// </summary>
		/// <param name="permissions_apikey">The API key of the application for which the check is being done. </param>
		/// <returns>The method returns an array of strings listing all methods/namespaces for which access has been granted. </returns>
		public IList<string> checkGrantedApiAccess(string permissions_apikey)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.permissions.checkGrantedApiAccess" } };
			_api.AddRequiredParameter(parameterList, "permissions_apikey", permissions_apikey);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? permissions_checkGrantedApiAccess_response.Parse(response).permissions_checkGrantedApiAccess_response_elt : null;
		}
        private string _callAsApiKey;
        public void beginPermissionsMode(string callAsApiKey)
        {
            isPermissionsModeActive = true;
            _callAsApiKey = callAsApiKey;
        }
        public void endPermissionsMode(string callAsApiKey)
        {
            isPermissionsModeActive = false;
            _callAsApiKey = null;
        }
        public bool isPermissionsModeActive
        {
            get;
            set;
        }
        public string callAsApiKey
        {
            get{return _callAsApiKey;}
        }
        #endregion
    }
}
