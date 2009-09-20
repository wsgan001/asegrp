namespace facebook.Types
{
	/// <summary>
	/// Facebook Enums TODO: Move all these to an xml as key/value pairs and use Linq to XML or something cleaner
	/// </summary>
	public class Enums
	{
		/// <summary>
		/// Extended Permissions
		/// </summary>
		public enum Extended_Permissions
		{
			/// <summary>
			/// Status Update
			/// </summary>
			status_update,
			/// <summary>
			/// Photo Upload
			/// </summary>
			photo_upload,
			/// <summary>
			/// Create a listing
			/// </summary>
			create_listing,
            email, 
            offline_access,
            create_event,
            rsvp_event,
            sms
        }

		/// <summary>
		/// Represents the type of an object property.
		/// </summary>
		public enum ObjectPropertyType
		{
			/// <summary>
			/// Integer.
			/// </summary>
			Integer = 1,
			/// <summary>
			/// String with less than 255 characters.
			/// </summary>
			String = 2,
			/// <summary>
			/// Text blob which less than 64kb.
			/// </summary>
			TextBlob = 3
		}

		/// <summary>
		/// Type of data association.
		/// </summary>
		public enum DataAssociationType
		{
			/// <summary>
			/// One-way association, where reverse lookup is not needed.
			/// </summary>
			OneWay = 1,

			/// <summary>
			/// Two-way symmetric association, where a backward association
			/// (B to A) is always created when a forward association (A to B) is created.
			/// </summary>
			TwoWaySymmetric = 2,

			/// <summary>
			/// Two-way asymmetric association, where a backward association (B to A) has
			/// different meaning than a forward association (A to B).
			/// </summary>
			TwoWayAsymmetric = 3
		}
	}
}