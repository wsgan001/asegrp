// ****************************************************************
// Copyright 2007, Charlie Poole
// This is free software licensed under the NUnit license. You may
// obtain a copy of the license at http://nunit.org/?p=license&r=2.4
// ****************************************************************

using System;
using NUnit.Framework;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Graphs;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections;

namespace NUnit.Util.Tests
{
    [PexClass(typeof(MemorySettingsStorage))]
    public partial class MemorySettingsStorageTests
	{
		MemorySettingsStorage storage;

		[TestInitialize]
		public void Init()
		{
			storage = new MemorySettingsStorage();
		}

		[TestCleanup]
		public void Cleanup()
		{
			storage.Dispose();
		}

        //No need to generalize
		[PexMethod]
		public void MakeStorage()
		{
			PexAssert.IsNotNull( storage );
		}


		[PexMethod]
		public void SaveAndLoadSettings()
		{
			PexAssert.IsNull( storage.GetSetting( "X" ), "X is not null" );
			PexAssert.IsNull( storage.GetSetting( "NAME" ), "NAME is not null" );

			storage.SaveSetting("X", 5);
			storage.SaveSetting("NAME", "Charlie");

			PexAssert.AreEqual( 5, storage.GetSetting("X") );
			PexAssert.AreEqual( "Charlie", storage.GetSetting("NAME") );
		}

        [PexMethod]
        public void SaveAndLoadSettingsPUT1([PexAssumeUnderTest]MemorySettingsStorage storage1, [PexAssumeUnderTest] String settingName, [PexAssumeUnderTest] Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(storage1.GetSetting(settingName) == null);
            storage1.SaveSetting(settingName, settingValue);
            PexAssume.AreEqual(settingValue, storage1.GetSetting(settingName));            
        }


		[PexMethod]
        public void RemoveSettings()
		{
            storage.SaveSetting("X", 5);
            storage.SaveSetting("NAME", "Charlie");

            storage.RemoveSetting("X");
            PexAssert.IsNull(storage.GetSetting("X"), "X not removed");
            PexAssert.AreEqual("Charlie", storage.GetSetting("NAME"));

            storage.RemoveSetting("NAME");
            PexAssert.IsNull(storage.GetSetting("NAME"), "NAME not removed");
        }

        [PexMethod]
        public void RemoveSettingsPUT1([PexAssumeUnderTest]MemorySettingsStorage storage1, [PexAssumeUnderTest] String settingName, [PexAssumeUnderTest] Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(storage1.GetSetting(settingName) == null);

            storage1.SaveSetting(settingName, settingValue);
            storage1.RemoveSetting(settingName);
            PexAssert.IsNull(storage1.GetSetting(settingName));            
        }


		[PexMethod]
		public void MakeSubStorages()
		{
			ISettingsStorage sub1 = storage.MakeChildStorage( "Sub1" );
			ISettingsStorage sub2 = storage.MakeChildStorage( "Sub2" );

			PexAssert.IsNotNull( sub1, "Sub1 is null" );
			PexAssert.IsNotNull( sub2, "Sub2 is null" );
		}

        [PexMethod]
        public void MakeSubStorages([PexAssumeUnderTest]MemorySettingsStorage storage1, [PexAssumeUnderTest]String subStorageName)
        {
            ISettingsStorage sub1 = storage1.MakeChildStorage(subStorageName);            
            PexAssert.IsNotNull(sub1);
        }


		[PexMethod]
		public void SubstorageSettings()
		{
			ISettingsStorage sub = storage.MakeChildStorage( "Sub" );

			sub.SaveSetting( "X", 5 );
			sub.SaveSetting( "NAME", "Charlie" );

			PexAssert.AreEqual( 5, sub.GetSetting( "X" ) );
			PexAssert.AreEqual( "Charlie", sub.GetSetting( "NAME" ) );

			sub.RemoveSetting( "X" );
			PexAssert.IsNull( sub.GetSetting( "X" ), "X not removed" );
			
			PexAssert.AreEqual( "Charlie", sub.GetSetting( "NAME" ) );

			sub.RemoveSetting( "NAME" );
			PexAssert.IsNull( sub.GetSetting( "NAME" ), "NAME not removed" );
		}

        [PexMethod]
        public void SubstorageSettingsPUT1([PexAssumeUnderTest]MemorySettingsStorage storage1, [PexAssumeUnderTest]String subStorageName, [PexAssumeUnderTest] String settingName, [PexAssumeUnderTest] Object settingValue)
        {
            ISettingsStorage sub = storage1.MakeChildStorage(subStorageName);
            sub.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(settingValue, sub.GetSetting(settingName));

            sub.RemoveSetting(settingName);
            PexAssert.IsNull(sub.GetSetting(settingName));
        }


		[PexMethod]
		public void TypeSafeSettings()
		{
			storage.SaveSetting( "X", 5);
			storage.SaveSetting( "Y", "17" );
			storage.SaveSetting( "NAME", "Charlie");

			PexAssert.AreEqual( 5, storage.GetSetting("X") );
			PexAssert.AreEqual( "17", storage.GetSetting( "Y" ) );
			PexAssert.AreEqual( "Charlie", storage.GetSetting( "NAME" ) );
		}
	}
}
