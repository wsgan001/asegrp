// ****************************************************************
// Copyright 2007, Charlie Poole
// This is free software licensed under the NUnit license. You may
// obtain a copy of the license at http://nunit.org/?p=license&r=2.4
// ****************************************************************

using System;
using NUnit.Core;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
//using NUnit.Tests.Assemblies;
//using NUnit.TestUtilities;
//using NUnit.Tests.Assemblies;

namespace NUnit.Util.Tests
{
    [PexClass(typeof(CategoryManager))]
    public partial class CategoryManagerTest_Pex
	{
		private CategoryManager categoryManager;

		[TestInitialize]
		public void CreateCategoryManager()
		{
			categoryManager = new CategoryManager();
		}

		[PexMethod]
		public void CanAddStringsWithoutDuplicating() 
		{
			categoryManager.Clear();
			string name1 = "Name1";
			string name2 = "Name2";
			string duplicate1 = "Name1";

			categoryManager.Add(name1);
			categoryManager.Add(name2);
			categoryManager.Add(duplicate1);

			Assert.AreEqual(2, categoryManager.Categories.Count);
		}


        [PexMethod]
        public void CanAddStrings()
        {
            categoryManager.Add("one");
            categoryManager.Add("two");
            Assert.AreEqual(2, categoryManager.Categories.Count);
        }

        //both the cases covered in a single PUT
        [PexMethod]
        public void CanAddStringsPUT([PexAssumeUnderTest] String name1, [PexAssumeUnderTest]String name2)
        {
            categoryManager.Clear();
            PexAssume.IsTrue(name1.Length > 1);
            PexAssume.IsTrue(name2.Length > 1);
            //string name1 = "Name1";
           // string name2 = "Name2";
           // string duplicate1 = "Name1";

            categoryManager.Add(name1);
            categoryManager.Add(name2);
           // categoryManager.Add(duplicate1);
            int count = 2;
            if (name1.Equals(name2))
            {
                count = 1;
            }
            
            PexAssert.AreEqual(count, categoryManager.Categories.Count);
        }


        [PexMethod]
        public void CanClearEntries()
        {
            categoryManager.Add("one");
            categoryManager.Add("two");
            categoryManager.Clear();
            Assert.AreEqual(0, categoryManager.Categories.Count);
        }

        [PexMethod]
        public void CanClearEntriesPUT([PexAssumeUnderTest] String name1, [PexAssumeUnderTest]String name2)
        {
            PexAssume.IsFalse(name1.Equals(name2));
            categoryManager.Add(name1);
            categoryManager.Add(name2);
            categoryManager.Clear();
            PexAssert.AreEqual(0, categoryManager.Categories.Count);
        }

		[PexMethod]
		public void CanAddTestCategories()
		{
			TestSuiteBuilder builder = new TestSuiteBuilder();
			Test suite = builder.Build( new TestPackage( "mock-assembly.dll" ) );

            Test test = null;//TestFinder.Find( "MockTest3", suite );
			categoryManager.AddCategories( test );
			Assert.AreEqual( 2, categoryManager.Categories.Count );
		}


		[PexMethod]
		public void CanAddAllAvailableCategoriesInTestTree()
		{
			TestSuiteBuilder builder = new TestSuiteBuilder();
			Test suite = builder.Build( new TestPackage( "mock-assembly.dll" ) );
			
			categoryManager.AddAllCategories( suite );
			//Assert.AreEqual( MockAssembly.Categories, categoryManager.Categories.Count );
		}
	}
}
