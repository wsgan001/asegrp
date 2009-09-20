using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using facebook.Schema;
using facebook.Utility;


namespace facebook
{
	/// <summary>
	/// feed methods
	/// </summary>
	public class feed
	{
        #region Constructor
        private readonly API _api;
		/// <summary>
		/// Public constructor for facebook.feed
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
        public feed(API parent)
        {
            _api = parent;
        }
        #endregion

        #region Facebook API

		/// <summary>
		/// Publishes a Mini-Feed story to the user corresponding to the session_key parameter, and publishes News Feed stories to the friends of that user who have added the application.
		/// </summary>
		/// <param name="title_template"></param>
		/// <param name="title_data"></param>
		/// <param name="body_template"></param>
		/// <param name="body_data"></param>
		/// <param name="images"></param>
		/// <returns></returns>
		public bool publishTemplatizedAction(string title_template, Dictionary<string, string> title_data,
											   string body_template, Dictionary<string, string> body_data,
		                                       Collection<feed_image> images)
		{
			return publishTemplatizedAction(title_template, title_data, body_template, body_data, null, 0, images, null);
		}

		/// <summary>
		/// Publishes a Mini-Feed story to the user corresponding to the session_key parameter, and publishes News Feed stories to the friends of that user who have added the application.
		/// </summary>
		/// <param name="title_template">The templatized markup displayed in the feed story's title section. This template must contain the token {actor} somewhere in it.</param>
		/// <param name="title_data">Optional - A JSON-encoded associative array of the values that should be substituted into the templates in the title_template markup string. The keys of this array are the tokens, and their associated values are the desired substitutions. 'actor' and 'target' are special tokens and should not be included in this array. If your title_template contains tokens besides 'actor' and 'target', then this is a required parameter.</param>
		/// <param name="body_template">Optional - The markup displayed in the feed story's body section.</param>
		/// <param name="body_data">Optional - A JSON-encoded associative array of the values that should be substituted into the templates in the body_template markup string. The keys of this array are the tokens, and their associated values are the desired substitutions. 'actor' and 'target' are special token and should not be included in this array.</param>
		/// <param name="body_general">Optional - Additional markup displayed in the feed story's body section. This markup is not required to be identical for two stories to be aggregated. One of the two will be chosen at random.</param>
		/// <param name="page_actor_id">Optional - if publishing a story to a Facebook Page, use this parameter as the page who performed the action. If you use this parameter, the application must be added to that Page's Feed. A session key is not required to do this.</param>
		/// <param name="images"></param>
		/// <param name="target_ids"></param>
		/// <returns></returns>
		public bool publishTemplatizedAction(string title_template, Dictionary<string, string> title_data,
											   string body_template, Dictionary<string, string> body_data,
											   string body_general, int page_actor_id,
		                                       Collection<feed_image> images, Collection<string> target_ids)
		{
			var parameterList = new Dictionary<string, string>{{"method", "facebook.feed.publishTemplatizedAction"}};
			_api.AddRequiredParameter(parameterList, "title_template", title_template);
			_api.AddOptionalParameter(parameterList, "page_actor_id", page_actor_id);
            _api.AddJSONAssociativeArray(parameterList, "title_data", title_data);
            _api.AddJSONAssociativeArray(parameterList, "body_data", body_data);
            _api.AddOptionalParameter(parameterList, "body_template", body_template);
            _api.AddOptionalParameter(parameterList, "body_general", body_general);
            _api.AddCollection(parameterList, "target_ids", target_ids);
            AddFeedImages(parameterList, images);

            var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || feed_publishTemplatizedAction_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// Deactivates a previously registered template bundle. Once a template bundle has been deactivated, it can no longer be used to publish stories via feed.publishUserAction. Stories published against the template bundle prior to its deactivation are still valid and will show up in Mini-Feed and News Feed. The response is true if and only if the template bundle, identified by template_bundle_id, is an active template bundle owned by the requesting application, and false otherwise. 
		/// </summary>
		/// <param name="template_bundle_id">The template bundle ID used to identify a previously registered template bundle. The ID is the one returned by a previous call to feed.registerTemplateBundle. </param>
		/// <returns>True or False</returns>
		public bool deactivateTemplateBundleByID(string template_bundle_id)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.feed.deactivateTemplateBundleByID" } };
			_api.AddRequiredParameter(parameterList, "template_bundle_id", template_bundle_id);

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || feed_deactivateTemplateBundleByID_response.Parse(response).TypedValue;
		}

		/// <summary>
		/// Retrieves information about a specified template bundle previously registered by the requesting application. 
		/// </summary>
		/// <param name="template_bundle_id">The template bundle ID used to identify a previously registered template bundle. The ID is the one returned by a previous call to feed.registerTemplateBundle. </param>
		/// <returns>Story Templates</returns>
		public template_bundle getRegisteredTemplateBundleByID(long template_bundle_id)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.feed.getRegisteredTemplateBundleByID" } };
			_api.AddRequiredParameter(parameterList, "template_bundle_id", template_bundle_id);

			var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? feed_getRegisteredTemplateBundleByID_response.Parse(response).Content : null;
		}

		/// <summary>
		/// Retrieves the full list of all the template bundles registered by the requesting application. The list does not include any of the template bundles previously deactivated via calls to feed.deactivateTemplateBundleByID. 
		/// </summary>
		/// <returns></returns>
		public IList<template_bundle> getRegisteredTemplateBundles()
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.feed.getRegisteredTemplateBundles" } };

			var response = _api.SendRequest(parameterList);
			return !string.IsNullOrEmpty(response) ? feed_getRegisteredTemplateBundles_response.Parse(response).template_bundle : null;
		}

        /// <summary>
        /// Builds a template bundle around the specified templates, registers them on Facebook, and responds with a template bundle ID that can be used to identify your template bundle to other Feed-related API calls. You need to register at least one bundle for each of your applications, if you have more than one.
        /// </summary>
        /// <param name="oneLineStoryTemplates"></param>
		/// <param name="fullStoryTemplate"></param>
		/// <param name="shortStoryTemplates"></param>
        public long  registerTemplateBundle(List<string> oneLineStoryTemplates, List<feedTemplate> shortStoryTemplates, feedTemplate fullStoryTemplate)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.feed.registerTemplateBundle" } };

            _api.AddJSONArray(parameterList, "one_line_story_templates", oneLineStoryTemplates);
            
            var list = new List<string>();
            foreach (var item in shortStoryTemplates)
            {
                var dict = new Dictionary<string, string>{
                    {"template_title", item.TemplateTitle},
                    {"template_body", item.TemplateBody},
                    {"preferred_layout", item.PreferredLayout}
                };
                list.Add(JSONHelper.ConvertToJSONAssociativeArray(dict));
            }
            _api.AddJSONArray(parameterList, "short_story_templates", list);

            var full_story_template = new Dictionary<string, string>();
            full_story_template.Add("template_title", fullStoryTemplate.TemplateTitle);
            full_story_template.Add("template_body", fullStoryTemplate.TemplateBody);
            _api.AddJSONAssociativeArray(parameterList, "full_story_template", full_story_template);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? feed_registerTemplateBundle_response.Parse(response).TypedValue : 0;

        }

		/// <summary>
		/// Different story sizes to use when publishing user actions to their feed.
		/// </summary>
		public enum PublishedStorySize
		{
			OneLine = 1,
			Short = 2,
			Full = 4
		}

		/// <summary>
		/// http://wiki.developers.facebook.com/index.php/Feed.publishUserAction
		/// Publishes a story on behalf of the user owning the session, using the specified template bundle. 
		/// An application can publish a maximum of 10 stories per user per day
		/// You can test your Feed templates using the Feed preview console (cf above wiki post).
		/// 
		/// Use JSONHelper.ConvertToJSONArray and/or JSONHelper.ConvertToJSONAssociativeArray to add 'subarrays' in template_data
		/// 
		/// Reserved tokens in template_data: 
		///     actor
		///     target
		///     
		/// Special tokens in template_data:
		///     images: array of image. image: src, (optional)href
		///     flash: swfsrc, imgsrc, (optional)expanded_width, (optional)expanded_height
		///     mp3: src, (optional)title, (optional)artist, (optional)album
		///     video: video_src, preview_img, (optional)video_title, (optional)video_link, (optional)video_type (default:application/x-shockwave-flash)
		///     
		/// </summary>
		/// <param name="template_bundle_id">The template bundle ID used to identify a previously registered template bundle. The ID is the one returned by a previous call to feed.registerTemplateBundle.</param>
		/// <param name="template_data">Optional - A JSON-encoded associative array of the values that should be substituted into the templates.</param>
		/// <param name="target_ids">Optional - list of IDs of friends of the actor, used for stories about a direct action between the actor and the targets of his or her action.</param>
		/// <param name="body_general">Optional - Additional markup that extends the body of a short story.</param>
		/// <param name="story_size">Can be 1 (one line, default), 2 (short) or 4 (full).</param>
		/// <returns>true if all succeeds, and false if the user never authorized the application to publish to his or her Wall.</returns>
		public bool publishUserAction(long template_bundle_id, Dictionary<string, string> template_data, List<long> target_ids, string body_general, PublishedStorySize story_size)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.feed.publishUserAction" } };
			_api.AddRequiredParameter(parameterList, "template_bundle_id", template_bundle_id);
			_api.AddJSONAssociativeArray(parameterList, "template_data", template_data);
			_api.AddList(parameterList, "target_ids", target_ids);
			_api.AddOptionalParameter(parameterList, "body_general", body_general);
			_api.AddOptionalParameter(parameterList, "story_size", (int)story_size);

			var response = _api.SendRequest(parameterList);
			return string.IsNullOrEmpty(response) || feed_publishUserAction_response.Parse(response).TypedValue;
		}

		
		/// <summary>
		/// Adds image, image link key value pairs to the request parameter list.
		/// </summary>
		/// <param name="dict"></param>
		/// <param name="images"></param>
		private static void AddFeedImages(IDictionary<string, string> dict, IEnumerable<feed_image> images)
		{
			if (Equals(images, null)) return;
			var i = 0;
			foreach(var image in images)
			{
				if (string.IsNullOrEmpty(image.image_url)) continue;
				dict.Add("image_" + (i + 1), image.image_url);
				dict.Add("image_" + (i + 1) + "_link", image.image_link_url);
				i++;
			}
		}
        #endregion
    }

	/// <summary>
	/// Contains the different parts of a Facebook feed template.
	/// </summary>
    public class feedTemplate
    {
		/// <summary>
		/// The title of the template
		/// </summary>
        public string TemplateTitle { get; set; }

		/// <summary>
		/// The body of the template.
		/// </summary>
        public string TemplateBody { get; set; }

		/// <summary>
		/// The preferred layout for the template.
		/// </summary>
        public string PreferredLayout { get; set; }

    }


}
