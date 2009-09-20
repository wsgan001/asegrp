using System.Collections.Generic;
using facebook.Schema;

namespace facebook
{
	/// <summary>
	/// Facebook notifications api methods.
	/// </summary>
	public class notifications
	{
		#region Constructor

		private readonly API _api;

		/// <summary>
		/// Public constructor for facebook.notifications
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public notifications(API parent)
		{
			_api = parent;
		}

		#endregion

		#region Facebook API

		/// <summary>
		/// Returns information on outstanding Facebook notifications for current session user.
		/// </summary>
		/// <returns>This method returns the same set of subelements, whether or not there are outstanding notifications in any area. If the unread subelement value is 0 for any of the pokes or shares elements, the most_recent element will be 0. Otherwise, the most_recent element will contain an identifier for the most recent notification of the enclosing type.</returns>
		public Schema.notifications get()
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.notifications.get"}};

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? notifications_get_response.Parse(response).Content : null;
		}

		/// <summary>
		/// Send a notification to a set of users. You can send messages to the user's notification page without needing any confirmation. Notifications sent to the notifications page for non-app users are subject to spam control. Additionally, any notification that you send on behalf of a user will be shown on that user's notifications page as a "sent notification."
		/// </summary>
		/// <param name="to_ids">Comma-separated list of recipient ids. These must be friends of the logged-in user or people who have added your application. If you leave this blank, you can send a notification directly to the session user with no name prefixed to the message.</param>
		/// <param name="notification">FBML for the notifications page.</param>
		/// <returns>This returns a comma-separated list of user ids for whom notifications were successfully sent. We will throw an error if an error occurred.</returns>
		/// <remarks>The notification parameter is a very stripped-down set of FBML which allows only tags that result in just text and links.</remarks>
		public string send(string to_ids, string notification)
		{
			var parameterList = new Dictionary<string, string>
			                    	{
			                    		{"method", "facebook.notifications.send"},
			                    		{"notification", string.Format(_api.InstalledCulture, notification)},
			                    		{"to_ids", to_ids}
			                    	};

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? notifications_send_response.Parse(response).TypedValue : null;
		}

		/// <summary>
		/// Send an email to the specified users, who must have added your application. You can only send one email to a user per day. Requires a session key for desktop apps, which may only send emails to the person whose session it is (does not require a session for web apps).
		/// </summary>
		/// <param name="recipients">Comma-separated list of recipient ids. These must be people who have added the application. You can email up to 100 people at a time.</param>
		/// <param name="subject">Subject of the email.</param>
		/// <param name="text">The plain text version of the email content. You must include at least one of either the fbml or text parameters. </param>
		/// <param name="fbml">The FBML version of the email. You must include at least one of either the fbml or text parameters. The fbml parameter is a stripped-down set of FBML that allows only tags that result in text, links and linebreaks.</param>
		/// <returns>This returns a comma-separated list of user ids for whom notifications were successfully sent. We will throw an error if an error occurred.</returns>
		public string sendEmail(string recipients, string subject, string text, string fbml)
		{
			var parameterList = new Dictionary<string, string>
			                    	{
			                    		{"method", "facebook.notifications.sendEmail"},
			                    		{"recipients", recipients},
			                    		{"subject", subject},
			                    		{"text", text}
			                    	};

			_api.AddFBMLParameter(parameterList, "fbml", fbml);

            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? notifications_sendEmail_response.Parse(response).TypedValue : null;
		}

		#endregion
	}
}