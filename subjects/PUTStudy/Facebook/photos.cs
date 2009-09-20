using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using facebook.Schema;

namespace facebook
{
	/// <summary>
	/// 
	/// </summary>
	public class photos
	{
		#region Constructor

		private readonly API _api;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="parent"></param>
		public photos(API parent)
		{
			_api = parent;
		}

		#endregion

		#region Facebook API

		/// <summary>
		/// Adds a tag with the given information to a photo. See photo uploads for a description of the upload workflow.
		/// </summary>
		/// <param name="pid">The photo id of the photo to be tagged.</param>
		/// <param name="tag_uid">The user id of the user being tagged. Either tag_uid or tag_text must be specified.</param>
		/// <param name="tag_text">Some text identifying the person being tagged. Either tag_uid or tag_text must be specified. This parameter is ignored if tag_uid is specified.</param>
		/// <param name="x">The horizontal position of the tag, as a percentage from 0 to 100, from the left of the photo.</param>
		/// <param name="y">The vertical position of the tag, as a percentage from 0 to 100, from the top of the photo.</param>
		/// <param name="tags">A JSON-serialized array representing a list of tags to be added to the photo. If the tags parameter is specified, the x, y, tag_uid, and tag_text parameters are ignored. Each tag in the list must specify: "x", "y", and either the user id "tag_uid" or free-form "tag_text" identifying the person being tagged. An example of this is the string [{"x":"30.0","y":"30.0","tag_uid":1234567890}, {"x":"70.0","y":"70.0","tag_text":"some person"}].</param>
		/// <returns>True if successful, otherwise an error.</returns>
		/// <remarks>Tags can only be added to pending photos owned by the current session user. Once a photo has been approved or rejected, it can no longer be tagged with this method. There is a limit of 20 tags per pending photo.</remarks>
		public void addTag(long pid, long tag_uid, string tag_text, float x, float y)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.photos.addTag"}};
			_api.AddRequiredParameter(parameterList, "pid", pid);
			_api.AddOptionalParameter(parameterList, "tag_uid", tag_uid);
			_api.AddOptionalParameter(parameterList, "tag_text", tag_text);
			_api.AddRequiredParameter(parameterList, "x", x);
			_api.AddRequiredParameter(parameterList, "y", y);

			_api.SendRequest(parameterList);
		}
        //TODO: support tags property

		/// <summary>
		/// Creates and returns a new album owned by the current session user. See photo uploads for a description of the upload workflow. The only storable values returned from this call are aid and owner.
		/// </summary>
		/// <param name="name">The album name.</param>
		/// <param name="location">Optional - The album location.</param>
		/// <param name="description">Optional - The album description.</param>
		/// <returns>A new album owned by the current session user</returns>
		/// <remarks>The returned cover_pid is always 0.</remarks>
		public album createAlbum(string name, string location, string description)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.photos.createAlbum"}};
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddOptionalParameter(parameterList, "location", location);
			_api.AddOptionalParameter(parameterList, "description", description);
            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? photos_createAlbum_response.Parse(response).Content : null;
		}

		/// <summary>
		/// Returns all visible photos according to the filters specified. This may be used to find all photos in which a user is tagged, return photos in a specific album, or to query specific pids.
		/// </summary>
		/// <param name="subj_id">Filter by photos tagged with this user.</param>
		/// <param name="aid">Filter by photos in this album.</param>
		/// <param name="pids">Filter by photos in this list. This is a comma-separated list of pids.</param>
		/// <returns>This method returns all visible photos satisfying the filters specified. The method can be used to return all photos tagged with user, in an album, query a specific set of photos by a list of pids, or filter on any combination of these three.</returns>
		/// <remarks>It is an error to omit all three of the subj_id, aid, and pids parameters. They have no defaults.</remarks>
		public IList<photo> get(string subj_id, long aid, List<long> pids)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.photos.get"}};
			_api.AddOptionalParameter(parameterList, "subj_id", subj_id);
			_api.AddOptionalParameter(parameterList, "aid", aid);
			_api.AddList(parameterList, "pids", pids);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? photos_get_response.Parse(response).photo : null;
		}

		/// <summary>
		/// Returns metadata about all of the photo albums uploaded by the specified user. The values returned from this call are not storable.
		/// </summary>
		/// <param name="uid">Optional - Return albums created by this user.</param>
		/// <param name="aids">Optional - Return albums with aids in this list. This is a comma-separated list of pids.</param>
		/// <returns>This method returns all visible photos satisfying the filters specified. The method can be used to return all photo albums created by a user, query a specific set of albums by a list of aids, or filter on any combination of these two. The album ids returned by this function can be passed in to facebook.photos.get.</returns>
		/// <remarks>It is an error to omit both of the uid and aids parameters. They have no defaults. In this call, an album owned by a user will be returned to an application if that user has not turned off access to the Facbook Platform.</remarks>
		public IList<album> getAlbums(long uid, List<long> aids)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.photos.getAlbums"}};
			_api.AddOptionalParameter(parameterList, "uid", uid);
			_api.AddList(parameterList, "aids", aids);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? photos_getAlbums_response.Parse(response).album : null;
		}

		/// <summary>
		/// Returns the set of user tags on all photos specified.
		/// </summary>
		/// <param name="pids">The list of photos from which to extract photo tags. This is a comma-separated list of pids.</param>
		/// <returns>If no photo tags are found, the method will return an empty photos_getTags_response element. Text tags not corresponding to a user are not currently returned. Some tags may be text-only and will have an empty subect element. This occurs in the case where a user did not specifically tag another account, but just supplied text information.</returns>
		/// <remarks> A tag of a user will be visible to an application only if that user has not turned off access to the Facebook Platform.</remarks>
		public IList<photo_tag> getTags(List<long> pids)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.photos.getTags"}};
			_api.AddList(parameterList, "pids", pids);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? photos_getTags_response.Parse(response).photo_tag : null;
		}

		/// <summary>
		/// Uploads a photo owned by the current session user and returns the new photo. See photo uploads for a description of the upload workflow. The only storable values returned from this call are pid, aid, and owner.
		/// </summary>
		/// <param name="aid">Optional - The album id of the destination album.</param>
		/// <param name="caption">Optional - The caption of the photo.</param>
		/// <param name="data">The raw image data for the photo.</param>
		/// <returns>Photo information, including the photo URL.</returns>
		/// <remarks>If no album is specified, the photo will be uploaded to a default album for the application, which will be created if necessary. Regular albums have a size limit of 60 photos. Default application albums have a size limit of 1000 photos. It is strongly recommended that you scale the image in your application before adding it to the request. The largest dimension should be at most 604 pixels (the largest display size Facebook supports).</remarks>
		public photo upload(long aid, string caption, FileInfo data)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.photos.upload"}};
			_api.AddOptionalParameter(parameterList, "aid", aid);
			_api.AddOptionalParameter(parameterList, "caption", caption);

            var response = _api.ExecuteApiUpload(data, parameterList);
            return !string.IsNullOrEmpty(response) ? photos_upload_response.Parse(response).Content : null;
		}

		#endregion

		#region Extended API

		/// <summary>
		/// Returns metadata about all of the photo albums uploaded by the current user. The values returned from this call are not storable.
		/// </summary>
		/// <returns>This method returns all visible photos satisfying the filters specified. The method can be used to return all photo albums created by a user, query a specific set of albums by a list of aids, or filter on any combination of these two. The album ids returned by this function can be passed in to facebook.photos.get.</returns>
		/// <remarks>It is an error to omit both of the uid and aids parameters. They have no defaults. In this call, an album owned by a user will be returned to an application if that user has not turned off access to the Facbook Platform.</remarks>
		public IList<album> getAlbums()
		{
			return getAlbums(_api.uid, null);
		}

		/// <summary>
		/// Returns metadata about all of the photo albums uploaded by the specified user. The values returned from this call are not storable.
		/// </summary>
		/// <param name="uid">Optional - Return albums created by this user.</param>
		/// <returns>This method returns all visible photos satisfying the filters specified. The method can be used to return all photo albums created by a user, query a specific set of albums by a list of aids, or filter on any combination of these two. The album ids returned by this function can be passed in to facebook.photos.get.</returns>
		/// <remarks>It is an error to omit both of the uid and aids parameters. They have no defaults. In this call, an album owned by a user will be returned to an application if that user has not turned off access to the Facbook Platform.</remarks>
		public IList<album> getAlbums(long uid)
		{
			return getAlbums(uid, null);
		}

        /// <summary>
        /// Returns metadata about all of the photo albums uploaded by the specified user. The values returned from this call are not storable.
        /// </summary>
        /// <param name="uid">Optional - Return albums created by this user.</param>
        /// <returns>This method returns all visible photos satisfying the filters specified. The method can be used to return all photo albums created by a user, query a specific set of albums by a list of aids, or filter on any combination of these two. The album ids returned by this function can be passed in to facebook.photos.get.</returns>
        /// <remarks>It is an error to omit both of the uid and aids parameters. They have no defaults. In this call, an album owned by a user will be returned to an application if that user has not turned off access to the Facbook Platform.</remarks>
        public IList<album> getAlbums(long? uid)
        {
            return getAlbums((int)uid, null);
        }

		/// <summary>
		/// Returns metadata about all of the photo albums with the following ids. The values returned from this call are not storable.
		/// </summary>
		/// <param name="aids">Return albums with aids in this list. This is a comma-separated list of pids.</param>
		/// <returns>This method returns all visible photos satisfying the filters specified. The method can be used to return all photo albums created by a user, query a specific set of albums by a list of aids, or filter on any combination of these two. The album ids returned by this function can be passed in to facebook.photos.get.</returns>
		/// <remarks>It is an error to omit both of the uid and aids parameters. They have no defaults. In this call, an album owned by a user will be returned to an application if that user has not turned off access to the Facbook Platform.</remarks>
		public IList<album> getAlbums(List<long> aids)
		{
			return getAlbums(0, aids);
		}

		#endregion
	}
}