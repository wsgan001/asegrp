using facebook;
using NUnit.Framework;
using System.Collections.Generic;
using facebook.Schema;
using System;


namespace facebook.tests
{
    
    
    /// <summary>
    ///This is a test class for dataTest and is intended
    ///to contain all dataTest Unit Tests
    ///</summary>
    [TestFixture]
    public class dataTest
    {


        

        #region Additional test attributes
        // 
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //[SetUp]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion


        private API _api;
        private long _objectID;
        private long _hashID;
        private long _id1;
        private long _id2;
        [SetUp]
        public void Initialize()
        {
            _api = new API
            {
                IsDesktopApplication = true,
                ApplicationKey = Constants.FBSamples_ApplicationKey,
                Secret = Constants.FBSamples_Secret,
                SessionKey = Constants.FBSamples_SessionKey,
                uid = Constants.FBSamples_UserId
            };
        }
        /// <summary>
        ///A test for setObjectProperty
        ///</summary>
        [Test]
        public void setObjectPropertyTest()
        {
            API parent = _api;
            data target = new data(parent);
            createObjectTest();
            long obj_id = _objectID;
            string prop_name = Constants.TestObjectPropertyName;
            string prop_value = Constants.TestPropertyValue;
            target.setObjectProperty(obj_id, prop_name, prop_value);
            var typ = target.getObject(_objectID,null);
            Assert.IsTrue(typ.Untyped.Value.IndexOf(prop_value) > 0);
        }

        /// <summary>
        ///A test for setUserPreference
        ///</summary>
        [Test]
        public void setUserPreferenceTest()
        {
            API parent = _api;
            data target = new data(parent); 
            int pref_id = 0; 
            string value = Constants.TestString1; 
            target.setUserPreference(pref_id, value);
            Assert.IsTrue(1 == 1); 
        }

        /// <summary>
        ///A test for setUserPreferences Known to fail as of 7/28/08.
        ///</summary>
        [Test]
        public void setUserPreferencesTest()
        {
            API parent = _api;
            data target = new data(parent);
            List<string> values = new List<string> { Constants.TestString2, Constants.TestString3}; 
            bool replace = true; 
            target.setUserPreferences(values, replace);
            //TODO: Figure out why there is no data coming back.  Could be a problem with the facebook api
            //for (int i = 0; i < 202; i++)
            //{
            //    var res = target.getUserPreference(i);
            //    System.Diagnostics.Debug.WriteLine(i.ToString()+ ": "+ res);
            //}
            string result1 = target.getUserPreference(0);

            Assert.AreEqual(Constants.TestString2, result1);
            string result2 = target.getUserPreference(1);
            Assert.AreEqual(Constants.TestString3, result2);
        }

        /// <summary>
        ///A test for undefineAssociation
        ///</summary>
        [Test]
        public void undefineAssociationTest()
        {
            defineAssociationTest();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestAssociationDefName;
            target.undefineAssociation(name);
            try
            {
                target.getAssociationDefinition(name);
            }
            catch (Utility.FacebookException e)
            {
                //If it's not there, the we've we've succesfully removed the assn.
                if (e.ErrorCode != 803)
                    throw e;
                //Return since we've passed
                return;
            }
            Assert.Fail("An exception should have been caught since the assn should have been removed");
        }

        /// <summary>
        ///A test for undefineObjectProperty
        ///</summary>
        [Test]
        public void undefineObjectPropertyTest()
        {
            defineObjectPropertyTest();
            API parent = _api;
            data target = new data(parent); 
            string obj_type = Constants.TestType;
            string prop_name = Constants.TestObjectPropertyName; 

            //Check to make sure we've got just one from out setup
            var types = parent.data.getObjectType(obj_type);
            Assert.AreEqual(types.Count, 1);

            //Should have been dropped at this point.
            target.undefineObjectProperty(obj_type, prop_name);
            types = parent.data.getObjectType(obj_type);
            Assert.AreEqual(types.Count,0);
        }

        /// <summary>
        ///A test for updateObject
        ///</summary>
        [Test]
        public void updateObjectTest()
        {
            API parent = _api;
            data target = new data(parent); 
            createObjectTest();
            long obj_id = _objectID;
            var properties = new Dictionary<string, string> { { Constants.TestObjectPropertyName, Constants.TestObjectPropertyNewName } };
            bool replace = true; 
            target.updateObject(obj_id, properties, replace);
            var newObject = target.getObject(obj_id, null);
            Assert.IsTrue(newObject.Untyped.Value.IndexOf(Constants.TestObjectPropertyNewName) > 0);
        }

        /// <summary>
        ///A test for setHashValue
        ///</summary>
        [Test]
        public void setHashValueTest()
        {
            createObjectTest();
            API parent = _api;
            data target = new data(parent);
            string obj_type = Constants.TestType;
            string key = Constants.TestHashKey;
            string value = Constants.TestHashValue;
            string prop_name = Constants.TestObjectPropertyName;
            long actual = target.setHashValue(obj_type, key, value, prop_name);
            _hashID = actual;
            //Assert.IsInstanceOfType(actual, typeof(long));
            Assert.IsTrue(actual>0);
            var ret = target.getHashValue(Constants.TestType, Constants.TestHashKey, Constants.TestObjectPropertyName);
            Assert.AreEqual(ret, Constants.TestHashValue);
        }

        /// <summary>
        ///A test for setCookie
        ///</summary>
        [Test]
        public void setCookieTest()
        {
            API parent = _api;
            data target = new data(parent);
            long uid = Constants.FBSamples_UserId;
            string cookieName = Constants.TestString1;
            string value = Constants.TestString2;
            Nullable<DateTime> expires = null;
            string path = string.Empty; 
            bool expected = true; 
            bool actual;
            actual = target.setCookie(uid, cookieName, value, expires, path);
            Assert.AreEqual(expected, actual);
            
        }

        /// <summary>
        ///A test for setAssociations
        ///</summary>
        [Test]
        public void setAssociationsTest()
        {
            //Does't work right now:
            //Invalid parameter: associations.	
            Get2ObjectIDs();
            API parent = _api;
            data target = new data(parent);
            List<DataAssociation> assocs = new List<DataAssociation>
                                               {
                                                   new DataAssociation
                                                       {
                                                           assoc_time = DateTime.Now,
                                                           data = Constants.TestAssociationData1,
                                                           id1 = _id1,
                                                           id2 = _id2,
                                                           name = Constants.TestAssociationDefName
                                                       },
                                                   new DataAssociation
                                                       {
                                                           assoc_time = DateTime.Now,
                                                           data = Constants.TestAssociationData2,
                                                           id1 = _id1,
                                                           id2 = _id2,
                                                           name = Constants.TestAssociationDefName
                                                       }
                                               };
            string name = Constants.TestAssociationName; 
            target.setAssociations(assocs, name);
        }

        /// <summary>
        ///A test for setAssociation
        ///</summary>
        [Test]
        public void setAssociationTest()
        {
            Get2ObjectIDs();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestType;
            string data = Constants.TestAssociationData1;
            DateTime assoc_time = DateTime.Now;
            target.setAssociation(name, _id1, _id2, data, assoc_time);
        }

        private void Get2ObjectIDs()
        {
            createObjectTest();
            _id1 = _objectID;
            createObjectTest();
            _id2 = _objectID;
        }

        /// <summary>
        ///A test for renameObjectType
        ///</summary>
        [Test]
        public void renameObjectTypeTest()
        {
            createObjectTypeTest();
            API parent = _api;
            data target = new data(parent); 
            string obj_type = Constants.TestType; 
            string new_name = Constants.TestTypeRename; 
            target.renameObjectType(obj_type, new_name);
            var types = parent.data.getObjectTypes();
            foreach(var typ in types)
            {
                Assert.AreEqual(typ.name,new_name);
            }
        }

        /// <summary>
        ///A test for renameObjectProperty
        ///</summary>
        [Test]
        public void renameObjectPropertyTest()
        {
            defineObjectPropertyTest();
            API parent = _api;
            data target = new data(parent); 
            string obj_type = Constants.TestType; 
            string prop_name = Constants.TestObjectPropertyName; 
            string new_name = Constants.TestObjectPropertyNewName; 
            target.renameObjectProperty(obj_type, prop_name, new_name);
            var typ = target.getObjectType(obj_type);
            Assert.AreEqual(typ[0].name, Constants.TestObjectPropertyNewName);
        }

        /// <summary>
        ///A test for renameAssociation
        ///</summary>
        [Test]
        public void renameAssociationTest()
        {
            API parent = _api;
            data target = new data(parent); 
            string name = string.Empty; // TODO: Initialize to an appropriate value
            string new_name = string.Empty; // TODO: Initialize to an appropriate value
            string new_alias1 = string.Empty; // TODO: Initialize to an appropriate value
            string new_alias2 = string.Empty; // TODO: Initialize to an appropriate value
            target.renameAssociation(name, new_name, new_alias1, new_alias2);
            //Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for removeHashKeys
        ///</summary>
        [Test]
        public void removeHashKeysTest()
        {
            //test currently fails with the error:
            //A database error occurred. Please try again: unable to lookup hash keys.	removeHashKeysTest	
            setHashValueTest();
            API parent = _api;
            data target = new data(parent);
            string obj_type = Constants.TestType;
            List<string> keys = new List<string>{_hashID.ToString()};
            target.removeHashKeys(obj_type, keys);
            var ret = target.getHashValue(Constants.TestType, Constants.TestHashKey, Constants.TestObjectPropertyName);
            Assert.AreEqual(ret, string.Empty);
        }

        /// <summary>
        ///A test for removeHashKey
        ///</summary>
        [Test]
        public void removeHashKeyTest()
        {
            setHashValueTest();
            API parent = _api;
            data target = new data(parent);
            string obj_type = Constants.TestType;
            string key = Constants.TestHashKey;
            target.removeHashKey(obj_type, key);
            var ret = target.getHashValue(Constants.TestType, Constants.TestHashKey, Constants.TestObjectPropertyName);
            Assert.AreEqual(ret, string.Empty);

        }

        /// <summary>
        ///A test for removeAssociations
        ///</summary>
        [Test]
        public void removeAssociationsTest()
        {
            //No DataAssociation object is provided by the XSD, so this will not work.
            API parent = _api;
            data target = new data(parent); 
            List<DataAssociation> assocs = null; // TODO: Initialize to an appropriate value
            string name = string.Empty; // TODO: Initialize to an appropriate value
            target.removeAssociations(assocs, name);
            //Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for removeAssociation
        ///</summary>
        [Test]
        public void removeAssociationTest()
        {
            setAssociationTest();
            API parent = _api;
            data target = new data(parent); 
            string name = Constants.TestType;
            target.removeAssociation(name, _id1, _id2);
        }

        /// <summary>
        ///A test for removeAssociatedObjects
        ///</summary>
        [Test]
        public void removeAssociatedObjectsTest()
        {
            setAssociationTest();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestType;
            long obj_id = _id1;
            target.removeAssociatedObjects(name, obj_id);
        }   

        /// <summary>
        ///A test for incHashValue
        ///</summary>
        [Test]
        public void incHashValueTest()
        {
            setHashValueTest();
            API parent = _api;
            data target = new data(parent);
            string obj_type = Constants.TestType;
            string key = Constants.TestHashKey;
            string prop_name = Constants.TestObjectPropertyName;
            int increment = 2; 
            long expected = long.Parse(Constants.TestHashValue) + increment;
            long actual;
            actual = target.incHashValue(obj_type, key, prop_name,increment);
            Assert.AreEqual(expected, actual);
            
        }

        /// <summary>
        ///A test for getUserPreference
        ///</summary>
        [Test]
        public void getUserPreferenceTest()
        {
            setUserPreferenceTest();
            API parent = _api;
            data target = new data(parent); 
            int pref_id = 0; 
            string expected = Constants.TestString1; 
            string actual;
            actual = target.getUserPreference(pref_id);
            Assert.AreEqual(expected, actual);
        }

     

        /// <summary>
        ///A test for getObjectType
        ///</summary>
        [Test]
        public void getObjectTypeTest()
        {
            defineObjectPropertyTest();
            API parent = _api;
            data target = new data(parent); 
            string obj_type = Constants.TestType;
            IList<object_property_info> expected = new List<object_property_info>
                                                       {
                                                           new object_property_info
                                                               {
                                                                   name = Constants.TestObjectPropertyName,
                                                                   data_type = 2,
                                                                   index_type = 0
                                                               }
                                                       };
            IList<object_property_info> actual;
            actual = target.getObjectType(obj_type);
            Assert.AreEqual(expected[0].name, actual[0].name);
            Assert.AreEqual(expected[0].data_type, actual[0].data_type);
            Assert.AreEqual(expected[0].index_type, actual[0].index_type);
        }

        /// <summary>
        ///A test for getObjects
        ///</summary>
        [Test]
        public void getObjectsTest()
        {
            //Currently get the following exception:
            //A database error occurred. Please try again: unable to fetch all objects.	
            API parent = _api;
            data target = new data(parent);
            createObjectTest();
            long id1 = _objectID;
            createObjectTest();
            long id2 = _objectID;
            List<long> obj_ids = new List<long> {id1, id2};
            target.getObjects(obj_ids, null);
        }

        /// <summary>
        ///A test for getObject
        ///</summary>
        [Test]
        public void getObjectTest()
        {
            API parent = _api;
            data target = new data(parent);
            createObjectTest();
            long obj_id = _objectID;
            List<string> prop_names = null; 
            data_getObject_response expected = null;
            data_getObject_response actual;
        }

        /// <summary>
        ///A test for getHashValue
        ///</summary>
        [Test]
        public void getHashValueTest()
        {
            setHashValueTest();
            API parent = _api;
            data target = new data(parent);
            string obj_type = Constants.TestType;
            string key = Constants.TestHashKey;
            string prop_name = Constants.TestObjectPropertyName;
            string expected = Constants.TestHashValue; 
            string actual;
            actual = target.getHashValue(obj_type, key, prop_name);
            Assert.AreEqual(expected, actual);
        }

        /// <summary>
        ///A test for getCookies
        ///</summary>
        [Test]
        public void getCookiesTest2()
        {
            API parent = _api;
            data target = new data(parent); 
            long uid = 0; // TODO: Initialize to an appropriate value
            IList<cookie> expected = null; // TODO: Initialize to an appropriate value
            IList<cookie> actual;
            actual = target.getCookies(uid);
            Assert.AreEqual(expected, actual);
            
        }

        /// <summary>
        ///A test for getCookies
        ///</summary>
        [Test]
        public void getCookiesTest1()
        {
            API parent = _api;
            data target = new data(parent); 
            long uid = 0; // TODO: Initialize to an appropriate value
            string cookieName = string.Empty; // TODO: Initialize to an appropriate value
            IList<cookie> expected = null; // TODO: Initialize to an appropriate value
            IList<cookie> actual;
            actual = target.getCookies(uid, cookieName);
            Assert.AreEqual(expected, actual);
            
        }

        /// <summary>
        ///A test for getCookies
        ///</summary>
        [Test]
        public void getCookiesTest()
        {
            API parent = _api;
            data target = new data(parent); 
            IList<cookie> expected = null; // TODO: Initialize to an appropriate value
            IList<cookie> actual;
            actual = target.getCookies();
            Assert.AreEqual(expected, actual);
            
        }

        /// <summary>
        ///A test for getAssociations
        ///</summary>
        [Test]
        public void getAssociationsTest()
        {
            setAssociationTest();
            API parent = _api;
            data target = new data(parent);
            long obj_id1 = _id1;
            long obj_id2 = _id2;
            bool no_data = false;
            data_getAssociations_response actual;
            actual = target.getAssociations(obj_id1, obj_id2, no_data);
            Assert.AreEqual(2, actual.object_association.Count); //Should be 2 assn's
            
        }

        /// <summary>
        ///A test for getAssociationDefinitions
        ///</summary>
        [Test]
        public void getAssociationDefinitionsTest()
        {
            defineAssociationTest();
            API parent = _api;
            data target = new data(parent);
            var resps = target.getAssociationDefinitions();
            foreach(var resp in resps.object_assoc_info)
            {
                //Doesn't work right now... parsing from FB doesn't work.  You can see the values in the untyped value
                Assert.AreEqual(resp.assoc_info1.alias, Constants.TestAssociationDefAlias1);
                Assert.AreEqual(resp.assoc_info2.alias, Constants.TestAssociationDefAlias2);
            }
        }

        /// <summary>
        ///A test for getAssociationDefinition
        ///</summary>
        [Test]
        public void getAssociationDefinitionTest()
        {
            defineAssociationTest();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestAssociationDefName; 
            var resp = target.getAssociationDefinition(name);
            //Doesn't work right now... parsing from FB doesn't work because we don't have the new XSD.  
            //You can see the values in the untyped value.
            Assert.AreEqual(resp.assoc_info1.alias, Constants.TestAssociationDefAlias1);
            Assert.AreEqual(resp.assoc_info2.alias, Constants.TestAssociationDefAlias2);
            
        }

        /// <summary>
        ///A test for getAssociatedObjects
        ///</summary>
        [Test]
        public void getAssociatedObjectsTest()
        {
            setAssociationTest();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestType;
            long obj_id = _id1; 
            bool no_data = false; 
            data_getAssociatedObjects_response actual;
            actual = target.getAssociatedObjects(name, obj_id, no_data);
            Assert.AreEqual(actual.object_association[0].id2,_id2);
        }

        /// <summary>
        ///A test for getAssociatedObjectCounts
        ///</summary>
        [Test]
        public void getAssociatedObjectCountsTest()
        {
            setAssociationTest();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestType;
            List<long> obj_ids = new List<long>{_id1,_id2};
            IList<int> actual;
            actual = target.getAssociatedObjectCounts(name, obj_ids);
            Assert.AreEqual(1, actual[0]);// Don't know why there are 0 being returned by facebook.  There should be 
            Assert.AreEqual(1, actual[1]); 
        }

        /// <summary>
        ///A test for getAssociatedObjectCount
        ///</summary>
        [Test]
        public void getAssociatedObjectCountTest()
        {
            
            setAssociationTest();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestType;
            long obj_id = _id2;
            int expected = 1;  // //Test is known to fail as of 8/10.  Don't know why there are 0 being returned by facebook.  It should be 1.
            int actual;
            actual = target.getAssociatedObjectCount(name, obj_id);
            Assert.AreEqual(expected, actual);
        }

        /// <summary>
        ///A test for dropObjectType
        ///</summary>
        [Test]
        public void dropObjectTypeTest()
        {
            var parent = _api;
            var target = new data(parent);
            var existingTypes = target.getObjectTypes();
            foreach (var typ in existingTypes)
            {
                parent.data.dropObjectType(typ.name);
            }
        }

        /// <summary>
        ///A test for deleteObjects
        ///</summary>
        [Test]
        public void deleteObjectsTest()
        {
            //Facebook currently throws the current exception:
            //facebook.Utility.FacebookException: A database error occurred. 
            //Please try again: unable to fetch objects.	

            API parent = _api;
            data target = new data(parent);
            createObjectTest();
            long value1 = _objectID;
            createObjectTest();
            long value2 = _objectID;
            List<long> obj_ids = new List<long> { value1, value2 };
            target.deleteObjects(obj_ids);
        }

        /// <summary>
        ///A test for deleteObject
        ///</summary>
        [Test]
        public void deleteObjectTest()
        {
            API parent = _api;
            data target = new data(parent);
            createObjectTest();
            long obj_id = _objectID;
            target.deleteObject(obj_id);
        }

        /// <summary>
        ///A test for defineObjectProperty
        ///</summary>
        [Test]
        public void defineObjectPropertyTest()
        {
            createObjectTypeTest();
            API parent = _api;
            data target = new data(parent); 
            string obj_type = Constants.TestType; 
            string prop_name = Constants.TestObjectPropertyName; 
            int prop_type = 1; 
            target.defineObjectProperty(obj_type, prop_name, prop_type);
        }

        /// <summary>
        ///A test for defineAssociation
        ///</summary>
        [Test]
        public void defineAssociationTest()
        {
            DropAssociations();
            createObjectTypeTest();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestAssociationDefName;
            int assoc_type = 2;
            assoc_object_type assoc_info1 = new assoc_object_type
                                                {
                                                    alias = Constants.TestAssociationDefAlias1,
                                                    object_type = Constants.TestType
                                                };
            assoc_object_type assoc_info2 = new assoc_object_type
                                                {
                                                    alias = Constants.TestAssociationDefAlias2,
                                                    object_type = Constants.TestType
                                                };
            bool? inverse = null;
            target.defineAssociation(name, assoc_type, assoc_info1, assoc_info2, inverse);

        }
        

        private void DropAssociations()
        {
            try
            {
                _api.data.undefineAssociation(Constants.TestAssociationDefName);
            }
            catch(Utility.FacebookException e)
            {
                //If it's not there, we're ok.
                if (e.ErrorCode != 803)
                    throw e;
            }
        }

        /// <summary>
        ///A test for createObject
        ///</summary>
        [Test]
        public void createObjectTest()
        {
            defineObjectPropertyTest();
            API parent = _api;
            data target = new data(parent); 
            string obj_type = Constants.TestType;
            var properties = new Dictionary<string, string>
                                 {
                                     {
                                         Constants.TestObjectPropertyName,
                                         Constants.TestPropertyValue
                                         }
                                 };
            var ret = target.createObject(obj_type, null);
            Assert.IsNotNull(ret);
            ret = target.createObject(obj_type, properties);
            _objectID = ret;
            Assert.IsNotNull(ret);
        }


        /// <summary>
        ///A test for createObjectType
        ///</summary>
        [Test]
        public void createObjectTypeTest()
        {
            dropObjectTypeTest();
            API parent = _api;
            data target = new data(parent);
            string name = Constants.TestType; 
            target.createObjectType(name);
            var existingTypes = target.getObjectTypes();
            foreach (var typ in existingTypes)
            {
                Assert.AreEqual(typ.name,name);
            }
        }

        /// <summary>
        ///A test for getObjectProperty
        ///</summary>
        [Test]
        public void getObjectPropertyTest()
        {
            API parent = _api;
            data target = new data(parent);
            createObjectTest();
            long obj_id = _objectID;
            string prop_name = Constants.TestObjectPropertyName;
            string expected = Constants.TestPropertyValue;
            string actual;
            actual = target.getObjectProperty(obj_id, prop_name);
            Assert.AreEqual(expected, actual);
        }
    }
}
