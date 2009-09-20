using System;
using System.ComponentModel;
using System.Drawing;
using System.Globalization;
using System.Windows.Forms;
using facebook.Forms;
using facebook.Properties;
using facebook.Types;

namespace facebook.Components
{
	/// <summary>
	/// 
	/// </summary>
	[ToolboxItem(true), ToolboxBitmap(typeof (FacebookService)), Designer(typeof (FacebookServiceDesigner))]
	public partial class FacebookService : Component
	{
		private readonly API api;

		#region Accessors

		/// <summary>
		/// An object used to call various marketplace-related methods in Facebook's API.
		/// </summary>
		public marketplace marketplace
		{
			get { return api.marketplace; }
		}

		/// <summary>
		/// An object used to call various photo-related methods in Facebook's API.
		/// </summary>
		public photos photos
		{
			get { return api.photos; }
		}

		/// <summary>
		/// An object used to call various user-related methods in Facebook's API.
		/// </summary>
		public users users
		{
			get { return api.users; }
		}

		/// <summary>
		/// An object used to call various friend-related methods in Facebook's API.
		/// </summary>
		public friends friends
		{
			get { return api.friends; }
		}

		/// <summary>
		/// An object used to call various event-related methods in Facebook's API.
		/// </summary>
		public events events
		{
			get { return api.events; }
		}

		/// <summary>
		/// An object used to call various group-related methods in Facebook's API.
		/// </summary>
		public groups groups
		{
			get { return api.groups; }
		}

		/// <summary>
		/// An object used to call various admin-related methods in Facebook's API.
		/// </summary>
		public admin admin
		{
			get { return api.admin; }
		}

		/// <summary>
		/// An object used to call various profile-related methods in Facebook's API.
		/// </summary>
		public profile profile
		{
			get { return api.profile; }
		}

		/// <summary>
		/// An object used to call various notification-related methods in Facebook's API.
		/// </summary>
		public notifications notifications
		{
			get { return api.notifications; }
		}

		/// <summary>
		/// An object used to call various FBML-related methods in Facebook's API.
		/// </summary>
		public fbml fbml
		{
			get { return api.fbml; }
		}

		/// <summary>
		/// An object used to call various feed-related methods in Facebook's API.
		/// </summary>
		public feed feed
		{
			get { return api.feed; }
		}

		/// <summary>
		/// An object used to call various FQL-related methods in Facebook's API.
		/// </summary>
		public fql fql
		{
			get { return api.fql; }
		}

		/// <summary>
		/// An object used to call various live message-related methods in Facebook's API.
		/// </summary>
		public liveMessage liveMessage
		{
			get { return api.liveMessage; }
		}

		/// <summary>
		/// An object used to call various batch-related methods in Facebook's API.
		/// </summary>
		public batch batch
		{
			get { return api.batch; }
		}

		/// <summary>
		/// An object used to call various page-related methods in Facebook's API.
		/// </summary>
		public pages pages
		{
			get { return api.pages; }
		}

		/// <summary>
		/// An object used to call various application-related methods in Facebook's API.
		/// </summary>
		public application application
		{
			get { return api.application; }
		}

		/// <summary>
		/// The application key for your application.
		/// </summary>
		[Category("Facebook"), Description("Access Key required to use the API")]
		public string ApplicationKey
		{
			get { return api.ApplicationKey; }
			set { api.ApplicationKey = value; }
		}

		/// <summary>
		/// The underlying API client object to use to communicate with Facebook.
		/// </summary>
	    public API API
	    {
            get { return api; }
	        
	    }

		/// <summary>
		/// The secret for your Facebook application.
		/// </summary>
		[Category("Facebook"), Description("Secret Word")]
		public string Secret
		{
			get { return api.Secret; }
			set { api.Secret = value; }
		}

		/// <summary>
		/// The Facebook ID of the current user.
		/// </summary>
		[Browsable(false)]
		public long uid
		{
			get { return api.uid; }
			set { api.uid = value; }
		}

		/// <summary>
		/// The current session key to use.
		/// </summary>
		[Browsable(false)]
		public string SessionKey
		{
			get { return api.SessionKey; }
			set { api.SessionKey = value; }
		}

		/// <summary>
		/// Whether or not the session expires.
		/// </summary>
		[Browsable(false)]
		public bool SessionExpires
		{
			get { return api.SessionExpires; }
		}

		/// <summary>
		/// The URL used to login to Facebok. Constructed using the application key, auth token, and a URL template.
		/// </summary>
		[Browsable(false)]
		private string LoginUrl
		{
			get
			{
				var args = new object[2];
				args[0] = api.ApplicationKey;
				args[1] = api.AuthToken;

				return String.Format(CultureInfo.InvariantCulture, Resources.FacebookLoginUrl, args);
			}
		}

		/// <summary>
		/// The URL used to log off of Facebook. Constructed using the application key, auth token, and a URL template.
		/// </summary>
		[Browsable(false)]
		private string LogOffUrl
		{
			get
			{
				var args = new object[2];
				args[0] = api.ApplicationKey;
				args[1] = api.AuthToken;

				return String.Format(CultureInfo.InvariantCulture, Resources.FacebookLogoutUrl, args);
			}
		}

		/// <summary>
		/// Whether or not the current application is a desktop application.
		/// </summary>
		[Browsable(false)]
		public bool IsDesktopApplication
		{
			get { return api.IsDesktopApplication; }
			set { api.IsDesktopApplication = value; }
		}

		/// <summary>
		/// Constructs the URL to use to redirect a user to enable a given extended permission for your application.
		/// </summary>
		/// <param name="permission">The specific permission to enable.</param>
		/// <returns>The URL to redirect users to.</returns>
		[Browsable(false)]
		private string ExtendedPermissionUrl(Enums.Extended_Permissions permission)
		{
			var args = new object[2];
			args[0] = api.ApplicationKey;
			args[1] = permission;

			return String.Format(CultureInfo.InvariantCulture, Resources.FacebookRequestExtendedPermissionUrl, args);
		}

		#endregion

		#region Constructors

		/// <summary>
		/// 
		/// </summary>
		public FacebookService()
		{
			api = new API();
			InitializeComponent();
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="container"></param>
		public FacebookService(IContainer container)
		{
			if (container != null) container.Add(this);

			api = new API();
			InitializeComponent();
		}

		#endregion Constuctors

		/// <summary>
		/// Displays an integrated browser to allow the user to log on to the
		/// Facebook web page.
		/// </summary>
		public void ConnectToFacebook()
		{
			if (IsDesktopApplication)
			{
				DialogResult result;
				api.SetAuthenticationToken();

				using (var formLogin = new FacebookAuthentication(LoginUrl))
				{
					result = formLogin.ShowDialog();
				}
				if (result == DialogResult.OK)
				{
					api.CreateSession();
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
		/// <param name="authToken">The auth token to use to create the session.</param>
		public void CreateSession(string authToken)
		{
			api.AuthToken = authToken;
			api.CreateSession(authToken);
		}

		/// <summary>
		/// Displays an integrated browser to allow the user to log on to the
		/// Facebook web page.
		/// </summary>
		public void GetExtendedPermission(Enums.Extended_Permissions permission)
		{
			DialogResult result;

			using (var formLogin = new FacebookExtendedPermission(ExtendedPermissionUrl(permission), permission, this))
			{
				result = formLogin.ShowDialog();
			}

			if (result != DialogResult.OK)
			{
				throw new Exception("Extended Permission Denied");
			}
		}

		/// <summary>
		/// 
		/// </summary>
		public void LogOff()
		{
			api.LogOff();
		}
	}
}