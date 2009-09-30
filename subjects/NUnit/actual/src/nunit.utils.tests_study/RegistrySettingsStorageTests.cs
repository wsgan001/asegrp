// ****************************************************************
// This is free software licensed under the NUnit license. You
// may obtain a copy of the license as well as information regarding
// copyright ownership at http://nunit.org/?p=license&r=2.4.
// ****************************************************************

using System;
using Microsoft.Win32;
//using NUnit.Framework;
//using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;
using NUnit.Framework;

namespace NUnit.Util.Tests
{
	//[PexClass] 
    [TestFixture]
    public partial class RegistrySettingsStorageTests
	{
		private static readonly string testKeyName = "Software\\NUnitTest";

		RegistryKey testKey;
		RegistrySettingsStorage storage;

		//[TestInitialize] 
        [SetUp]
		public void BeforeEachTest()
		{
			testKey = Registry.CurrentUser.CreateSubKey( testKeyName );
			storage = new RegistrySettingsStorage( testKey );
		}

		//[TestCleanup] 
        [TearDown]
		public void AfterEachTest()
		{
			NUnitRegistry.ClearKey( testKey );
			storage.Dispose();
		}

		[Test] //[PexMethod]
		public void StorageHasCorrectKey()
		{
			PexAssert.AreEqual( "HKEY_CURRENT_USER\\" + testKeyName.ToUpper(), storage.StorageKey.Name.ToUpper() );
		}

		[Test] //[PexMethod]
		public void SaveAndLoadSettings()
		{
			Assert.IsNull( storage.GetSetting( "X" ), "X is not null" );
			Assert.IsNull( storage.GetSetting( "NAME" ), "NAME is not null" );

			storage.SaveSetting("X", 5);
			storage.SaveSetting("NAME", "Charlie");

			Assert.AreEqual( 5, storage.GetSetting("X") );
			Assert.AreEqual( "Charlie", storage.GetSetting("NAME") );

			Assert.AreEqual( 5, testKey.GetValue( "X" ) );
			Assert.AreEqual( "Charlie", testKey.GetValue( "NAME" ) );
		}

		[Test] //[PexMethod]
		public void RemoveSettings()
		{
			storage.SaveSetting("X", 5);
			storage.SaveSetting("NAME", "Charlie");

			storage.RemoveSetting( "X" );
			Assert.IsNull( storage.GetSetting( "X" ), "X not removed" );
			Assert.AreEqual( "Charlie", storage.GetSetting( "NAME" ) );

			storage.RemoveSetting( "NAME" );
			Assert.IsNull( storage.GetSetting( "NAME" ), "NAME not removed" );
		}

		[Test] //[PexMethod]
		public void MakeSubStorages()
		{
			RegistrySettingsStorage sub1 = (RegistrySettingsStorage)storage.MakeChildStorage( "Sub1" );
			RegistrySettingsStorage sub2 = (RegistrySettingsStorage)storage.MakeChildStorage( "Sub2" );

			Assert.IsNotNull( sub1, "Sub1 is null" );
			Assert.IsNotNull( sub2, "Sub2 is null" );

            PexAssert.AreEqual("HKEY_CURRENT_USER\\" + testKeyName.ToUpper() + "\\Sub1".ToUpper(), sub1.StorageKey.Name.ToUpper());
            PexAssert.AreEqual("HKEY_CURRENT_USER\\" + testKeyName.ToUpper() + "\\Sub2".ToUpper(), sub2.StorageKey.Name.ToUpper());

/*			Microsoft.VisualStudio.TestTools.UnitTesting.StringAssert.AreEqualIgnoringCase( "HKEY_CURRENT_USER\\" + testKeyName + "\\Sub1", sub1.StorageKey.Name);
			StringAssert.AreEqualIgnoringCase( "HKEY_CURRENT_USER\\" + testKeyName + "\\Sub2", sub2.StorageKey.Name );*/
		}

		[Test] //[PexMethod]
		public void SubstorageSettings()
		{
			ISettingsStorage sub = storage.MakeChildStorage( "Sub" );

			sub.SaveSetting( "X", 5 );
			sub.SaveSetting( "NAME", "Charlie" );

			Assert.AreEqual( 5, sub.GetSetting( "X" ) );
			Assert.AreEqual( "Charlie", sub.GetSetting( "NAME" ) );

			sub.RemoveSetting( "X" );
			Assert.IsNull( sub.GetSetting( "X" ), "X not removed" );
			
			Assert.AreEqual( "Charlie", sub.GetSetting( "NAME" ) );

			sub.RemoveSetting( "NAME" );
			Assert.IsNull( sub.GetSetting( "NAME" ), "NAME not removed" );
		}

		[Test] //[PexMethod]
		public void TypeSafeSettings()
		{
			storage.SaveSetting( "X", 5);
			storage.SaveSetting( "Y", "17" );
			storage.SaveSetting( "NAME", "Charlie");

			Assert.AreEqual( 5, storage.GetSetting("X") );
			Assert.AreEqual( "17", storage.GetSetting( "Y" ) );
			Assert.AreEqual( "Charlie", storage.GetSetting( "NAME" ) );
		}
	}
}
