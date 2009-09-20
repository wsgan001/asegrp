using System.Collections.Generic;
using System.Linq;
using facebook.Schema;
using facebook.Utility;
using System;

namespace facebook
{
	/// <summary>
	/// friends methods
	/// </summary>
	public class friends
	{
		#region Constructor

		private readonly API _api;

		/// <summary>
		/// Public constructor for facebook.friends
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public friends(API parent)
		{
			_api = parent;
		}

		#endregion

		#region Facebook API

		/// <summary>
		/// Returns whether or not each pair of specified users is friends with each other. The first array specifies one half of each pair, the second array the other half; therefore, they must be of equal size.
		/// </summary>
		/// <param name="users1">A list of user ids matched with uids2. This is a list of users.</param>
		/// <param name="users2">A list of user ids matched with uids1. This is a list of users.</param>
		/// <returns>Returns a list of friend_info elements corresponding to the lists passed.</returns>
		public IList<friend_info> areFriends(List<user> users1, List<user> users2)
		{
			var uids1 = (from u in users1 select u.uid.Value).ToList();
			var uids2 = (from u in users2 select u.uid.Value).ToList();
			return areFriends(uids1, uids2);
		}

		/// <summary>
		/// Returns whether or not each pair of specified users is friends with each other. The first array specifies one half of each pair, the second array the other half; therefore, they must be of equal size.
		/// </summary>
		/// <param name="uid1">the first user id</param>
		/// <param name="uid2">the second user id</param>
		/// <returns>Returns a list of friend_info elements corresponding to the lists passed. The are_friends subelement of each friend_info element will be 0 if the users are not friends, and 1 if they are friends. For each pair, this function is symmetric (does not matter which user is in uids1 and which is in uids2).</returns>
		public IList<friend_info> areFriends(long uid1, long uid2)
		{
			var uids1 = new List<long>();
			var uids2 = new List<long>();
			uids1.Add(uid1);
			uids2.Add(uid2);
			return areFriends(uids1, uids2);
		}

		/// <summary>
		/// Returns whether or not each pair of specified users is friends with each other. The first array specifies one half of each pair, the second array the other half; therefore, they must be of equal size.
		/// </summary>
		/// <param name="uids1">A list of user ids matched with uids2. This is a comma-separated list of user ids.</param>
		/// <param name="uids2">A list of user ids matched with uids1. This is a comma-separated list of user ids.</param>
		/// <returns>Returns a list of friend_info elements corresponding to the lists passed. The are_friends subelement of each friend_info element will be 0 if the users are not friends, and 1 if they are friends. For each pair, this function is symmetric (does not matter which user is in uids1 and which is in uids2).</returns>
		public IList<friend_info> areFriends(List<long> uids1, List<long> uids2)
		{
			var parameterList = new Dictionary<string, string>
			                    	{
			                    		{"method", "facebook.friends.areFriends"}
			                    	};
			_api.AddList(parameterList, "uids1", uids1);
			_api.AddList(parameterList, "uids2", uids2);
            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? friends_areFriends_response.Parse(response).friend_info : null;
		}

		/// <summary>
		/// Returns the identifiers of the current user's Facebook friends. The current user is determined from the session_key parameter. The values returned from this call are not storable.
		/// </summary>
		/// <param name="flid">Get the friends belonging to a Friend List.</param>
		/// <returns>Friend user ids.</returns>
		public IList<long> get(long flid)
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.friends.get"}};
			_api.AddOptionalParameter(parameterList, "flid", flid);
            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? friends_get_response.Parse(response).uid : null;
		}

		/// <summary>
		/// Returns the identifiers of the current user's Facebook friends who are signed up for the specific calling application. The current user is determined from the session_key parameter. The values returned from this call are not storable.
		/// </summary>
		/// <returns>The friend ids returned are those friends signed up for the calling application, a subset of the friends returned from the facebook.friends.get method. If no friends are found, the method will return an empty friends_getAppUsers_response element.</returns>
		public IList<long> getAppUsers()
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.friends.getAppUsers"}};

            var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? friends_getAppUsers_response.Parse(response).uid : null;
		}

		/// <summary>
		/// Returns the names and identifiers of any Friend Lists that the user has created. The current user is determined from the session_key parameter. The values returned from this call are storable (i.e., you may store the id of a friend list that the user has elected for use in some feature of your application), but should be verified periodically, as users may delete or modify lists at any time. Friend Lists are private on Facebook, so this information must not be republished to anyone other than the logged in user. Members of lists may be obtained using friends.get with an flid parameter.
		/// </summary>
		/// <returns>Friend list ids (flid) and list names.</returns>
		public IList<friendlist> getLists()
		{
			var parameterList = new Dictionary<string, string> {{"method", "facebook.friends.getLists"}};

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? friends_getLists_response.Parse(response).friendlist : null;
		}

		#endregion

		#region Extended API

		/// <summary>
		/// Returns the identifiers of the current user's Facebook friends. The current user is determined from the session_key parameter. The values returned from this call are not storable.
		/// </summary>
		/// <returns>Friend user ids.</returns>
		public IList<long> get()
		{
			return get(0);
		}

		/// <summary>
		/// Returns whether or not the pair of specified users is friends with each other.
		/// </summary>
		/// <param name="user1">First user.</param>
		/// <param name="user2">Second user.</param>
		/// <returns>Returns a list of friend_info elements corresponding to the lists passed.</returns>
		public IList<friend_info> areFriends(user user1, user user2)
		{
			var uids1 = new List<long>();
			var uids2 = new List<long>();
			uids1.Add(user1.uid.Value);
			uids2.Add(user2.uid.Value);
			return areFriends(uids1, uids2);
		}

		/// <summary>
		/// Returns the a list of user object for all of the logged in users friends
		/// </summary>
		/// <returns>Friend user ids.</returns>
		public IList<user> getUserObjects()
		{
			return getUserObjects(0);
		}

		/// <summary>
		/// Returns the a list of user object for all of the logged in users friends in the specified friend lists
		/// </summary>
		/// <param name="flid">Get the friends belonging to a Friend List.</param>
		/// <returns>Friend user ids.</returns>
		public IList<user> getUserObjects(long flid)
		{
            if (_api.batch.isActive)
            {
                throw new Exception("Extended API methods are not supported within a batch");
            }
			var parameterList = new Dictionary<string, string> {{"method", "facebook.friends.get"}};
			_api.AddOptionalParameter(parameterList, "flid", flid);

			return
				_api.users.getInfo(
					StringHelper.ConvertToCommaSeparated(friends_get_response.Parse(_api.SendRequest(parameterList)).uid));
		}

		/// <summary>
		/// Returns the a list of user object for all of the logged in users friends that are users of this application
		/// </summary>
		/// <returns>The friend ids returned are those friends signed up for the calling application, a subset of the friends returned from the facebook.friends.get method. If no friends are found, the method will return an empty friends_getAppUsers_response element.</returns>
		public IList<user> getAppUsersObjects()
		{
            if (_api.batch.isActive)
            {
                throw new Exception("Extended API methods are not supported within a batch");
            }
            var parameterList = new Dictionary<string, string> { { "method", "facebook.friends.getAppUsers" } };

			return
				_api.users.getInfo(
					StringHelper.ConvertToCommaSeparated(friends_getAppUsers_response.Parse(_api.SendRequest(parameterList)).uid));
		}

		#endregion
	}
}