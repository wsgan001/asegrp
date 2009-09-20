using System;
using System.Collections.Generic;
using facebook.Schema;
using facebook.Utility;

namespace facebook
{
	/// <summary>
	/// Facebook admin api methods.
	/// </summary>
    public class admin
    {
		/// <summary>
		/// 
		/// </summary>
        private readonly API _api;

        /// <summary>
        /// Public constructor for facebook.admin
        /// </summary>
        /// <param name="parent">Needs a connected API object for making requests</param>
        public admin(API parent)
        {
            _api = parent;
        }

        #region Facebook API

		/// <summary>
		/// Gets various metrics for your application from Facebook.
		/// </summary>
		/// <param name="startDate">The start of the time span to get metrics for.</param>
		/// <param name="endDate">The end of the time span to get metrics for.</param>
		/// <param name="period">The type of time period for which metrics were collected.</param>
		/// <returns></returns>
        public IList<metrics> getMetrics(DateTime startDate, DateTime endDate, Period period)
        {
            return getMetrics(GetMetricNames(period), startDate, endDate, period);
        }
        /// <summary>
        /// Returns specified daily metrics for your application, given a date range. 
        /// </summary>
        /// <returns></returns>
        public IList<metrics> getMetrics(List<string> metrics, DateTime startDate, DateTime endDate, Period period)
        {
            var parameterList = new Dictionary<string, string> {{"method", "facebook.admin.getMetrics"}};
            _api.AddRequiredParameter(parameterList, "start_time", DateHelper.ConvertDateToDouble(startDate).ToString());
            _api.AddRequiredParameter(parameterList, "end_time", DateHelper.ConvertDateToDouble(endDate).ToString());
            _api.AddRequiredParameter(parameterList, "period", period.ToString("D"));
            _api.AddJSONArray(parameterList, "metrics", metrics);
            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? admin_getMetrics_response.Parse(response).metrics : null;
        }


        /// <summary>
        /// Returns the current allocation limits for your application for the specified integration points. Allocation limits are determined daily.
        /// </summary>
        /// <returns>The requested allocation</returns>
        public int getAllocation(IntegrationPointName name)
        {
            var parameterList = new Dictionary<string, string> {{"method", "facebook.admin.getAllocation"}};
            _api.AddRequiredParameter(parameterList, "integration_point_name", name.ToString());
            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? admin_getAllocation_response.Parse(response).TypedValue : 0;
        }
        /// <summary>
        /// Returns the current allocation limits for your application for the specified integration points. Allocation limits are determined daily.
        /// </summary>
        /// <returns>The requested allocation</returns>
        public bool setAppProperties(Dictionary<string,string> properties)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.admin.setAppProperties" } };
            _api.AddJSONAssociativeArray(parameterList, "properties", properties);
            string response = _api.SendRequest(parameterList);
            return string.IsNullOrEmpty(response) || admin_setAppProperties_response.Parse(response).TypedValue;
        }
        public Dictionary<string,string> getAppProperties()
        {
            return getAppProperties(GetApplicationPropertyNames());
        }
        /// <summary>
        /// Returns the current allocation limits for your application for the specified integration points. Allocation limits are determined daily.
        /// </summary>
        /// <returns>The requested allocation</returns>
        public Dictionary<string,string> getAppProperties(List<string> applicationProperites)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.admin.getAppProperties" } };
            _api.AddJSONArray(parameterList, "properties", applicationProperites);
            string response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? JSONHelper.ConvertFromJSONAssoicativeArray(admin_getAppProperties_response.Parse(response).TypedValue) : null;
        }
        #endregion

        #region Entity Definitions

		/// <summary>
		/// The type of period over which metrics are collected.
		/// </summary>
        public enum Period
        {
            Day = 86400,
            Week = 604800,
            Month = 2592000
        }

        public enum IntegrationPointName
        {
            notifications_per_day,
            requests_per_day,
            emails_per_day,
            email_disable_message_location
        }
        public List<string> GetApplicationPropertyNames()
        {
            return new List<string>
            {
                "application_name",
                "callback_url",
                "post_install_url",
                "edit_url",    
                "dashboard_url",
                "uninstall_url",
                "ip_list",
                "email",
                "description",
                "use_iframe",
                "desktop",
                "is_mobile",
                "default_fbml",
                "default_column",
                "message_url",
                "message_action",
                "about_url",
                "private_install",
                "installable",
                "privacy_url",
                "help_url",
                "see_all_url",
                "tos_url",
                "dev_mode",
                "preload_fql",
                "default_action_fbml",
                "canvas_name",
                "icon_url",
                "logo_url"}; 
        } 
        public List<string> GetMetricNames(Period period)
        {
            //TODO: Use refelction against the schema to get these values
            var types = new List<string>
                       {
                          "active_users",
                          "api_calls",
                            "unique_api_calls",
                            "canvas_page_views",
                            "unique_canvas_page_views",
                            "canvas_http_request_time_avg",
                            "canvas_fbml_render_time_avg"
                       };
            //These are only valid for day-based values
            if (period == Period.Day)
            {
                types.AddRange(new List<string>
                               {
                                   "unique_adds",
                                   "unique_removes",
                                   "unique_blocks",
                                   "unique_unblocks"
                               });

            }
            return types;
        }
        public List<string> GetDailyMetricNames()
        {
            //TODO: Use refelction against the schema to get these values
            return new List<string>
                       {
                           "date",
                           "daily_active_users",
                           "unique_adds",
                           "unique_removes",
                           "unique_blocks",
                           "unique_unblocks",
                           "api_calls",
                           "unique_api_calls",
                           "canvas_page_views",
                           "unique_canvas_page_views",
                           "canvas_http_request_time_avg",
                           "canvas_fbml_render_time_avg"
                       };
        }
        #endregion

        #region Private Helper Methods
        #endregion
    }
}
