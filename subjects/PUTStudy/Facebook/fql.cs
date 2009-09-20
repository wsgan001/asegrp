using System.Collections.Generic;

namespace facebook
{
	/// <summary>
	/// fql methods
	/// </summary>
	public class fql
	{
		#region Constructor

		private readonly API _api;

		/// <summary>
		/// Public constructor for facebook.fql
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public fql(API parent)
		{
			_api = parent;
		}

		#endregion

		#region Facebook API

		/// <summary>
		/// Sends a direct FQL query to FB
		/// </summary>
		/// <param name="query">FQL Query</param>
		/// <returns>Result of the FQL query as an XML string</returns> 
		public string query(string query)
		{
			var parameterList = new Dictionary<string, string>(3) {{"method", "facebook.fql.query"}};
			_api.AddRequiredParameter(parameterList, "query", query);

			return _api.SendRequest(parameterList, true);
		}

		#endregion
	}
}