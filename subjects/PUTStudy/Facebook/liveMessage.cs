using System.Collections.Generic;
using facebook.Schema;

namespace facebook
{
	/// <summary>
	/// Public class facebook.liveMessage
	/// </summary>
	public class liveMessage
	{
		#region Constructor

		private readonly API _api;

		/// <summary>
		/// Public constructor for facebook.liveMessage
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public liveMessage(API parent)
		{
			_api = parent;
		}

		#endregion

		#region Facebook API

		/// <summary>
		/// Sends a live message to a particular user's browser, which should be handled in FBJS. Messages can only be sent and received by users who have accepted your application's Terms of Service. 
		/// </summary>
		/// <param name="recipient">The message recipient. </param>
		/// <param name="event_name">Name of the "event" for which messages will be sent and received (max length: 128 bytes). A LiveMessage FBJS object must be initialized with this event name to receive the message. </param>
		/// <param name="message">A JSON-encoded string of the message to send (max length: 1024 bytes). </param>
		/// <returns>Boolean of success or failure.</returns>
		public bool send(long recipient, string event_name, string message)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.liveMessage.send"}};
			_api.AddRequiredParameter(parameterList, "recipient", recipient.ToString());
			_api.AddRequiredParameter(parameterList, "event_name", event_name);
			_api.AddJSONAssociativeArray(parameterList, "message", new Dictionary<string, string>{{"from", _api.uid.ToString()}, {"msg", message}});

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || liveMessage_send_response.Parse(response).TypedValue;
		}

		#endregion
	}
}