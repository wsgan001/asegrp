using System.Collections.Generic;
using facebook.Schema;

namespace facebook
{
	/// <summary>
	/// Facebook application api methods.
	/// </summary>
	public class application
	{
		private readonly API _api;
		/// <summary>
		/// Public constructor for facebook.auth
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public application(API parent)
		{
			_api = parent;
		}

		/// <summary>
		/// Returns public information for an application (as shown in the application directory) by either application ID, API key, or canvas page name. 
		/// </summary>
		/// <param name="application_id">Application ID of the desired application. You must specify exactly one of application_id, application_api_key or application_canvas_name. </param>
		/// <param name="application_api_key">API key of the desired application. You must specify exactly one of application_id, application_api_key or application_canvas_name. </param>
		/// <param name="application_canvas_name">Canvas page name of the desired application. You must specify exactly one of application_id, application_api_key or application_canvas_name. </param>
		/// <returns>list of app_info objects</returns>
        public app_info getPublicInfo(int application_id, string application_api_key, string application_canvas_name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.application.getPublicInfo" } };
            _api.AddOptionalParameter(parameterList, "application_id", application_id);
			_api.AddOptionalParameter(parameterList, "application_api_key", application_api_key);
			_api.AddOptionalParameter(parameterList, "application_canvas_name", application_canvas_name);

			var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? application_getPublicInfo_response.Parse(response).Content : null;
		}
        /// <summary>
        /// Returns public information for the current application (as shown in the application directory) by either application ID, API key, or canvas page name. 
        /// </summary>
        /// <returns>list of app_info objects</returns>
        public app_info getPublicInfo()
        {
            return getPublicInfo(0, _api.ApplicationKey, null);
        }

	}
}
