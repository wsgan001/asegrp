using System.Collections.Generic;
using System.Collections.ObjectModel;
using facebook.Schema;
using facebook.Utility;


namespace facebook
{
	/// <summary>
	/// facebook.groups methods.
	/// </summary>
	public class groups
	{
        #region Constructor
        private readonly API _api;
		/// <summary>
		/// Public constructor for facebook.groups
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
        public groups(API parent)
        {
            _api = parent;
        }
        #endregion

		/// <summary>
		/// Returns all visible groups according to the filters specified. This may be used to find all groups of which a user is as member, or to query specific gids.
		/// </summary>
		/// <returns>All groups of the session user.</returns>
        public IList<group> get()
		{
			return get(_api.uid, null);
		}

		/// <summary>
		/// Returns all visible groups according to the filters specified. This may be used to find all groups of which a user is as member, or to query specific gids.
		/// </summary>
		/// <param name="uid">Filter by groups associated with a user with this uid</param>
		/// <returns>All groups of the supplied user id.</returns>
        public IList<group> get(long uid)
		{
			return get(uid, null);
		}

		/// <summary>
		/// Returns all visible groups according to the filters specified. This may be used to find all groups of which a user is as member, or to query specific gids.
		/// </summary>
		/// <param name="gids">Filter by this list of group ids. This is a comma-separated list of gids.</param>
		/// <returns>If the uid parameter is omitted, the method returns all groups associted with the provided gids, regardless of any user relationship.</returns>
        public IList<group> get(List<long> gids)
		{
			return get(0, gids);
		}

		/// <summary>
		/// Returns all visible groups according to the filters specified. This may be used to find all groups of which a user is as member, or to query specific gids.
		/// </summary>
		/// <param name="uid">Filter by groups associated with a user with this uid</param>
		/// <param name="gids">Filter by this list of group ids. This is a comma-separated list of gids.</param>
		/// <returns>This method returns all groups satisfying the filters specified. The method can be used to return all groups associated with user, or query a specific set of events by a list of gids. If both the uid and gids parameters are provided, the method returns all groups in the set of gids, with which the user is associated. If the gids parameter is omitted, the method returns all groups associated with the provided user.</returns>
		/// <remarks>Group creators will be visible to an application only if the creator has not turned off access to the Platform or used the application'; If the creator has opted out, the creator element will appear as nil=true.</remarks>
		public IList<group> get(long uid, List<long> gids)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.groups.get"}};
            _api.AddOptionalParameter(parameterList, "uid", uid);
            _api.AddList(parameterList, "gids", gids);

            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? groups_get_response.Parse(response).group : null;
		}

		/// <summary>
		/// Returns membership list data associated with a group.
		/// </summary>
		/// <param name="gid">Group id to return members for.</param>
		/// <returns>This method returns four (possibly empty) lists of users associated with a group, keyed on their associations. The members list will contain the officers and admins lists, but will not overlap with the not_replied list.</returns>
		public group_members getMembers(long gid)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.groups.getMembers"}};
            _api.AddRequiredParameter(parameterList, "gid", gid);
            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? groups_getMembers_response.Parse(response).Content : null;
		}
	}
}