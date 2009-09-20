using System.Collections.Generic;
using System.ComponentModel;
using System.Text;
using facebook.Schema;

namespace facebook.Utility
{
    internal sealed class StringHelper
    {
        private StringHelper()
        {
        }

        /// <summary>
        /// Convert a collection of strings to a comma separated list.
        /// </summary>
        /// <param name="collection">The collection to convert to a comma separated list.</param>
        /// <returns>comma separated string.</returns>
        internal static string ConvertToCommaSeparated(IList<string> collection)
        {
            ///
            /// assumed that the average string length is 10 and double the buffer multiplying by 2
            /// if this does not fit in your case, please change the values
            /// 
            int preAllocation = collection.Count * 10 * 2;
            var sb = new StringBuilder(preAllocation);
            int i = 0;
            foreach (string key in collection)
            {
                sb.Append(key);
                if (i < collection.Count - 1)
                    sb.Append(",");

                i++;
            }
            return sb.ToString();
        }
        /// <summary>
        /// Convert a collection of strings to a comma separated list.
        /// </summary>
        /// <param name="collection">The collection to convert to a comma separated list.</param>
        /// <returns>comma separated string.</returns>
        internal static string ConvertToCommaSeparated(IList<int?> collection)
        {
            ///
            /// assumed that the average string length is 10 and double the buffer multiplying by 2
            /// if this does not fit in your case, please change the values
            /// 
            int preAllocation = collection.Count * 10 * 2;
            var sb = new StringBuilder(preAllocation);
            int i = 0;
            foreach (int? key in collection)
            {
                sb.Append(key.ToString());
                if (i < collection.Count - 1)
                    sb.Append(",");

                i++;
            }
            return sb.ToString();
        }
        /// <summary>
        /// Convert a collection of strings to a comma separated list.
        /// </summary>
        /// <param name="collection">The collection to convert to a comma separated list.</param>
        /// <returns>comma separated string.</returns>
        internal static string ConvertToCommaSeparated(IList<int> collection)
        {
            ///
            /// assumed that the average string length is 10 and double the buffer multiplying by 2
            /// if this does not fit in your case, please change the values
            /// 
            int preAllocation = collection.Count * 10 * 2;
            var sb = new StringBuilder(preAllocation);
            int i = 0;
            foreach (int key in collection)
            {
                sb.Append(key.ToString());
                if (i < collection.Count - 1)
                    sb.Append(",");

                i++;
            }
            return sb.ToString();
        }

        internal static string ConvertToCommaSeparated(IList<long> collection)
        {
            ///
            /// assumed that the average string length is 10 and double the buffer multiplying by 2
            /// if this does not fit in your case, please change the values
            /// 
            int preAllocation = collection.Count * 10 * 2;
            var sb = new StringBuilder(preAllocation);
            int i = 0;
            foreach (long key in collection)
            {
                sb.Append(key.ToString());
                if (i < collection.Count - 1)
                    sb.Append(",");

                i++;
            }
            return sb.ToString();
        }


    }
}
