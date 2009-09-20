using facebook;
using NUnit.Framework;
using System.Collections.Generic;

namespace facebook.tests
{
    
    
    /// <summary>
    ///This is a test class for batchTest and is intended
    ///to contain all batchTest Unit Tests
    ///</summary>
    [TestFixture]
    public class batchTest
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
        ///A test for executeBatch
        ///</summary>
        [Test]
        public void executeBatchTest1()
        {
            API parent = _api;
            IList<object> actual;
            parent.batch.beginBatch();
            parent.friends.get();
            parent.marketplace.getCategories();
            actual = parent.batch.executeBatch();
            Assert.IsTrue(actual.Count>0);
        }

        /// <summary>
        ///A test for executeBatch
        ///</summary>
        [Test]
        public void executeBatchTest()
        {
            API parent = _api;
            IList<object> actual;
            parent.batch.beginBatch();
            parent.friends.get();
            parent.marketplace.getCategories();
            actual = parent.batch.executeBatch(true);
            Assert.IsTrue(actual.Count > 0);
        }
    }
}
