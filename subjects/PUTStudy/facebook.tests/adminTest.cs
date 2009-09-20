using facebook;
using NUnit.Framework;
using System.Collections.Generic;
using System;
using facebook.Schema;

namespace facebook.tests
{
    
    
    /// <summary>
    ///This is a test class for adminTest and is intended
    ///to contain all adminTest Unit Tests
    ///</summary>
    [TestFixture]
    public class adminTest
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
        ///A test for setAppProperties
        ///</summary>
        [Test]
        public void setAppPropertiesTest()
        {
            //This stuff only works for canvas apps, and since I don't have an infinite session anymore
            //with canvas apps, I can't run this test...
            API parent = new API
            {
                ApplicationKey = Constants.FBSamples_WebApplicationKey,
                Secret = Constants.FBSamples_WebSecret,
                uid = Constants.FBSamples_UserId
            }; ;
            admin target = parent.admin;
            bool expected = true; // TODO: Initialize to an appropriate value
            bool actual;
            var dict = new Dictionary<string, string>
            {
                {"application_name","Sample IFrame App"}
            };
            actual = target.setAppProperties(dict);
            var props = target.getAppProperties(target.GetApplicationPropertyNames());
            Assert.AreEqual(expected, actual);
            Assert.IsNotNull(props);
            
        }

        /// <summary>
        ///A test for getMetrics
        ///</summary>
        [Test]
        public void getMetricsTest()
        {
            API parent = _api;
            admin target = new admin(parent); 
            
            DateTime startDate = DateTime.Now.Subtract(new TimeSpan(10, 0, 0, 0));
            DateTime endDate = DateTime.Now; 
            admin.Period period = admin.Period.Day; 
            IList<metrics> actual;
            actual = target.getMetrics(startDate, endDate, period);
            Assert.IsTrue(actual.Count>0);
            
        }

        /// <summary>
        ///A test for getAllocation
        ///</summary>
        [Test]
        public void getAllocationTest()
        {
            API parent = _api;
            admin target = new admin(parent);
            admin.IntegrationPointName name = admin.IntegrationPointName.requests_per_day; // TODO: Initialize to an appropriate value
            int actual;
            actual = target.getAllocation(name);
            Assert.IsTrue(actual>0);
            
        }
    }
}
