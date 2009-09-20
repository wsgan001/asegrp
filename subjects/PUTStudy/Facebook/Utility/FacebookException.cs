using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace facebook.Utility
{
	/// <summary>
	/// Represents an error returned by the Facebook REST-like API.
	/// </summary>
    public class FacebookException: Exception
    {
		/// <summary>
		/// The error returned by Facebook in XML format.
		/// </summary>
        public string ErrorXml { get; set; }

		/// <summary>
		/// The specific error code returned by Facebook.
		/// </summary>
        public int ErrorCode { get; set; }

        private string _message = "";

		/// <summary>
		/// The error message returned by Facebook.
		/// </summary>
        public override string Message
        {
            get
            {
                return _message;
            }
        }

		/// <summary>
		/// The request that caused the error, in XML format.
		/// </summary>
        public string RequestXml { get; set; }

		/// <summary>
		/// A value representing the specific type of error returned by Facebook.
		/// </summary>
        public ErrorType ErrorType { get { return (Utility.ErrorType) ErrorCode; } }

		/// <summary>
		/// Constructor for the FacebookException.
		/// </summary>
		/// <param name="errorXml">The request that caused the error, in XML format.</param>
		/// <param name="errorCode">The specific error code returned by Facebook.</param>
		/// <param name="message">The error message returned by Facebook.</param>
		/// <param name="requestXml">The request that caused the error, in XML format.</param>
        public FacebookException(string errorXml,
            int errorCode,
            string message,
            string requestXml)
        {
            ErrorXml = errorXml;
            ErrorCode = errorCode;
            _message = message;
            RequestXml = requestXml;
        }

    }

	/// <summary>
	/// A value representing the specific type of error returned by Facebook.
	/// </summary>
    public enum ErrorType
        {
            Unknown = 1,
            ServiceUnavailable = 2,
            RequestLimit = 4,
            Timeout = 102,
            Signing = 104,
            InvalidUser = 110,
            InvalidAlbum = 120,
            UserNotVisible = 210,
            AlbumNotVisible = 220,
            PhotoNotVIsible = 221,
            InvaldFQLSyntax = 601
        }
}
