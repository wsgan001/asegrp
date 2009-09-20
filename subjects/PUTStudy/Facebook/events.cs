using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using facebook.Schema;
using facebook.Utility;
using System.Globalization;


namespace facebook
{
	/// <summary>
	/// Events methods
	/// </summary>
	public class events
	{
        #region Constructor
        private readonly API _api;
		/// <summary>
		/// Public constructor for facebook.events
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
        public events(API parent)
        {
            _api = parent;
        }
        #endregion

        #region Facebook API
		/// <summary>
		/// Returns all visible events according to the filters specified. This may be used to find all events of a user, or to query specific eids.
		/// </summary>
		/// <param name="uid">Filter by events associated with a user with this uid.</param>
		/// <param name="eids">Filter by this list of event ids. This is a comma-separated list of eids.</param>
		/// <param name="start_time">Filter with this UTC as lower bound. A missing or zero parameter indicates no lower bound.</param>
		/// <param name="end_time">Filter with this UTC as upper bound. A missing or zero parameter indicates no upper bound.</param>
		/// <param name="rsvp_status">Filter by this RSVP status.  attending,unsure,declined,not_replied </param>
		/// <returns>This method returns all events satisfying the filters specified. The method can be used to return all events associated with user, or query a specific set of events by a list of eids.</returns> 
		public IList<facebookevent> get(long? uid, List<long> eids, DateTime? start_time, DateTime? end_time, string rsvp_status)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.events.get" } };
			_api.AddOptionalParameter(parameterList, "uid", uid);
			_api.AddList(parameterList, "eids", eids);
			_api.AddOptionalParameter(parameterList, "start_time", DateHelper.ConvertDateToDouble(start_time));
			_api.AddOptionalParameter(parameterList, "end_time", DateHelper.ConvertDateToDouble(end_time));
			_api.AddOptionalParameter(parameterList, "rsvp_status", rsvp_status);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? events_get_response.Parse(response).facebookevent : null;
		}

        /// <summary>
        /// Cancels the event
        /// </summary>
        /// <param name="eid">Unique identifier of event to cancel.</param>
		/// <param name="cancelMessage">string explaining why event was cancelled </param>
        /// <returns>If successful</returns> 
        public bool cancel(long eid, string cancelMessage)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.events.cancel" } };
            _api.AddRequiredParameter(parameterList, "eid", eid);
            _api.AddOptionalParameter(parameterList, "cancel_message", cancelMessage);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? events_cancel_response.Parse(response).TypedValue : false;
        }
        /// <summary>
        /// Create an event - You must pass the following parameters in the event_info array: 
        ///name 
        ///category 
        ///subcategory 
        ///location 
        ///start_time 
        ///end_time 
        ///Note: The start_time and end_time are the times that were input by the event creator, converted to UTC after assuming that they were in Pacific time (Daylight Savings or Standard, depending on the date of the event), then converted into Unix epoch time. 
        ///
        ///Optionally, you can pass the following parameters in the event_info array: 
        ///    
        ///street 
        ///phone 
        ///email 
        ///host_id 
        ///host 
        ///desc 
        ///privacy_type 
        ///tagline 
        /// </summary>
        /// <param name="event_info">key value pairs describin the event.</param>
        /// <returns>The eid of the created event</returns> 
        public long create(facebookevent event_info)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.events.create" } };
            var dict = new Dictionary<string,string>
            {
                {"description", event_info.description},
                {"end_time", event_info.end_time.ToString()},
                {"category", event_info.event_type},
                {"subcategory", event_info.event_subtype},
                {"host", event_info.host},
                {"location", event_info.location},
                {"name", event_info.name},
                {"start_time", event_info.start_time.ToString()},
                {"tagline", event_info.tagline}
            };
            _api.AddJSONAssociativeArray(parameterList, "event_info", dict);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? events_create_response.Parse(response).TypedValue : 0;
        }
        /// <summary>
        /// Create an event - You must pass the following parameters in the event_info array: 
        ///name 
        ///category 
        ///subcategory 
        ///location 
        ///start_time 
        ///end_time 
        ///Note: The start_time and end_time are the times that were input by the event creator, converted to UTC after assuming that they were in Pacific time (Daylight Savings or Standard, depending on the date of the event), then converted into Unix epoch time. 
        ///
        ///Optionally, you can pass the following parameters in the event_info array: 
        ///    
        ///street 
        ///phone 
        ///email 
        ///host_id 
        ///host 
        ///desc 
        ///privacy_type 
        ///tagline 
        /// </summary>
        /// <param name="eid">identifier of the event being updated</param>
        /// <param name="event_info">key value pairs describing the event.</param>
        /// <returns>If successful</returns> 
        public bool edit(long eid, facebookevent event_info)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.events.edit" } };
            _api.AddRequiredParameter(parameterList, "eid", eid);
            var dict = new Dictionary<string, string>
            {
                {"description", event_info.description},
                {"end_time", event_info.end_time.ToString()},
                {"category", event_info.event_type},
                {"subcategory", event_info.event_subtype},
                {"host", event_info.host},
                {"location", event_info.location},
                {"name", event_info.name},
                {"start_time", event_info.start_time.ToString()},
                {"tagline", event_info.tagline}
            };
            _api.AddJSONAssociativeArray(parameterList, "event_info", dict);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? events_edit_response.Parse(response).TypedValue : false;
        }
        /// <summary>
        /// Sets the rsvp status
        /// </summary>
        /// <param name="eid">Unique identifier of event to cancel.</param>
		/// <param name="rsvpStatus">The user's RSVP status. Specify attending, unsure, or declined. </param>
        /// <returns>If successful</returns> 
        public bool rsvp(long eid, string rsvpStatus)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.events.rsvp" } };
            _api.AddRequiredParameter(parameterList, "eid", eid);
            _api.AddRequiredParameter(parameterList, "rsvp_status", rsvpStatus);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? events_cancel_response.Parse(response).TypedValue : false;
        }

		/// <summary>
		/// Returns membership list data associated with an event.
		/// </summary>
		/// <param name="eid">Event id to return users for.</param>
		/// <returns>This method returns four (possibly empty) lists of users associated with an event, keyed on their associations. These lists should never share any members.</returns>
		/// <remarks>The lists can contain uids of users not using the calling application.</remarks>
		public event_members getMembers(long eid)
		{
		    var parameterList = new Dictionary<string, string> {{"method", "facebook.events.getMembers"}};
            _api.AddRequiredParameter(parameterList, "eid", eid);
            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? events_getMembers_response.Parse(response).Content : null;
		}
        #endregion

        #region Extended API

		/// <summary>
		/// Returns all visible events for the logged-in user.
		/// </summary>
		/// <returns>A list of events.</returns> 
		public IList<facebookevent> get()
		{
			return get(_api.uid, null, null, null, null);
		}

		/// <summary>
		/// Returns all visible events for the given user.
		/// </summary>
		/// <param name="uid">The user to get events for.</param>
		/// <returns>A list of events belonging to that user.</returns>
		public IList<facebookevent> get(long? uid)
		{
			return get(uid, null, null, null, null);
		}

		/// <summary>
		/// Returns all visible events for the given user.
		/// </summary>
		/// <param name="uid">The user to get events for.</param>
		/// <param name="eids">Filter by this list of event ids. This is a comma-separated list of eids.</param>
		/// <returns>A list of events belonging to that user.</returns>
		public IList<facebookevent> get(long? uid, List<long> eids)
		{
			return get(uid, eids, null, null, null);
		}

		/// <summary>
		/// Returns the events corresponding to the given event ids.
		/// </summary>
		/// <param name="eids">Filter by this list of event ids. This is a comma-separated list of eids.</param>
		/// <returns>A list of the events corresponding to the given evnet ids.</returns>
		public IList<facebookevent> get(List<long> eids)
		{
			return get(0, eids, null, null, null);
		}

		/// <summary>
		/// Returns all visible events according to the filters specified. This may be used to find all events of a user, or to query specific eids.
		/// </summary>
		/// <param name="uid">Filter by events associated with a user with this uid.</param>
		/// <param name="eids">Filter by this list of event ids. This is a comma-separated list of eids.</param>
		/// <param name="start_time">Filter with this UTC as lower bound. A missing or zero parameter indicates no lower bound.</param>
		/// <param name="end_time">Filter with this UTC as upper bound. A missing or zero parameter indicates no upper bound.</param>
		/// <returns>This method returns all events satisfying the filters specified. The method can be used to return all events associated with user, or query a specific set of events by a list of eids.</returns> 
		public IList<facebookevent> get(long? uid, List<long> eids, DateTime? start_time, DateTime? end_time)
		{
			return get(uid, eids, null, null, null); ;
		}
        #endregion
    }
}