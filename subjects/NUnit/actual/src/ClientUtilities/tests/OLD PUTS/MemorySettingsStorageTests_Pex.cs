// ****************************************************************
// Copyright 2007, Charlie Poole
// This is free software licensed under the NUnit license. You may
// obtain a copy of the license at http://nunit.org/?p=license&r=2.4
// ****************************************************************

using System;
//using NUnit.Framework;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections;
using NUnit.Framework;

namespace NUnit.Util.PUTs
{
    [TestFixture]
    [PexClass]//(typeof(MemorySettingsStorage))]
    public partial class MemorySettingsStorageTests
	{
		MemorySettingsStorage storage;

		[TestInitialize] //[SetUp]
		public void Init()
		{
			storage = new MemorySettingsStorage();
		}

		[TestCleanup] //[TearDown]
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
        public void SaveAndLoadSettingsPUT1([PexAssumeUnderTest]MemorySettingsStorage storage1, [PexAssumeUnderTest] String settingName, [PexAssumeNotNull] Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(storage1.GetSetting(settingName) == null);
            storage1.SaveSetting(settingName, settingValue);
            PexAssume.AreEqual(settingValue, storage1.GetSetting(settingName));            
        }

        [PexMethod]
        public void RemoveSettingsPUT1([PexAssumeUnderTest]MemorySettingsStorage storage1, [PexAssumeUnderTest] String settingName, [PexAssumeNotNull] Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(storage1.GetSetting(settingName) == null);

            storage1.SaveSetting(settingName, settingValue);
            storage1.RemoveSetting(settingName);
            PexAssert.IsNull(storage1.GetSetting(settingName));            
        }

        [PexMethod]
        public void MakeSubStorages([PexAssumeUnderTest]MemorySettingsStorage storage1, [PexAssumeUnderTest]String subStorageName)
        {
            ISettingsStorage sub1 = storage1.MakeChildStorage(subStorageName);            
            PexAssert.IsNotNull(sub1);
        }


		
        [PexMethod]
        public void SubstorageSettingsPUT1([PexAssumeUnderTest]MemorySettingsStorage storage1, [PexAssumeUnderTest]String subStorageName, [PexAssumeUnderTest] String settingName, [PexAssumeNotNull] Object settingValue)
        {
            ISettingsStorage sub = storage1.MakeChildStorage(subStorageName);
            sub.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(settingValue, sub.GetSetting(settingName));

            sub.RemoveSetting(settingName);
            PexAssert.IsNull(sub.GetSetting(settingName));
        }

	}
}
