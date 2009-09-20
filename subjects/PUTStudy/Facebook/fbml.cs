using System.Collections.Generic;
using facebook.Schema;


namespace facebook
{
	/// <summary>
	/// Facebook fbml api methods.
	/// </summary>
	public class fbml
	{
        #region Constructor
        private readonly API _api;
		/// <summary>
		/// Public constructor for facebook.fbml
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
        public fbml(API parent)
        {
            _api = parent;
        }
        #endregion

        #region Facebook API
		/// <summary>
		/// Fetches and re-caches the image stored at the given URL, for use in images published to non-canvas pages via the API (e.g. to user profiles via facebook.profile.setFBML, or to news feed via facebook.feed.publishActionOfUser). See the FBML documentation for a description of the markup and its role in various contexts.
		/// </summary>
		/// <param name="url">Absolute URL from which to refresh the image.</param>
		/// <returns>This method returns 1 if Facebook found a cached version of your image and successfully refreshed the image. It returns a blank response if Facebook was unable to find any previously cached version to refresh, or the image was unable to be re-fetched from your site and cached successfully. In such instances, whatever images were cached before remain as they were cached.</returns>
		public bool refreshImgSrc(string url)
		{
			var parameterList = new Dictionary<string, string>
			                    	{
			                    		{"method", "facebook.fbml.refreshImgSrc"},
			                    		{"url", string.Format(_api.InstalledCulture, url)}
			                    	};

            var response = _api.SendRequest(parameterList);
            return string.IsNullOrEmpty(response) || fbml_refreshImgSrc_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// Fetches and re-caches the content stored at the given URL, for use in a fb:ref FBML tag. See the FBML documentation for a description of the markup and its role in various contexts.
		/// </summary>
		/// <param name="url">Absolute URL from which to fetch content. This URL should be used in a fb:ref FBML tag.</param>
		/// <returns>The result of the method call as a string.</returns>
		public bool refreshRefUrl(string url)
		{
			var parameterList = new Dictionary<string, string>
			                    	{
			                    		{"method", "facebook.fbml.refreshRefUrl"},
			                    		{"url", string.Format(_api.InstalledCulture, url)}
			                    	};

            var response = _api.SendRequest(parameterList);
            return string.IsNullOrEmpty(response) || fbml_refreshRefUrl_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// Associates a given "handle" with FBML markup so that the handle can be used within the fb:ref FBML tag. A handle is unique within an application and allows an application to publish identical FBML to many user profiles and do subsequent updates without having to republish FBML on behalf of each user. See the FBML documentation for a description of the markup and its role in various contexts.
		/// </summary>
		/// <param name="handle">Handle to associate with the given FBML.</param>
		/// <param name="fbml">FBML to associate with the given handle.</param>
		/// <returns>The result of the method call as a binary string.</returns>
		public bool setRefHandle(string handle, string fbml)
		{
			var parameterList = new Dictionary<string, string>
			                    	{{"method", "facebook.fbml.setRefHandle"}};
            _api.AddRequiredParameter(parameterList, "handle", handle);
			_api.AddRequiredParameter(parameterList, "fbml", fbml);

            var response = _api.SendRequest(parameterList);
            return string.IsNullOrEmpty(response) || fbml_setRefHandle_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// Lets you insert text strings in their native language into the Facebook Translations database so they can be translated. See Translating Platform Applications for more information about translating your applications. 
		/// </summary>
		/// <param name="native_strings">A JSON-encoded array of strings to translate. Each element of the string array is another array, with text storing the actual string, description storing the description of the text. </param>
		/// <returns>If successful, this method returns the number of strings uploaded. </returns>
		public bool uploadNativeStrings(Dictionary<string, string> native_strings)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.fbml.uploadNativeStrings" } };
			_api.AddJSONAssociativeArray(parameterList, "native_strings", native_strings);

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || fbml_uploadNativeStrings_response.Parse(response).TypedValue;
		}
        #endregion
	}
}