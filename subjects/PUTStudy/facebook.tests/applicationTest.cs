using facebook;
using NUnit.Framework;
using facebook.Schema;

namespace facebook.tests
{
    
    
    /// <summary>
    ///This is a test class for applicationTest and is intended
    ///to contain all applicationTest Unit Tests
    ///</summary>
    [TestFixture]
    public class applicationTest
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
        ///A test for getPublicInfo
        ///</summary>
        [Test]
        public void getPublicInfoTest1()
        {
            API parent = _api;
            application target = _api.application;
            app_info actual;
            actual = target.getPublicInfo();
            Assert.AreEqual(actual.api_key,Constants.FBSamples_ApplicationKey);
            Assert.IsNotNull(actual.display_name);
            
        }

        /// <summary>
        ///A test for getPublicInfo
        ///</summary>
        [Test]
        public void getPublicInfoTest()
        {
            API parent = _api;
            application target = _api.application;
            int application_id = 0; 
            string application_api_key = Constants.FBSamples_ApplicationKey; 
            string application_canvas_name = string.Empty; 
            app_info actual;
            actual = target.getPublicInfo(application_id, application_api_key, application_canvas_name);
            Assert.AreEqual(actual.api_key, Constants.FBSamples_ApplicationKey);
            Assert.IsNotNull(actual.display_name);
            
        }
    }
}
