using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.IO;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Web;
using System.Windows.Forms;

using facebook.Forms;
using facebook.Properties;
using facebook.Schema;
using facebook.Utility;

namespace facebook
{
	/// <summary>
	/// Provides various methods to use the Facebook Platform API.
	/// </summary>
	public class API
	{
		internal const string ANDCLAUSE = " AND";
		internal const string NEWLINE = "\r\n";
		internal const string PREFIX = "--";
		internal const string VERSION = "1.0";
		private readonly auth _auth;
		private readonly marketplace _marketplace;
        private readonly admin _admin;
        private readonly events _events;
        private readonly groups _groups;
        private readonly photos _photos;
        private readonly users _users;
        private readonly friends _friends;
        private readonly notifications _notifications;
        private readonly profile _profile;
        private readonly feed _feed;
        private readonly fbml _fbml;
        private readonly fql _fql;
        private readonly liveMessage _liveMessage;
        private readonly batch _batch;
		private readonly pages _pages;
		private readonly application _application;
        private readonly data _data;
        private readonly permissions _permissions;

		public API()
		{
			AuthToken = string.Empty;
			IsDesktopApplication = true;
			InstalledCulture = CultureInfo.InstalledUICulture;

			_auth = new auth(this);
			_marketplace = new marketplace(this);
		    _admin = new admin(this);
            _photos = new photos(this);
            _friends = new friends(this);
            _users = new users(this);
            _events = new events(this);
            _groups = new groups(this);
            _notifications = new notifications(this);
            _profile = new profile(this);
            _fbml = new fbml(this);
            _feed = new feed(this);
            _fql = new fql(this);
            _liveMessage = new liveMessage(this);
            _batch = new batch(this);
			_pages = new pages(this);
			_application = new application(this);
            _data = new data(this);
            _permissions = new permissions(this);
        }

		public marketplace marketplace
		{
			get { return _marketplace; }
		}
        public photos photos
        {
            get { return _photos; }
        }
        public users users
        {
            get { return _users; }
        }
        public friends friends
        {
            get { return _friends; }
        }
        public events events
        {
            get { return _events; }
        }
        public groups groups
        {
            get { return _groups; }
        }

        public admin admin
        {
            get { return _admin; }
        }
        public profile profile
        {
            get { return _profile; }
        }
        public notifications notifications
        {
            get { return _notifications; }
        }
        public fbml fbml
        {
            get { return _fbml; }
        }
        public feed feed
        {
            get { return _feed; }
        }
        public fql fql
        {
            get { return _fql; }
        }
        public liveMessage liveMessage
        {
            get { return _liveMessage; }
        }
        public batch batch
        {
            get { return _batch; }
        }
		public pages pages
		{
			get { return _pages; }
		}
		public application application
		{
			get { return _application; }
		}
        public data data
        {
            get { return _data; }
        }
        public permissions permissions
        {
            get { return _permissions; }
        }
        public string ApplicationKey { get; set; }

		/// <summary>
		/// Whether or not the session expires
		/// </summary>
		public bool SessionExpires { get; private set; }

		/// <summary>
		/// Whether or not this component is being used in a desktop application
		/// </summary>
		public bool IsDesktopApplication { get; set; }

		/// <summary>
		/// Secret word
		/// </summary>
		public string Secret { get; set; }

		/// <summary>
		/// Session key
		/// </summary>
		public string SessionKey { get; set; }

		/// <summary>
		/// User Id
		/// </summary>
		public long uid { get; set; }

		/// <summary>
		/// Authorization token
		/// </summary>
		public string AuthToken { get; set; }

		private string LoginUrl
		{
			get
			{
				var args = new object[2];
				args[0] = ApplicationKey;
				args[1] = AuthToken;

				return String.Format(CultureInfo.InvariantCulture, Resources.FacebookLoginUrl, args);
			}
		}

		internal CultureInfo InstalledCulture { get; private set; }

		/// <summary>
		/// Creates and sets a new authentication token.
		/// </summary>
		public void SetAuthenticationToken()
		{
			AuthToken = _auth.createToken();
		}

		/// <summary>
		/// Displays an integrated browser to allow the user to log on to the
		/// Facebook web page.
		/// </summary>
		public void ConnectToFacebook()
		{
			if (!IsSessionActive() && IsDesktopApplication)
			{
				DialogResult result;
				SetAuthenticationToken();

				using (var formLogin = new FacebookAuthentication(LoginUrl))
				{
					result = formLogin.ShowDialog();
				}
				if (result == DialogResult.OK)
				{
					CreateSession();
				}
				else
				{
					throw new Exception("Login attempt failed");
				}
			}
		}

		/// <summary>
		/// Creates a new session with Facebook.
		/// </summary>
		/// <param name="authToken">The auth token received from Facebook.</param>
		public void CreateSession(string authToken)
		{
			AuthToken = authToken;
			CreateSession();
		}

		internal void CreateSession()
		{
			var auth = _auth.getSession(AuthToken);

			SessionKey = auth.session_key;
			uid = auth.uid;
			SessionExpires = auth.expires > 0;

			if (IsDesktopApplication)
			{
				Secret = auth.secret;
			}
		}

		internal bool IsSessionActive()
		{
			return !String.IsNullOrEmpty(SessionKey);
		}

		/// <summary>
		/// Forgets all connection information so that this object may be used for another connection.
		/// </summary>
		public void LogOff()
		{
			AuthToken = null;
			SessionKey = null;
			uid = 0;
		}


		internal void AddFBMLParameter(IDictionary<string, string> dict, string key, string value)
		{
			if (!string.IsNullOrEmpty(value))
			{
				dict.Add(key, string.Format(InstalledCulture, value.Replace("{", "{{").Replace("}", "}}")));
			}
		}

		internal void AddCultureParameter(IDictionary<string, string> dict, string key, string value)
		{
			if (!string.IsNullOrEmpty(value))
			{
				dict.Add(key, string.Format(InstalledCulture, value));
			}
		}

		internal void AddRequiredParameter(IDictionary<string, string> dict, string key, string value)
		{
			if (!string.IsNullOrEmpty(value))
			{
				dict.Add(key, value);
			}
			else
			{
				throw new Exception(string.Format("Error: Parameter '{0}' is required.", key));
			}
		}



        

		internal void AddRequiredParameter(IDictionary<string, string> dict, string key, int value)
		{
			if (value >= 0)
			{
				dict.Add(key, value.ToString());
			}
			else
			{
				throw new Exception(string.Format("Error: Parameter '{0}' is required.", key));
			}
		}
        internal void AddRequiredParameter(IDictionary<string, string> dict, string key, long value)
        {
            if (value >= 0)
            {
                dict.Add(key, value.ToString());
            }
            else
            {
                throw new Exception(string.Format("Error: Parameter '{0}' is required.", key));
            }
        }

        internal void AddRequiredParameter(IDictionary<string, string> dict, string key, double value)
        {
            if (value >= 0)
            {
                dict.Add(key, value.ToString());
            }
            else
            {
                throw new Exception(string.Format("Error: Parameter '{0}' is required.", key));
            }
        }
     
		internal void AddRequiredParameter(IDictionary<string, string> dict, string key, float value)
		{
			if (value >= 0.0)
			{
				dict.Add(key, value.ToString());
			}
			else
			{
				throw new Exception(string.Format("Error: Parameter '{0}' is required.", key));
			}
		}

        internal void AddRequiredParameter(IDictionary<string, string> dict, string key, assoc_object_type assoc_info)
        {
            if (!(assoc_info ==null))
            {
                var assoc = new Dictionary<string, string>
                                {
                                    {"alias" , assoc_info.alias},
                                    {"object_type" , assoc_info.object_type},
                                    {"unique" , assoc_info.unique.ToString()}
                                };
                AddJSONAssociativeArray(dict,key,assoc);
            }
            else
            {
                throw new Exception(string.Format("Error: Parameter '{0}' is required.", key));
            }
        }

       internal void AddRequiredParameter(IDictionary<string, string> dict, string key, IList<DataAssociation> assocs)
        {
            if (!(assocs == null))
            {
              Dictionary<string, string> tempDict;
              StringBuilder sb = new StringBuilder(assocs.Count*200);
              foreach(var da in assocs)
              {
                  tempDict = new Dictionary<string, string>
                                 {
                                     {"assoc_time", DateHelper.ConvertDateToDouble(da.assoc_time).ToString()},
                                     {"data", da.data},
                                     {"id1", da.id1.ToString()},
                                     {"id2", da.id2.ToString()},
                                     {"name", da.name}
                                 };
                  sb.Append(JSONHelper.ConvertToJSONAssociativeArray(tempDict));
              }
                AddRequiredParameter(dict,key,sb.ToString());
            }
            else
            {
                throw new Exception(string.Format("Error: Parameter '{0}' is required.", key));
            }
        }

		internal void AddParameter(IDictionary<string, string> dict, string key, bool value)
		{
			dict.Add(key, value.ToString());
		}

		internal void AddJSONAssociativeArray(IDictionary<string, string> dict, string key, Dictionary<string, string> value)
		{
			if (value != null && value.Count > 0)
			{
                dict.Add(key, JSONHelper.ConvertToJSONAssociativeArray(value));
			}
		}
		internal void AddJSONArray(IDictionary<string, string> dict, string key, List<string> value)
		{
			if (value != null && value.Count > 0)
			{
                dict.Add(key, JSONHelper.ConvertToJSONArray(value));
			}
		}

        internal void AddCollection(IDictionary<string, string> dict, string key, Collection<string> values)
		{
			if (!Equals(values, null) && values.Count > 0)
			{
				dict.Add(key, StringHelper.ConvertToCommaSeparated(values));
			}
		}

		internal void AddList(IDictionary<string, string> dict, string key, List<string> values)
		{
			if (!Equals(values, null) && values.Count > 0)
			{
				dict.Add(key, StringHelper.ConvertToCommaSeparated(values));
			}
		}

		internal void AddList(IDictionary<string, string> dict, string key, List<long> values)
		{
			if (!Equals(values, null) && values.Count > 0)
			{
				dict.Add(key, StringHelper.ConvertToCommaSeparated(values));
			}
		}
        internal void AddList(IDictionary<string, string> dict, string key, List<int> values)
        {
            if (!Equals(values, null) && values.Count > 0)
            {
                dict.Add(key, StringHelper.ConvertToCommaSeparated(values));
            }
        }

		internal void AddOptionalParameter(IDictionary<string, string> dict, string key, string value)
		{
			if (!string.IsNullOrEmpty(value))
			{
				dict.Add(key, value);
			}
		}

		internal void AddOptionalParameter(IDictionary<string, string> dict, string key, long value)
		{
            if (value >= 0)
            {
                dict.Add(key, value.ToString());
            }
		}
		internal void AddOptionalParameter(IDictionary<string, string> dict, string key, long? value)
		{
            if (value != null)
            {
                AddOptionalParameter(dict, key, value.Value);
            }
		}

        internal void AddOptionalParameter(IDictionary<string, string> dict, string key, int value)
        {
            if (value >= 0)
            {
                dict.Add(key, value.ToString());
            }
        }
        internal void AddOptionalParameter(IDictionary<string, string> dict, string key, int? value)
        {
            if (value != null)
            {
                AddOptionalParameter(dict, key, value.Value);
            }
        }
        internal void AddOptionalParameter(IDictionary<string, string> dict, string key, double value)
        {
            AddOptionalParameter(dict, key, value);
        }
        internal void AddOptionalParameter(IDictionary<string, string> dict, string key, double? value)
        {
            if (value >= 0)
            {
                dict.Add(key, value.ToString());
            }
        }



		internal static List<string> ParameterDictionaryToList(IEnumerable<KeyValuePair<string, string>> parameterDictionary)
		{
			var parameters = new List<string>();

			foreach (var kvp in parameterDictionary)
			{
				parameters.Add(String.Format(CultureInfo.InvariantCulture, "{0}", kvp.Key));
			}
			return parameters;
		}

		/// <summary>
		/// Sends a request through the REST API to Facebook. This is what the other methods all use to communicate with Facebook.
		/// You should only use this method if you need to call a method that is not defined in this toolkit. Note that this method
		/// will always use the session key in the request.
		/// </summary>
		/// <param name="parameterDictionary">
		/// A dictionary of parameters for the method being called, as specified in the documentation for the method. You will need
		/// to make sure to have a parameter named "method" corresponding to the method name. Note that the toolkit will populate
		/// the api_key, call_id, sig, v, and session_key parameters.
		/// </param>
		/// <returns>The XML response returned by Facebook.</returns>
		public string SendRequest(IDictionary<string, string> parameterDictionary)
		{
			return SendRequest(parameterDictionary, true);
		}

		/// <summary>
		/// Sends a request through the REST API to Facebook. This is what the other methods all use to communicate with Facebook.
		/// You should only use this method if you need to call a method that is not defined in this toolkit.
		/// </summary>
		/// <param name="parameterDictionary">
		/// A dictionary of parameters for the method being called, as specified in the documentation for the method. You will need
		/// to make sure to have a parameter named "method" corresponding to the method name. Note that the toolkit will populate
		/// the api_key, call_id, sig, v, and session_key parameters.
		/// </param>
		/// <param name="useSession">Whether or not to use the session key in the request.</param>
		/// <returns>The XML response returned by Facebook.</returns>
		public string SendRequest(IDictionary<string, string> parameterDictionary, bool useSession)
		{
			if (useSession)
			{
				parameterDictionary.Add("session_key", SessionKey);
			}

			string requestUrl = GetRequestUrl(parameterDictionary["method"] == "facebook.auth.getSession");
            if (permissions.isPermissionsModeActive)
            {
                parameterDictionary.Add("call_as_apikey", permissions.callAsApiKey);
            }
            string parameters = CreateHTTPParameterList(parameterDictionary);
            if (batch.isActive)
            {
                batch.callList.Add(parameters);
                return null;
            }
		    string result = string.Empty;
            try
            {
                result = processResponse(postRequest(requestUrl, parameters));
            }
            catch (Utility.FacebookException e)
            {
                if (e.ErrorCode == 4 && e.Message == "Application request limit reached")
                {
                    System.Diagnostics.Debug.WriteLine("Hit Limit. Wait for one minute and retry once.");
                    System.Threading.Thread.Sleep((60000));
                    result = processResponse(postRequest(requestUrl, parameters));
                }
                else
                    throw e;
            }
		    return result;
		}

		internal static string GetRequestUrl(bool useSSL)
		{
			return useSSL ? Resources.FacebookRESTUrl.Replace("http", "https") : Resources.FacebookRESTUrl;
		}

		internal string CreateHTTPParameterList(IDictionary<string, string> parameterList)
		{
			var queryBuilder = new StringBuilder();

			parameterList.Add("api_key", ApplicationKey);
			parameterList.Add("v", VERSION);
			parameterList.Add("call_id", DateTime.Now.Ticks.ToString("x", CultureInfo.InvariantCulture));

			parameterList.Add("sig", GenerateSignature(parameterList));

			// Build the query
			foreach (var kvp in parameterList)
			{
				queryBuilder.Append(kvp.Key);
				queryBuilder.Append("=");
                queryBuilder.Append(HttpUtility.UrlEncode(kvp.Value));
				queryBuilder.Append("&");
			}
			queryBuilder.Remove(queryBuilder.Length - 1, 1);

			return queryBuilder.ToString();
		}

		/// <summary>
		/// This method generates the signature based on parameters supplied
		/// </summary>
		/// <param name="parameters">List of paramenters</param>
		/// <returns>Generated signature</returns>
		internal string GenerateSignature(IDictionary<string, string> parameters)
		{
			var signatureBuilder = new StringBuilder();

			// Sort the keys of the method call in alphabetical order
			var keyList = ParameterDictionaryToList(parameters);
			keyList.Sort();

			// Append all the parameters to the signature input paramaters
			foreach (string key in keyList)
				signatureBuilder.Append(String.Format(CultureInfo.InvariantCulture, "{0}={1}", key, parameters[key]));

			// Append the secret to the signature builder
			signatureBuilder.Append(Secret);

			var md5 = MD5.Create();
			// Compute the MD5 hash of the signature builder
			var hash = md5.ComputeHash(Encoding.UTF8.GetBytes(signatureBuilder.ToString().Trim()));

			// Reinitialize the signature builder to store the actual signature
			signatureBuilder = new StringBuilder();

			// Append the hash to the signature
			foreach (var hashByte in hash)
				signatureBuilder.Append(hashByte.ToString("x2", CultureInfo.InvariantCulture));

			return signatureBuilder.ToString();
		}

        internal static WebResponse postRequest(string requestUrl, string postString)
        {

            var webRequest = WebRequest.Create(requestUrl);
            webRequest.Method = "POST";
            webRequest.ContentType = "application/x-www-form-urlencoded";

            if (!String.IsNullOrEmpty(postString))
            {
                var parameterString = Encoding.ASCII.GetBytes(postString);
                webRequest.ContentLength = parameterString.Length;

                using (var buffer = webRequest.GetRequestStream())
                {
                    buffer.Write(parameterString, 0, parameterString.Length);
                    buffer.Close();
                }
            }

            return webRequest.GetResponse();
        }

       


	    internal static string processResponse(WebResponse webResponse)
		{
			string xmlResponse;

			using (var streamReader = new StreamReader(webResponse.GetResponseStream()))
			{
				xmlResponse = streamReader.ReadToEnd();
			}

			ErrorCheck(xmlResponse);

			return xmlResponse;
		}

		internal static void ErrorCheck(string xml)
		{
			error_response error = null;

			try
			{
				error = error_response.Parse(xml);
			}
			catch
			{
				//If we failed to parse an the message as an exception message, that means it is not an exception
			}
			finally
			{
                try
                {
                    if (!Equals(error, null))
                    {
                        string request_args;
                        if (error.request_args==null)
                            request_args = "";
                        else 
                            request_args = error.request_args.ToString();
                        var ex = new FacebookException(error.Content.ToString(),
                                                       error.error_code, error.error_msg, request_args);
                        throw ex;
                    }
                }
                catch (FacebookException e)
                    {
                        throw e;
                    }
                catch(Exception e)
                {
                    throw new FacebookException("Error Parsing the error Text", -1, e.Message, xml);
                }
			}
		}


		internal string ExecuteApiUpload(FileSystemInfo uploadFile, IDictionary<string, string> parameterList)
		{
			parameterList.Add("api_key", ApplicationKey);
            parameterList.Add("session_key", SessionKey);
            parameterList.Add("v", VERSION);
			parameterList.Add("call_id", DateTime.Now.Ticks.ToString(CultureInfo.InvariantCulture));
			parameterList.Add("sig", GenerateSignature(parameterList));

			return GetFileQueryResponse(parameterList, uploadFile);
		}

		/// <summary>
		/// Get File Query Response
		/// </summary>
		/// <param name="parameterDictionary">parameter list</param>
		/// <param name="uploadFile">uploaded file info</param>
		/// <returns>Response data</returns>
		internal static string GetFileQueryResponse(IEnumerable<KeyValuePair<string, string>> parameterDictionary,
		                                            FileSystemInfo uploadFile)
		{
			string responseData;

			string boundary = DateTime.Now.Ticks.ToString("x", CultureInfo.InvariantCulture);
			string sRequestUrl = Resources.FacebookRESTUrl;

			// Build up the post message header
			var sb = new StringBuilder();
			foreach (var kvp in parameterDictionary)
			{
				sb.Append(PREFIX).Append(boundary).Append(NEWLINE);
				sb.Append("Content-Disposition: form-data; name=\"").Append(kvp.Key).Append("\"");
				sb.Append(NEWLINE);
				sb.Append(NEWLINE);
				sb.Append(kvp.Value);
				sb.Append(NEWLINE);
			}
			sb.Append(PREFIX).Append(boundary).Append(NEWLINE);
			sb.Append("Content-Disposition: form-data; filename=\"").Append(uploadFile.Name).Append("\"").Append(NEWLINE);
			sb.Append("Content-Type: image/jpeg").Append(NEWLINE).Append(NEWLINE);

			byte[] postHeaderBytes = Encoding.ASCII.GetBytes(sb.ToString());

			byte[] fileData = File.ReadAllBytes(uploadFile.FullName);

			byte[] boundaryBytes = Encoding.ASCII.GetBytes(String.Concat(NEWLINE, PREFIX, boundary, PREFIX, NEWLINE));

			var webrequest = (HttpWebRequest) WebRequest.Create(sRequestUrl);
			webrequest.ContentLength = postHeaderBytes.Length + fileData.Length + boundaryBytes.Length;
			webrequest.ContentType = String.Concat("multipart/form-data; boundary=", boundary);
			webrequest.Method = "POST";

			using (Stream requestStream = webrequest.GetRequestStream())
			{
				requestStream.Write(postHeaderBytes, 0, postHeaderBytes.Length);
				requestStream.Write(fileData, 0, fileData.Length);
				requestStream.Write(boundaryBytes, 0, boundaryBytes.Length);
			}
			var response = (HttpWebResponse) webrequest.GetResponse();
			using (var streamReader = new StreamReader(response.GetResponseStream()))
			{
				responseData = streamReader.ReadToEnd();
			}
            ErrorCheck(responseData);
			return responseData;
		}
	}
}
