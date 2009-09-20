using System.Collections.Generic;
using facebook.Schema;


namespace facebook
{
	/// <summary>
	/// Facebook auth api methods.
	/// </summary>
	public class auth
	{
		private readonly API _api;

		/// <summary>
		/// Public constructor for facebook.auth
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public auth(API parent)
		{
			_api = parent;
		}

		/// <summary>
		/// (Intended for desktop applications only.) Creates an auth_token to be passed in as a parameter to login.php and then to facebook.auth.getSession after the user has logged in. The user must log in soon after you create this token. See the facebook authentication guide for more information.
		/// </summary>
		/// <returns>Authentication token Guid.</returns>
		/// <remarks>That this function does not require a session_key or call_id (although specifying a call_id will not cause any problems). The values returned from this call are storable, but expire on their first use in facebook.auth.getSession.</remarks>
		public string createToken()
		{
            var parameterList = new Dictionary<string, string> { { "method", "facebook.auth.createToken" } };

            var response = _api.SendRequest(parameterList, false);
            return !string.IsNullOrEmpty(response) ? auth_createToken_response.Parse(response).TypedValue : null;
		}

		/// <summary>
		/// Returns the session key bound to an auth_token (as returned by facebook.auth.createToken or in the callback_url). Should be called immediately after the user has logged in. See the facebook authentication guide for more information.
		/// </summary>
		/// <returns>If the user has successfully logged in, this will return valid values for each field. The expires element is a Unix time that indicates when the given session will expire. If the value is 0, the session will never expire. See the authentication guide for more information.</returns>
		/// <remarks>For desktop applications this method must be called at the https endpoint instead of the http endpoint, and its return value is slightly different (as noted below). Also, this function does not require a session_key or call_id (although specifying a call_id will not cause any problems). The session key is storable for the duration of the session, and the uid is storable indefinitely. For desktop applications, the top-level element will have an additional element named secret that should be used as the session's secret key as described in the facebook authentication guide.</remarks>
		public session_info getSession()
		{
			return  getSession(createToken());
		}

		/// <summary>
		/// Returns the session key bound to an auth_token (as returned by facebook.auth.createToken or in the callback_url). Should be called immediately after the user has logged in. See the facebook authentication guide for more information.
		/// </summary>
		/// <param name="auth_token"></param>
		/// <returns>If the user has successfully logged in, this will return valid values for each field. The expires element is a Unix time that indicates when the given session will expire. If the value is 0, the session will never expire. See the authentication guide for more information.</returns>
		/// <remarks>For desktop applications this method must be called at the https endpoint instead of the http endpoint, and its return value is slightly different (as noted below). Also, this function does not require a session_key or call_id (although specifying a call_id will not cause any problems). The session key is storable for the duration of the session, and the uid is storable indefinitely. For desktop applications, the top-level element will have an additional element named secret that should be used as the session's secret key as described in the facebook authentication guide.</remarks>
		public session_info getSession(string auth_token)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.auth.getSession" }, { "auth_token", auth_token } };

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? auth_getSession_response.Parse(response).Content : null;
        }

		/// <summary>
		/// Invalidates the current session being used, regardless of whether it is temporary or infinite. After successfully calling this function, no further API calls requiring a session will succeed using this session.
		/// </summary>
		/// <returns>If the invalidation is successful, this will return true.</returns>
		public bool expireSession()
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.auth.expireSession" } };

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || auth_expireSession_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// If this method is called for the logged in user, then no further API calls can be made on that user's behalf until the user decides to authorize the application again. 
		/// </summary>
		/// <returns>If the revoke is successful, this will return true.</returns>
		public void revokeAuthorization()
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.auth.revokeAuthorization" } };

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Creates a temporary session secret for the current (non-infinite) session of a Web application. This session secret will not be used in the signature for the server-side component of an application, it is only meant for use by applications which additionally want to use a client side component.
		/// </summary>
		/// <returns></returns>
		public auth_promoteSession_response promoteSession()
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.auth.promoteSession" } };

			var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? auth_promoteSession_response.Parse(response) : null;
		}
	}
}
