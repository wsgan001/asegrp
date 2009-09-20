using System.Collections.Generic;
using facebook.Schema;
using System;
using facebook.Utility;

namespace facebook
{
	/// <summary>
	/// Facebook data api methods.
	/// </summary>
	public class data
	{
		private readonly API _api;

		/// <summary>
		/// Public constructor for facebook.data
		/// </summary>
		/// <param name="parent">Needs a connected API object for making requests</param>
		public data(API parent)
		{
			_api = parent;
		}

		/// <summary>
		/// Sets currently authenticated user's preference. Each preference is a string of maximum 128 characters and each of them has a numeric identifier ranged from 0 to 200. Therefore, every application can store up to 201 string values for each of its users.
		/// </summary>
		/// <param name="pref_id">(0-201) Numeric identifier of this preference.</param>
		/// <param name="value">(max. 128 characters) Value of the preference to set. Set it to "0" or "" to remove this preference.</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		/// <remarks>To "remove" a preference, set it to 0 or empty string. Both "0" and "" are considered as "not present", and getPreference() call will not return them. To tell them from each other, one can use some serialization format. For example, "n:0" for zeros and "s:" for empty strings. </remarks>
		public void setUserPreference(int pref_id, string value)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.setUserPreference" } };
			_api.AddRequiredParameter(parameterList, "pref_id", pref_id);
			_api.AddRequiredParameter(parameterList, "value", value);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Sets currently authenticated user's preferences in batch. Each preference is a string of maximum 128 characters and each of them has a numeric identifier ranged from 0 to 200. Therefore, every application can store up to 201 string values for each of its user. 
		/// </summary>
		/// <param name="values">Id-value pairs of preferences to set. Each id is an integer between 0 and 200 inclusively. Each value is a string with maximum length of 128 characters. Use "0" or "" to remove a preference.</param>
		/// <param name="replace">True to replace all existing preferences of this user; false to merge into existing preferences.</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		/// <remarks>To "remove" a preference, set it to 0 or empty string. Both "0" and "" are considered as "not present", and getPreference() call will not return them. To tell them from each other, one can use some serialization format. For example, "n:0" for zeros and "s:" for empty strings. </remarks>
		public void setUserPreferences(List<string> values, bool replace)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.setUserPreferences" } };
			_api.AddList(parameterList, "values", values);
			_api.AddParameter(parameterList, "replace", replace);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Gets currently authenticated user's preference. 
		/// </summary>
		/// <param name="pref_id">(0-200) Numeric identifier of the preference to get. </param>
		/// <returns>string: value of the specified preference. Empty string if the preference was not set, or it was set to "0" or empty string before.</returns>
		public string getUserPreference(int pref_id)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getUserPreference" } };
			_api.AddRequiredParameter(parameterList, "pref_id", pref_id);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getUserPreference_response.Parse(response).TypedValue : null;
		}

		/// <summary>
		/// An object type is like a "table" in SQL terminology, or a "class" in object-oriented programming concepts. Each object type has a unique human-readable "name" that will be used to identify itself throughout the API. Each object type also has a list of properties that one has to define individually. Each property is like a "column" in an SQL table, or a "data member" in an object class. 
		/// </summary>
		/// <param name="name">Name of this new object type. This name needs to be unique among all object types and associations defined for this application. This name also needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void createObjectType(string name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.createObjectType" } };
			_api.AddRequiredParameter(parameterList, "name", name);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Remove a previously defined object type. This will also delete ALL objects of this type. This deletion is NOT reversible. 
		/// </summary>
		/// <param name="obj_type">Name of the object type to delete. This will also delete all objects that were created with the type. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void dropObjectType(string obj_type)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.dropObjectType" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Rename a previously defined object type. 
		/// </summary>
		/// <param name="obj_type">Previous name of the object type to rename. </param>
		/// <param name="new_name">New name to use. This name needs to be unique among all object types and associations defined for this application. This name also needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores.</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void renameObjectType(string obj_type, string new_name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.renameObjectType" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddRequiredParameter(parameterList, "new_name", new_name);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Add a new object property to an object type.
		/// </summary>
		/// <param name="obj_type">Object type to add a new property to. </param>
		/// <param name="prop_name">Name of the new property to add. This name needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores. </param>
		/// <param name="prop_type">Type of the new property: 1 for integer, 2 for string (max. 255 characters), 3 for text blob (max. 64kb)</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void defineObjectProperty(string obj_type, string prop_name, int prop_type)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.defineObjectProperty" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddRequiredParameter(parameterList, "prop_name", prop_name);
			_api.AddRequiredParameter(parameterList, "prop_type", prop_type);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Remove a previously defined property of an object type. This will remove ALL values of this property of ALL objects of this type. This removal is NOT reversible. 
		/// </summary>
		/// <param name="obj_type">Object type from which a property is removed. </param>
		/// <param name="prop_name">Name of the property to remove. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void undefineObjectProperty(string obj_type, string prop_name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.undefineObjectProperty" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddRequiredParameter(parameterList, "prop_name", prop_name);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Rename a previously defined object property. 
		/// </summary>
		/// <param name="obj_type">Object type of the property to rename. </param>
		/// <param name="prop_name">Name of the property to change. </param>
		/// <param name="new_name">	New name to use. This name needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void renameObjectProperty(string obj_type, string prop_name, string new_name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.renameObjectProperty" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddRequiredParameter(parameterList, "prop_name", prop_name);
			_api.AddRequiredParameter(parameterList, "new_name", new_name);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Get a list of all previously defined object types. 
		/// </summary>
		/// <returns>list of object type definitions, each of which has; name: name of object type; object_class: (reserved)</returns>
		public IList<object_type_info> getObjectTypes()
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getObjectTypes" } };

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getObjectTypes_response.Parse(response).object_type_info : null;
		}

		/// <summary>
		/// Get detailed definitions of an object type, including all its properties and their types. 
		/// </summary>
		/// <param name="obj_type">Object type to get definition about. </param>
		/// <returns>list of object property definitions, each of which has; name: name of property; data_type: type of property. 1 for integer, 2 for string (max. 255 characters), 3 for text blob (max. 64kb); index_type: (reserved)</returns>
		public IList<object_property_info> getObjectType(string obj_type)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getObjectType" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getObjectType_response.Parse(response).object_property_info : null;
		}
        
        /// <summary>
        /// Create a new object.
        /// </summary>
        /// <param name="obj_type">Specifies which type of new object to create. </param>
        /// <param name="properties">Optional - Name-value pairs of properties this new object has. The parameters must be JSON encoded with double quoted property and value, i.e. {"name":"value"} </param>
        /// <returns>64-bit integer: Numeric identifier (fbid) of newly created object. </returns>
        public long createObject(string obj_type, Dictionary<string,string> properties)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.data.createObject" } };
            _api.AddRequiredParameter(parameterList, "obj_type", obj_type);
            _api.AddJSONAssociativeArray(parameterList, "properties", properties);
            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_createObject_response.Parse(response).TypedValue : 0;
        }
        

		/// <summary>
		/// Update an object's properties. 
		/// </summary>
		/// <param name="obj_id">Numeric identifier (fbid) of the object to modify. </param>
		/// <param name="properties">Name-value pairs of new properties. </param>
		/// <param name="replace">True if replace all existing properties; false to merge into existing ones.</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void updateObject(long obj_id, Dictionary<string,string> properties, bool replace)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.updateObject" } };
			_api.AddRequiredParameter(parameterList, "obj_id", obj_id);
			_api.AddJSONAssociativeArray(parameterList, "properties", properties);
			parameterList.Add("replace", replace.ToString());

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Delete an object permanently. 
		/// </summary>
		/// <param name="obj_id">Numeric identifier (fbid) of the object to delete. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void deleteObject(long obj_id)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.deleteObject" } };
			_api.AddRequiredParameter(parameterList, "obj_id", obj_id);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Delete multiple objects permanently. 
		/// </summary>
		/// <param name="obj_ids">A list of 64-bit integers that are numeric identifiers (fbids) of objects to delete.</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void deleteObjects(List<long> obj_ids)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.deleteObjects" } };
			_api.AddList(parameterList, "obj_ids", obj_ids);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Get an object's properties.
		/// </summary>
		/// <param name="obj_id">Numeric identifier (fbid) of the object to query.</param>
		/// <param name="prop_names">Optional - A list of property names (strings) to selectively query a subset of object properties. If not specified, all properties will be returned.</param>
		/// <returns>An array of the values only (not the names) of specified properties of the object. </returns>
		/// <remarks>The second (index 1) is the object id (fbid); after that they will be properties you added yourself.</remarks>
		public data_getObject_response getObject(long obj_id, List<string> prop_names)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getObject" } };
			_api.AddRequiredParameter(parameterList, "obj_id", obj_id);
			_api.AddList(parameterList, "prop_names", prop_names);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getObject_response.Parse(response) : null;
		}

		/// <summary>
		/// Get properties of multiple objects. 
		/// </summary>
		/// <param name="obj_ids">A list of 64-bit numeric identifiers (fbids) of objects to query. For example: [fbid1, fbid2] </param>
		/// <param name="prop_names">Optional - A list of property names (strings) to selectively query a subset of object properties. If not specified, all properties will be returned. </param>
		/// <returns>list of name-value pairs. </returns>
		public IList<container> getObjects(List<long> obj_ids, List<string> prop_names)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getObjects" } };
			_api.AddList(parameterList, "obj_ids", obj_ids);
			_api.AddList(parameterList, "prop_names", prop_names);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getObjects_response.Parse(response).container : null;
		}

        /// <summary>
        /// Get properties of multiple objects. 
        /// </summary>
        /// <param name="obj_id">A list of 64-bit numeric identifiers (fbids) of objects to query. For example: [fbid1, fbid2] </param>
        /// <param name="prop_name">Optional - A list of property names (strings) to selectively query a subset of object properties. If not specified, all properties will be returned. </param>
        /// <returns>list of name-value pairs. </returns>
        public string getObjectProperty(long obj_id, string prop_name)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getObjectProperty" } };
            _api.AddRequiredParameter(parameterList, "obj_id", obj_id);
            _api.AddRequiredParameter(parameterList, "prop_name", prop_name);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getObjectProperty_response.Parse(response).TypedValue : null;
        }

		/// <summary>
		/// Set a single property of an object. 
		/// </summary>
		/// <param name="obj_id">Object's numeric identifier (fbid).</param>
		/// <param name="prop_name">Property's name.</param>
		/// <param name="prop_value">Property's value.</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void setObjectProperty(long obj_id, string prop_name, string prop_value)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.setObjectProperty" } };
			_api.AddRequiredParameter(parameterList, "obj_id", obj_id);
			_api.AddRequiredParameter(parameterList, "prop_name", prop_name);
			_api.AddRequiredParameter(parameterList, "prop_value", prop_value);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Get a property value by a hash key. 
		/// </summary>
		/// <param name="obj_type">Object's type. This is required, so that different object types may use the same hash keys for different objects. </param>
		/// <param name="key">Hash key (string object identifier) for locating the object. This is created by a call to Data.setHashValue. </param>
		/// <param name="prop_name">Name of the property to query. </param>
		/// <returns>string: property's value. Empty string will be returned (without any error), if object with the specified hash key was not found or created. </returns>
		public string getHashValue(string obj_type, string key, string prop_name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getHashValue" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddRequiredParameter(parameterList, "key", key);
			_api.AddRequiredParameter(parameterList, "prop_name", prop_name);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getHashValue_response.Parse(response).TypedValue : null;
		}

		/// <summary>
		/// Set a property value by a hash key. 
		/// </summary>
		/// <param name="obj_type">Object's type. This is required so that different object types can use the same hash keys for different objects. </param>
		/// <param name="key">Hash key. This is a unique string chosen by the user that can be used to refer to the object in subsequent function calls. </param>
		/// <param name="value">Property's value to set. If the hash key exists, this will overwrite any previous value. </param>
		/// <param name="prop_name">Name of the property to set. </param>
		/// <returns>64-bit integer: Numeric identifier (fbid) of the object. </returns>
		public long setHashValue(string obj_type, string key, string value, string prop_name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.setHashValue" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddRequiredParameter(parameterList, "key", key);
			_api.AddRequiredParameter(parameterList, "value", value);
			_api.AddRequiredParameter(parameterList, "prop_name", prop_name);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_setHashValue_response.Parse(response).TypedValue : 0;
		}

		/// <summary>
		/// Atomically increases a numeric property by a hash key. This is different than "setHashValue(getHashValue() + increment)", which has two API functions calls that are not atomically done (subject to race conditions with values overwritten by interleaved API calls). 
		/// </summary>
		/// <param name="obj_type">Object's type. This is required, so that different object types may use the same hash keys for different objects. </param>
		/// <param name="key">Hash key (string object identifier) for locating the object. </param>
		/// <param name="prop_name">Name of the property to set. </param>
		/// <param name="increment">Optional - Default is 1. Increments to add to current value, which is 0 if object was not found or created. Use negative number for decrements. </param>
		/// <returns>32-bit integer: property's value after incremented. </returns>
		public long incHashValue(string obj_type, string key, string prop_name, int increment)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.incHashValue" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddRequiredParameter(parameterList, "key", key);
			_api.AddRequiredParameter(parameterList, "prop_name", prop_name);
			_api.AddRequiredParameter(parameterList, "increment", increment);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_incHashValue_response.Parse(response).TypedValue : 0;
		}

		/// <summary>
		/// Delete an object by a hash key. 
		/// </summary>
		/// <param name="obj_type">Object's type. This is required, so that different object types may use the same hash keys for different objects. </param>
		/// <param name="key">Hash key (string object identifier) to remove. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void removeHashKey(string obj_type, string key)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.removeHashKey" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddRequiredParameter(parameterList, "key", key);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Delete multiple objects by a list of hash keys. 
		/// </summary>
		/// <param name="obj_type">Object's type. This is required, so that different object types may use the same hash keys for different objects. </param>
		/// <param name="keys">A list of hash keys (string object identifier) to remove.</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void removeHashKeys(string obj_type, List<string> keys)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.removeHashKeys" } };
			_api.AddRequiredParameter(parameterList, "obj_type", obj_type);
			_api.AddList(parameterList, "keys", keys);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// An object association is a directional relationship between two object identifiers. For example, Application Installation: user id => installed application ids; Marriage: husband => wife; Friendship: user id => friend user id; Gift: giver => receiver 
		/// Each association has at least 3 names that are required to describe it: name of the association itself: "installation", "marriage", "friendship", "gift". alias1, name of the first object identifier: "user id", "husband", "giver". alias2, name of the second object identifier: "application id", "wife", "friend user id", "receiver". 
		/// For some associations, we also need reverse direction for a lookup by the second object identifier. For examples, in "marriage" case, not only may we need to look up wife ids by husband ids, but we may also need to look up husband ids by wife ids. We call this a two-way association. Since "husband to wife" is not the same as "wife to husband", we call this a two-way asymmetric association.
		/// In some other two-way associations, backward association has the same meaning of forward association. For example, in "friendship", if "A is B's friend" then "B is A's friend" as well. We call these types of two-way associations symmetric. For a symmetric association, when "A to B" is added, we also add "B to A", so that a reverse lookup can find out exactly the same information. 
		/// </summary>
		/// <param name="name">Name of forward association to create. This name needs to be unique among all object types and associations defined for this application. This name also needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores. </param>
		/// <param name="assoc_type">Type of this association:
		/// 1: one-way association, where reverse lookup is not needed;
		/// 2: two-way symmetric association, where a backward association (B to A) is always created when a forward association (A to B) is created.
		/// 3: two-way asymmetric association, where a backward association (B to A) has different meaning than a forward association (A to B). </param>
		/// <param name="assoc_info1">Describes object identifier 1 in an association. This is a data structure that has:
		/// alias: name of object identifier 1. This alias needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores.
		/// object_type: Optional - object type of object identifier 1.
		/// unique: Optional - Default to false. Whether each unique object identifier 1 can only appear once in all associations of this type. </param>
		/// <param name="assoc_info2">Describes object identifier 2 in an association. This is a data structure that has:
		/// alias: name of object identifier 2. This alias needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores.
		/// object_type: Optional - object type of object identifier 2.
		/// unique: Optional - Default to false. Whether each unique object identifier 2 can only appear once in all associations of this type. </param>
		/// <param name="inverse">Optional - name of backward association, if it is two-way asymmetric. This name needs to be unique among all object types and associations defined for this application. This name also needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
        public void defineAssociation(string name, int assoc_type, assoc_object_type assoc_info1, assoc_object_type assoc_info2, bool? inverse)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.defineAssociation" } };
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddRequiredParameter(parameterList, "assoc_type", assoc_type);
			_api.AddRequiredParameter(parameterList, "assoc_info1", assoc_info1);
			_api.AddRequiredParameter(parameterList, "assoc_info2", assoc_info2);
            if (inverse!=null)
		        _api.AddParameter(parameterList, "inverse", (bool)inverse);
			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Remove a previously defined association. This will also delete this type of associations established between objects. This deletion is not reversible. 
		/// </summary>
		/// <param name="name">Name of the association to remove.</param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void undefineAssociation(string name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.undefineAssociation" } };
			_api.AddRequiredParameter(parameterList, "name", name);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Rename a previously defined association. Any renaming here only affects one direction. To change names and aliases for another direction, rename with the name of that direction of association. 
		/// </summary>
		/// <param name="name">Name of the association to change. </param>
		/// <param name="new_name">Optional - New name to use. This name needs to be unique among all object types and associations defined for this application. This name also needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores. </param>
		/// <param name="new_alias1">Optional - New alias for object identifier 1 to use. This alias needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores. </param>
		/// <param name="new_alias2">Optional - New alias for object identifier 2 to use. This alias needs to be a valid identifier, which is no longer than 32 characters, starting with a letter (a-z) and consisting of only small letters (a-z), numbers (0-9) and/or underscores. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void renameAssociation(string name, string new_name, string new_alias1, string new_alias2)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.renameAssociation" } };
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddRequiredParameter(parameterList, "new_name", new_name);
			_api.AddRequiredParameter(parameterList, "new_alias1", new_alias1);
			_api.AddRequiredParameter(parameterList, "new_alias2", new_alias2);


			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Get detailed definition of an association. 
		/// </summary>
		/// <param name="name">	Name of the association. </param>
		/// <returns>An object association information data structure that has:
		/// name: name of the association;
		/// * assoc_type: an integer indicating association's type:
		/// o 1: one-way association, where reverse lookup is not needed;
		/// o 2: two-way symmetric association, where a backward association (B to A) is always created when a forward association (A to B) is created.
		/// o 3: two-way asymmetric association, where a backward association (B to A) has different meaning than a forward association (A to B). 
		/// * assoc_info1: object identifier 1's information:
		/// o alias: name of object identifier 1.
		/// o object_type: Optional - object type of object identifier 1.
		/// o unique: Whether each unique object identifier 1 can only appear once in all associations of this type. 
		/// * assoc_info2: object identifier 2's information:
		/// o alias: name of object identifier 2.
		/// o object_type: Optional - object type of object identifier 2.
		/// o unique: Whether each unique object identifier 1 can only appear once in all associations of this type.
		/// </returns>
		public data_getAssociationDefinition_response getAssociationDefinition(string name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getAssociationDefinition" } };
			_api.AddRequiredParameter(parameterList, "name", name);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getAssociationDefinition_response.Parse(response) : null;
		}

		/// <summary>
		/// Get detailed definitions of all previously defined associations. 
		/// </summary>
		/// <returns>A list of object association information data structures, each of which has: 
		/// name: name of the association;
		/// * assoc_type: an integer indicating association's type:
		/// o 1: one-way association, where reverse lookup is not needed;
		/// o 2: two-way symmetric association, where a backward association (B to A) is always created when a forward association (A to B) is created.
		/// o 3: two-way asymmetric association, where a backward association (B to A) has different meaning than a forward association (A to B). 
		/// * assoc_info1: object identifier 1's information:
		/// o alias: name of object identifier 1.
		/// o object_type: Optional - object type of object identifier 1.
		/// o unique: Whether each unique object identifier 1 can only appear once in all associations of this type. 
		/// * assoc_info2: object identifier 2's information:
		/// o alias: name of object identifier 2.
		/// o object_type: Optional - object type of object identifier 2.
		/// o unique: Whether each unique object identifier 1 can only appear once in all associations of this type.
		/// </returns>
		public data_getAssociationDefinitions_response getAssociationDefinitions()
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getAssociationDefinitions" } };

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getAssociationDefinitions_response.Parse(response) : null;
		}

		/// <summary>
		/// Creates an association between two object identifiers. The order of these two identifiers matters, unless this is a symmetric two-way association. 
		/// </summary>
		/// <param name="name">Name of the association to set. </param>
		/// <param name="obj_id1">Object identifier 1. </param>
		/// <param name="obj_id2">Object identifier 2. </param>
		/// <param name="data">Optional - An arbitrary data (max. 255 characters) to store with this association. </param>
		/// <param name="assoc_time">Optional - Default to association creation time. A timestamp to store with this association. This timestamp is represented as number of seconds since the Unix Epoch (January 1 1970 00:00:00 GMT). </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void setAssociation(string name, long obj_id1, long obj_id2, string data, DateTime assoc_time)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.setAssociation" } };
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddRequiredParameter(parameterList, "obj_id1", obj_id1);
			_api.AddRequiredParameter(parameterList, "obj_id2", obj_id2);
			_api.AddRequiredParameter(parameterList, "data", data);
			_api.AddRequiredParameter(parameterList, "assoc_time", (double)DateHelper.ConvertDateToDouble(DateTime.Now));

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Creates a list of associations. 
		/// </summary>
		/// <param name="assocs">A list of associations to set. Each of them has:
		/// * name: string, optional - name of the association to set;
		/// * id1: int64, object identifier 1;
		/// * id2: int64, object identifier 2;
		/// * data: string, optional - an arbitrary data (max. 255 characters) to store with this association.
		/// * time: integer, optional - default to association creation time. A timestamp to store with this association. This timestamp is represented as number of seconds since the Unix Epoch (January 1 1970 00:00:00 GMT). </param>
		/// <param name="name">Optional - default association name if association name is not specified in the list. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void setAssociations(IList<DataAssociation> assocs, string name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.setAssociations" } };
            _api.AddRequiredParameter(parameterList, "assocs", assocs);
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Removes an association between two object identifiers. The order of these two identifiers matters, unless this is a symmetric two-way association. 
		/// </summary>
		/// <param name="name">Name of the association. </param>
		/// <param name="obj_id1">Object identifier 1. </param>
		/// <param name="obj_id2">Object identifier 2. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void removeAssociation(string name, long obj_id1, long obj_id2)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.removeAssociation" } };
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddRequiredParameter(parameterList, "obj_id1", obj_id1);
			_api.AddRequiredParameter(parameterList, "obj_id2", obj_id2);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Removes a list of associations. 
		/// </summary>
		/// <param name="assocs">A list of associations to remove. Each of them has:
		/// * name: string, optional - name of the association;
		/// * id1: int64, object identifier 1;
		/// * id2: int64, object identifier 2; </param>
		/// <param name="name">Optional - default association name if association name is not specified in the list. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void removeAssociations(List<DataAssociation> assocs, string name)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.removeAssociations" } };
			//_api.AddList(parameterList, "assocs", assocs);
			_api.AddRequiredParameter(parameterList, "name", name);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// The name of this function may be misleading, but it actually removes associations between any other objects and a specified object. Those other associated objects will NOT be removed or deleted. Only the associations will be broken and deleted. 
		/// </summary>
		/// <param name="name">Name of the association. </param>
		/// <param name="obj_id">Object identifier. </param>
		/// <returns>Empty response if succesful, error otherwise.</returns>
		public void removeAssociatedObjects(string name, long obj_id)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.removeAssociatedObjects" } };
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddRequiredParameter(parameterList, "obj_id", obj_id);

			_api.SendRequest(parameterList);
		}

		/// <summary>
		/// Returns a list of object ids that are associated with specified object. 
		/// </summary>
		/// <param name="name">Name of the association. </param>
		/// <param name="obj_id">Object identifier. </param>
		/// <param name="no_data">True if only return object identifiers; false to return data and time as well. </param>
		/// <returns>A list of objects associated with the given id, each of which has:
		/// * id2: object identifier 2;
		/// * data: arbitrary data stored with this association; and
		/// * time: association creation time or a timestamp stored with this association. </returns>
		public data_getAssociatedObjects_response getAssociatedObjects(string name, long obj_id, bool no_data)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getAssociatedObjects" } };
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddRequiredParameter(parameterList, "obj_id", obj_id);
			parameterList.Add("no_data", no_data.ToString());

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getAssociatedObjects_response.Parse(response) : null;
		}

		/// <summary>
		/// Returns count of object ids that are associated with specified object. This function takes constant time to return the count, regardless how many objects are associated. 
		/// </summary>
		/// <param name="name">Name of the association. </param>
		/// <param name="obj_id">Object identifier. </param>
		/// <returns>integer: count of associated objects of the specified object. Seems to be returning empty string for zero. </returns>
		public int getAssociatedObjectCount(string name, long obj_id)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getAssociatedObjectCount" } };
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddRequiredParameter(parameterList, "obj_id", obj_id);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getAssociatedObjectCount_response.Parse(response).TypedValue : 0;
		}

		/// <summary>
		/// Returns individual counts of object ids that are associated with a list of specified objects. This function takes constant time to return the counts, regardless how many objects are associated with each queried object. 
		/// </summary>
		/// <param name="name">	Name of the association. </param>
		/// <param name="obj_ids">A list of 64-bit numeric object identifiers. </param>
		/// <returns>A list of integers, each of which is count of associated objects of one object in the specified list. The order of counts match exactly with input list. </returns>
		public IList<int> getAssociatedObjectCounts(string name, List<long> obj_ids)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getAssociatedObjectCounts" } };
			_api.AddRequiredParameter(parameterList, "name", name);
			_api.AddList(parameterList, "obj_ids", obj_ids);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getAssociatedObjectCounts_response.Parse(response).data_getAssociatedObjectCounts_response_elt : null;
		}

		/// <summary>
		/// Get all associations between two object identifiers. 
		/// </summary>
		/// <param name="obj_id1">Object identifier 1. </param>
		/// <param name="obj_id2">Object identifier 2. </param>
		/// <param name="no_data">True if only return object identifiers; false to return data and time as well. </param>
		/// <returns>A list of associations, each of which has:
		/// * name: name of the association to set;
		/// * id1: object identifier 1;
		/// * id2: object identifier 2;
		/// * data: arbitrary information stored with this association; and
		/// * time: association creation time or a timestamp that was stored with this association. </returns>
		public data_getAssociations_response getAssociations(long obj_id1, long obj_id2, bool no_data)
		{
			var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getAssociations" } };
			_api.AddRequiredParameter(parameterList, "obj_id1", obj_id1);
			_api.AddRequiredParameter(parameterList, "obj_id2", obj_id2);
			parameterList.Add("no_data", no_data.ToString());

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? data_getAssociations_response.Parse(response) : null;
		}
        /// <summary>
        /// Get All Cookies for the logged user. 
        /// </summary>
        /// <returns>A list of cookies</returns>
        public IList<cookie> getCookies()
        {
            return getCookies(_api.uid, null);
        }
        /// <summary>
        /// Get All Cookies for the logged user. 
        /// </summary>
		/// <param name="uid">User id </param>
        /// <param name="no_data">True if only return object identifiers; false to return data and time as well. </param>
        /// <returns>A list of cookies</returns>
        public IList<cookie> getCookies(long uid)
        {
            return getCookies(uid, null);
        }
        /// <summary>
        /// Get Cookies for the specified user. 
        /// </summary>
        /// <param name="uid">User id </param>
        /// <param name="cookieName">Optional- Cookie name </param>
        /// <returns>A list of cookies</returns>
        public IList<cookie> getCookies(long uid, string cookieName)
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.data.getCookies" } };
            _api.AddRequiredParameter(parameterList, "uid", uid);
            _api.AddOptionalParameter(parameterList, "name", cookieName);

            var response = _api.SendRequest(parameterList);
            return !string.IsNullOrEmpty(response) ? Schema.data_getCookies_response.Parse(response).cookie : null;
        }
        /// <summary>
        /// Get Cookies for the specified user. 
        /// </summary>
        /// <param name="uid">User id </param>
        /// <param name="cookieName">Cookie name </param>
        /// <param name="value">cookie value </param>
        /// <param name="expires">Time stamp when the cookie should expire. If not specified, the cookie expires after 24 hours. (The time stamp can be longer than 24 hours and currently has no limit)</param>
        /// <param name="path">Path relative to the application's callback URL, with which the cookie should be associated. (default value is /) </param>
        /// <returns>true or false</returns>
        public bool setCookie(long uid, string cookieName,string value,DateTime? expires, string path )
        {
            var parameterList = new Dictionary<string, string> { { "method", "facebook.data.setCookie" } };
            _api.AddRequiredParameter(parameterList, "uid", uid);
            _api.AddRequiredParameter(parameterList, "name", cookieName);
            _api.AddRequiredParameter(parameterList, "value", value);
            _api.AddOptionalParameter(parameterList, "expires", DateHelper.ConvertDateToDouble(expires));
            _api.AddOptionalParameter(parameterList, "path", path);

            var response = _api.SendRequest(parameterList);
            return string.IsNullOrEmpty(response) || Schema.data_setCookie_response.Parse(response).TypedValue;
        }
    }

	/// <summary>
	/// Id-value pairs
	/// </summary>
	public class map
	{
	}

	

	/// <summary>
	/// Id-value pairs
	/// </summary>
	public class DataAssociation
	{
        /// <summary>
        /// Name of the association to set. 
        /// </summary>
	    public string name { get; set; }
        /// <summary>
        /// Object identifier 1. 
        /// </summary>
	    public double id1 { get; set; }
        /// <summary>
        /// Object identifier 2.
        /// </summary>
	    public double id2 { get; set; }
        /// <summary>
        /// Optional - An arbitrary data (max. 255 characters) to store with this association. 
        /// </summary>
	    public string data { get; set; }
        /// <summary>
        /// Optional - Default to association creation time. A timestamp to store with this association. 
        /// </summary>
        public DateTime assoc_time { get; set; }
	}
}
