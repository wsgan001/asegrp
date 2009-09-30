// ****************************************************************
// This is free software licensed under the NUnit license. You
// may obtain a copy of the license as well as information regarding
// copyright ownership at http://nunit.org/?p=license&r=2.4.
// ****************************************************************

using System;
using Microsoft.Win32;
//using NUnit.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;

namespace NUnit.Util.Tests
{
	[PexClass(typeof(RegistrySettingsStorage))]
    public partial class RegistrySettingsStorageTests_Pex
	{
		private static readonly string testKeyName = "Software\\NUnitTest";

		RegistryKey testKey;
		RegistrySettingsStorage storage;

		[TestInitialize]
		public void BeforeEachTest()
		{
			testKey = Registry.CurrentUser.CreateSubKey( testKeyName );
			storage = new RegistrySettingsStorage( testKey );
		}

		[TestCleanup]
		public void AfterEachTest()
		{
			NUnitRegistry.ClearKey( testKey );
			storage.Dispose();
		}

		[PexMethod]
		public void StorageHasCorrectKey()
		{
            String testVar = "HKEY_CURRENT_USER\\" + testKeyName;
			PexAssert.IsTrue(storage.StorageKey.Name.ToLower().Equals(testVar.ToLower()) );
		}

        //PHASE 2
       [PexMethod]
        public void StorageHasCorrectKeyPUT1([PexAssumeUnderTest] String testVar)
        {
            PexAssume.IsTrue(testVar.Contains("t"));
            RegistryKey key = Registry.CurrentUser;
            RegistryKey subkey = key.CreateSubKey(testVar);
            RegistrySettingsStorage storage = new RegistrySettingsStorage(subkey);
            testVar = "HKEY_CURRENT_USER\\" + testVar;
            PexAssert.IsTrue(storage.StorageKey.Name.ToLower().Equals(testVar.ToLower()));
        }

		[PexMethod]
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

        //PHASE 1
        [PexMethod]
        public void SaveAndLoadSettingsPUT1([PexAssumeUnderTest] String[] name, [PexAssumeUnderTest]Object[] value)
        {
            PexAssume.IsTrue(name.Length == value.Length);
            for (int i = 0; i < value.Length; i++)
            {
                PexAssume.IsTrue(value[i] is String || value[i] is int);
                PexAssume.IsNotNullOrEmpty(name[i]);
                PexAssume.IsNotNull(value[i]);
            }

            for (int i = 0; i < name.Length; i++)
            {
                storage.SaveSetting(name[i], value[i]);
            }
            for (int i = 0; i < name.Length; i++)
            {
                PexAssert.AreEqual(value[i], storage.GetSetting(name[i]));
                PexAssert.AreEqual(value[i], testKey.GetValue(name[i]));
            }

        }

        //PHASE 2
        [PexMethod]
        public void SaveAndLoadSettingsPUT2([PexAssumeUnderTest] String[] name, [PexAssumeUnderTest]Object[] value)
        {
            
            PexAssume.IsTrue(name.Length == value.Length);
            for (int i = 0; i < value.Length; i++)
            {
                PexAssume.IsTrue(value[i] is bool || value[i] is String || value[i] is int);
                PexAssume.IsNotNullOrEmpty(name[i]);
                PexAssume.IsNotNull(value[i]);
                if(value[i] is String)
                    PexAssume.IsTrue(name[i].Contains("t"));
            }

            for (int i = 0; i < name.Length-1; i++)
            {
                //Phase 2 - Not adding the last name so that we cover the test for subkey not existing and we are calling it
                storage.SaveSetting(name[i], value[i]);
            }
            for (int i = 0; i < name.Length-1; i++)
            {
                
                if (value[i] is bool)
                {
                    //Boolean currValue = value[i] as Boolean;
                    if (value[i].ToString().Equals("True"))
                    {
                        PexAssert.AreEqual(1, storage.GetSetting(name[i]));
                        PexAssert.AreEqual(1, testKey.GetValue(name[i]));
                    }
                    else
                    {
                        PexAssert.AreEqual(0, storage.GetSetting(name[i]));
                        PexAssert.AreEqual(0, testKey.GetValue(name[i]));
                    }
                }
                else
                {
                    PexAssert.AreEqual(value[i], storage.GetSetting(name[i]));
                    PexAssert.AreEqual(value[i], testKey.GetValue(name[i]));
                }
            }
            if(name.Length > 0)
                PexAssert.AreEqual(null, storage.GetSetting(name[name.Length - 1]));

        }

        [PexMethod]
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

        //PHASE 1
        [PexMethod]
        public void RemoveSettingsPUT1([PexAssumeUnderTest] String[] name, [PexAssumeUnderTest]Object[] value)
        {
            PexAssume.IsTrue(name.Length == value.Length);
            for (int i = 0; i < value.Length; i++)
            {
                PexAssume.IsTrue(value[i] is String || value[i] is int);
                PexAssume.IsNotNullOrEmpty(name[i]);
                PexAssume.IsNotNull(value[i]);
                //PexAssume.IsTrue(name[i].Contains("t"));

            }

            for (int i = 0; i < name.Length; i++)
            {
                storage.SaveSetting(name[i], value[i]);
            }
            for (int i = 0; i < name.Length; i++)
            {
                if (storage.GetSetting(name[i]) != null)
                {
                    storage.RemoveSetting(name[i]);
                    PexAssert.IsNull(storage.GetSetting(name[i]), name[i] + " not removed");
                }
            }
        }

        //PHASE 2
        [PexMethod]
         public void RemoveSettingsPUT2([PexAssumeUnderTest] String[] name,[PexAssumeUnderTest]Object[] value)
        {
            PexAssume.IsTrue(name.Length == value.Length);
            for (int i = 0; i < value.Length; i++)
            {
                PexAssume.IsTrue(value[i] is bool || value[i] is String || value[i] is int);
                PexAssume.IsNotNullOrEmpty(name[i]);
                PexAssume.IsNotNull(value[i]);
                //PexAssume.IsTrue(name[i].Contains("t"));
                
            }
 
            for (int i = 0; i < name.Length-1; i++)
            {
                storage.SaveSetting(name[i], value[i]);
            }
             for (int i = 0; i < name.Length-1; i++)
            {
                if(storage.GetSetting(name[i]) != null){
                    storage.RemoveSetting(name[i]);
                    PexAssert.IsNull(storage.GetSetting(name[i]), name[i]+" not removed");
                }
            }
             if (name.Length > 0)
                 PexAssert.AreEqual(null, storage.GetSetting(name[name.Length - 1]));
        }


		[PexMethod]
		public void MakeSubStorages()
		{
			RegistrySettingsStorage sub1 = (RegistrySettingsStorage)storage.MakeChildStorage( "Sub1" );
			RegistrySettingsStorage sub2 = (RegistrySettingsStorage)storage.MakeChildStorage( "Sub2" );

			Assert.IsNotNull( sub1, "Sub1 is null" );
			Assert.IsNotNull( sub2, "Sub2 is null" );

            String test1 = "HKEY_CURRENT_USER\\" + testKeyName+ "\\Sub1";
			PexAssert.IsTrue(sub1.StorageKey.Name.ToLower().Equals(test1.ToLower()));
            String test2 =  "HKEY_CURRENT_USER\\" + testKeyName + "\\Sub2";
            PexAssert.IsTrue(sub2.StorageKey.Name.ToLower().Equals(test2.ToLower()));
		}


        [PexMethod]
        public void MakeSubStoragesPUT1([PexAssumeUnderTest]String subName)
        {
            PexAssume.IsTrue(subName.Contains("t"));
            String test1 = "HKEY_CURRENT_USER\\" + testKeyName + "\\"+subName;
            RegistrySettingsStorage sub1 = (RegistrySettingsStorage)storage.MakeChildStorage(subName);
            Console.WriteLine(test1.ToLower());
            Console.WriteLine(sub1.StorageKey.Name.ToLower());
            PexAssert.IsNotNull(sub1, "Sub1 is null");         
            PexAssert.IsTrue(sub1.StorageKey.Name.ToLower().Equals(test1.ToLower()));
        }

		[PexMethod]
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


        //PHASE 1
        [PexMethod(MaxRunsWithoutNewTests = 200)]
        //check for substorage setting save
        public void SubstorageSettingsPUT1([PexAssumeUnderTest]String subName, [PexAssumeUnderTest]String[] name, Object[] value)
        {
            //constraints for NOT NULL Arrays
            PexAssume.IsNotNull(value);

            //constraints for equal size arrays 
            PexAssume.IsTrue(name.Length == value.Length);

            for (int i = 0; i < value.Length; i++)
            {
                //constraint for the TYPE of Object to be created
                PexAssume.IsTrue(value[i] is String || value[i] is int);

                //constraint for each name to be NOT NULL
                PexAssume.IsNotNull(name[i]);

                PexAssume.IsTrue(name[i].Contains("t"));

                //constraint for each value to be NOT NULL
                PexAssume.IsNotNull(value[i]);
            }

            //Constratraint to avoid duplicate values
            for (int i = 0; i < name.Length; i++)
            {
                for (int j = 0; j < name.Length; j++)
                {
                    PexAssume.IsFalse(name[j].Equals(name[j]));
                }
            }

            ISettingsStorage sub = storage.MakeChildStorage(subName);
            for (int j = 0; j < value.Length; j++)
            {
                sub.SaveSetting(name[j], value[j]);
            }
            for (int j = 0; j < value.Length; j++)
            {
                PexAssert.AreEqual(value[j], sub.GetSetting(name[j]));
            }


        }

        //PHASE 2
        [PexMethod(MaxRunsWithoutNewTests = 100)]
        //check for substorage setting save
        public void SubstorageSettingsPUT3([PexAssumeUnderTest]String subName, [PexAssumeUnderTest]String[] name,Object[] value)
        {
            //constraints for NOT NULL Arrays
            PexAssume.IsNotNull(value);
            
            //constraints for equal size arrays 
            PexAssume.IsTrue(name.Length == value.Length);
            
            for(int i = 0; i < value.Length; i ++){
                //constraint for the TYPE of Object to be created
                PexAssume.IsTrue(value[i] is String || value[i] is int);

                //constraint for each name to be NOT NULL
                PexAssume.IsNotNull(name[i]);
                
                PexAssume.IsTrue(name[i].Contains("t"));
               
                //constraint for each value to be NOT NULL
                PexAssume.IsNotNull(value[i]);
            }

            //Constratraint to generate duplicate values
            for (int i = 0; i < name.Length; i++)
            {
                for (int j = 0; j < name.Length; j++)
                {
                    PexAssume.IsFalse(name[j].Equals(name[j]));
                }
            }
            
            ISettingsStorage sub = storage.MakeChildStorage(subName);
            for (int j = 0; j < value.Length; j++)
            {
                sub.SaveSetting(name[j], value[j]);
            }
            for (int j = 0; j < value.Length; j++)
            {
                PexAssert.AreEqual(value[j], sub.GetSetting(name[j]));
            }
            

        }

        //PHASE 1
        [PexMethod]
        //check for saved substorage settings removed
        public void SubstorageSettingsPUT2([PexAssumeUnderTest]String subName, [PexAssumeUnderTest]String[] name, [PexAssumeUnderTest]Object[] value)
        {
            PexAssume.IsTrue(name.Length == value.Length);
            for (int i = 0; i < value.Length; i++)
            {
                PexAssume.IsTrue(value[i] is String || value[i] is int);
                PexAssume.IsNotNullOrEmpty(name[i]);
                PexAssume.IsNotNull(value[i]);
                PexAssume.IsTrue(name[i].Contains("t"));

            }
            for (int i = 0; i < name.Length; i++)
            {
                PexAssume.IsNotNullOrEmpty(name[i]);
            }
            ISettingsStorage sub = storage.MakeChildStorage(subName);
            for (int j = 0; j < value.Length; j++)
            {
                sub.SaveSetting(name[j], value[j]);
            }
            for (int j = 0; j < value.Length; j++)
            {
                String curr = name[j];
                sub.RemoveSetting(curr);
                PexAssert.IsNull(sub.GetSetting(curr), curr + " not removed");

            }
        }

        //PHASE 2
        [PexMethod]
        //check for saved substorage settings removed
        public void SubstorageSettingsPUT4([PexAssumeUnderTest]String subName, [PexAssumeUnderTest]String[] name, [PexAssumeUnderTest]Object[] value)
        {
            PexAssume.IsTrue(name.Length == value.Length);
            for (int i = 0; i < value.Length; i++)
            {
                PexAssume.IsTrue(value[i] is bool || value[i] is String || value[i] is int);
                PexAssume.IsNotNullOrEmpty(name[i]);
                PexAssume.IsNotNull(value[i]);
                if(value[i] is String)
                    PexAssume.IsTrue(name[i].Contains(".") || !name[i].Contains("."));
            
            }
            for (int i = 0; i < name.Length; i++)
            {
                PexAssume.IsNotNullOrEmpty(name[i]);
            }
            ISettingsStorage sub = storage.MakeChildStorage(subName);
            for (int j = 0; j < value.Length-1; j++)
            {
                sub.SaveSetting(name[j], value[j]);
            }
            for (int j = 0; j < value.Length-1; j++)
            {
                String curr = name[j];
                sub.RemoveSetting(curr);
                PexAssert.IsNull(sub.GetSetting(curr), curr + " not removed");
                
            }
            if (name.Length > 0)
                PexAssert.AreEqual(null, storage.GetSetting(name[name.Length - 1]));
        }


		[PexMethod]
		public void TypeSafeSettings()
		{
			storage.SaveSetting( "X", 5);
			storage.SaveSetting( "Y", "17" );
			storage.SaveSetting( "NAME", "Charlie");

			Assert.AreEqual( 5, storage.GetSetting("X") );
			Assert.AreEqual( "17", storage.GetSetting( "Y" ) );
			Assert.AreEqual( "Charlie", storage.GetSetting( "NAME" ) );
		}

        //PHASE 1
        //Error with special character
        [PexMethod]
        public void TypeSafeSettingsPUT1([PexAssumeUnderTest]String name, [PexAssumeUnderTest]String value)
        {
            //  PexAssume.IsNotNull(value);
            PexAssume.IsTrue(value.Length > 0);
            PexAssume.IsTrue(name.Contains("test"));
            storage.SaveSetting(name, value);
            //  storage.SaveSetting("Y", "17");
            // storage.SaveSetting("NAME", "Charlie");

            PexAssert.AreEqual(value, storage.GetSetting(name));
            //Assert.AreEqual("17", storage.GetSetting("Y"));
            // Assert.AreEqual("Charlie", storage.GetSetting("NAME"));
        }

        //PHASE 2
        //Error with special character
        [PexMethod]
        public void TypeSafeSettingsPUT2([PexAssumeUnderTest]String name, [PexAssumeUnderTest]String value)
        {
          //  PexAssume.IsNotNull(value);
            PexAssume.IsTrue(value.Length > 0);
            PexAssume.IsTrue(name.Contains("test"));
            storage.SaveSetting(name, value);
          //  storage.SaveSetting("Y", "17");
           // storage.SaveSetting("NAME", "Charlie");

            PexAssert.AreEqual(value, storage.GetSetting(name));
            //Assert.AreEqual("17", storage.GetSetting("Y"));
           // Assert.AreEqual("Charlie", storage.GetSetting("NAME"));
        }
	}
}
