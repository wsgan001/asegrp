using System;
using System.Collections.Generic;
using facebook.Utility;
using System.Xml.Linq;
using System.Linq;
using Microsoft.Xml.Schema.Linq;
using System.Reflection;

namespace facebook
{
	/// <summary>
	/// 
	/// </summary>
	public class batch
    {

        #region Constructor

        private readonly API _api;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="parent"></param>
		public batch(API parent)
		{
			_api = parent;
		}

		#endregion

		#region Facebook API
        private List<string> _callList = new List<string>();
        private bool _isActive;

		private string run(List<string> callList, bool isSerial)
		{

            var parameterList = new Dictionary<string, string> { { "method", "facebook.batch.run" } };
            _api.AddJSONArray(parameterList, "method_feed", callList);
            if (isSerial)
            {
                _api.AddRequiredParameter(parameterList, "serial_only", "1");
            }
			return _api.SendRequest(parameterList, true);
		}

        public void beginBatch()
        {
            _isActive = true;
        }

		/// <summary>
		/// Executes the current batch.
		/// </summary>
		/// <returns></returns>
        public IList<Object> executeBatch()
        {
            return executeBatch(false);
        }

        public IList<Object> executeBatch(bool isSerial)
        {
            IList<Object> ret = new List<Object>();
            _isActive = false;
            string response = this.run(_callList, isSerial);

            XDocument doc = XDocument.Parse(response);
            XNamespace xname = XNamespace.Get(facebook.Properties.Resources.FacebookNamespace);
            var responses = from element in doc.Descendants(xname + "batch_run_response_elt")
                    select element.Value;


            foreach (var r in responses)
            {
                object[] margs = new object[1];
                margs[0] = r;
                Type t = TypeHelper.getResponseObjectType(r);
                MethodInfo mi = t.GetMethod("Parse");
                ret.Add(mi.Invoke(null, margs));
            }
            _callList.Clear();
            if (ret.Count > 0)
            {
                return ret;
            }
            else
            {
                return null;
            }
        }

        public bool isActive
        {
            get { return _isActive; } 
        }
        public List<string> callList
        {
            get { return _callList; }
        }


		#endregion
	}
}