// ****************************************************************
// This is free software licensed under the NUnit license. You
// may obtain a copy of the license as well as information regarding
// copyright ownership at http://nunit.org/?p=license&r=2.4.
// ****************************************************************

using System;
using NUnit.Framework;
using Microsoft.Win32;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Graphs;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Text.RegularExpressions;

namespace NUnit.Util.Tests
{
    [PexClass(typeof(SettingsGroup))]
    public partial class SettingsGroupTests
	{
		private SettingsGroup testGroup;

		[TestInitialize]
		public void BeforeEachTest()
		{
			MemorySettingsStorage storage = new MemorySettingsStorage();
			testGroup = new SettingsGroup( storage );
		}

		[TestCleanup]
		public void AfterEachTest()
		{
			testGroup.Dispose();
		}

		[PexMethod]
		public void TopLevelSettings()
		{
			testGroup.SaveSetting( "X", 5 );
			testGroup.SaveSetting( "NAME", "Charlie" );
			PexAssert.AreEqual( 5, testGroup.GetSetting( "X" ) );
			PexAssert.AreEqual( "Charlie", testGroup.GetSetting( "NAME" ) );

			testGroup.RemoveSetting( "X" );
			PexAssert.IsNull( testGroup.GetSetting( "X" ), "X not removed" );
			PexAssert.AreEqual( "Charlie", testGroup.GetSetting( "NAME" ) );

			testGroup.RemoveSetting( "NAME" );
			PexAssert.IsNull( testGroup.GetSetting( "NAME" ), "NAME not removed" );
		}

        [PexMethod]
        public void TopLevelSettingsPUT1([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeUnderTest]Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(testGroup1.GetSetting(settingName) == null);            
            testGroup1.SaveSetting(settingName, settingValue);
            PexAssume.AreEqual(settingValue, testGroup1.GetSetting(settingName));

        }

        [PexMethod]
        public void TopLevelSettingsPUT2([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeUnderTest]Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(testGroup1.GetSetting(settingName) != null);
            testGroup1.RemoveSetting(settingName);                
            PexAssume.IsNull(testGroup1.GetSetting(settingName));
        }

        [PexMethod]
        public void TopLevelSettingsPUT3([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeUnderTest]Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            DelegateHandler dhObj = new DelegateHandler(testGroup1);
            PexAssume.IsTrue(testGroup1.GetSetting(settingName) != null);            
            testGroup1.SaveSetting(settingName, testGroup1.GetSetting(settingName));
            testGroup1.RemoveSetting(settingName);
            PexAssume.IsNull(testGroup1.GetSetting(settingName));
        }

		[PexMethod]
		public void SubGroupSettings()
		{
			SettingsGroup subGroup = new SettingsGroup( testGroup.Storage );
			PexAssert.IsNotNull( subGroup );
			PexAssert.IsNotNull( subGroup.Storage );

			subGroup.SaveSetting( "X", 5 );
			subGroup.SaveSetting( "NAME", "Charlie" );
			PexAssert.AreEqual( 5, subGroup.GetSetting( "X" ) );
			PexAssert.AreEqual( "Charlie", subGroup.GetSetting( "NAME" ) );

			subGroup.RemoveSetting( "X" );
			PexAssert.IsNull( subGroup.GetSetting( "X" ), "X not removed" );
			PexAssert.AreEqual( "Charlie", subGroup.GetSetting( "NAME" ) );

			subGroup.RemoveSetting( "NAME" );
			PexAssert.IsNull( subGroup.GetSetting( "NAME" ), "NAME not removed" );
		}

        [PexMethod]
        public void SubGroupSettingsPUT1([PexAssumeUnderTest] SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeUnderTest]Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);

            SettingsGroup subGroup = new SettingsGroup(testGroup1.Storage);
            PexAssert.IsNotNull(subGroup);
            PexAssert.IsNotNull(subGroup.Storage);

            subGroup.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(settingValue, subGroup.GetSetting(settingName));            
        }

        [PexMethod]
        public void SubGroupSettingsPUT2([PexAssumeUnderTest] SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeUnderTest]Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);

            SettingsGroup subGroup = new SettingsGroup(testGroup1.Storage);
            PexAssert.IsNotNull(subGroup);
            PexAssert.IsNotNull(subGroup.Storage);
            subGroup.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(settingValue, subGroup.GetSetting(settingName));
            subGroup.RemoveSetting(settingName);
            PexAssert.IsNull(subGroup.GetSetting(settingName));
        }

        [PexMethod]
        public void SubGroupSettingsPUT3([PexAssumeUnderTest] SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeUnderTest]Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            DelegateHandler dhObj1 = new DelegateHandler(testGroup1);
            SettingsGroup subGroup = new SettingsGroup(testGroup1.Storage);
            PexAssert.IsNotNull(subGroup);
            PexAssert.IsNotNull(subGroup.Storage);

            //Without delegate
            subGroup.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(settingValue, subGroup.GetSetting(settingName));
            subGroup.RemoveSetting(settingName);
            PexAssert.IsNull(subGroup.GetSetting(settingName));
            
            //With delegate
            DelegateHandler dhObj2 = new DelegateHandler(subGroup);
            subGroup.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(settingValue, subGroup.GetSetting(settingName));
            subGroup.RemoveSetting(settingName);
            PexAssert.IsNull(subGroup.GetSetting(settingName));
        }

		[PexMethod]
		public void TypeSafeSettings()
		{
			testGroup.SaveSetting( "X", 5);
			testGroup.SaveSetting( "Y", "17" );
			testGroup.SaveSetting( "NAME", "Charlie");

			PexAssert.AreEqual( 5, testGroup.GetSetting("X") );
			PexAssert.AreEqual( "17", testGroup.GetSetting( "Y" ) );
			PexAssert.AreEqual( "Charlie", testGroup.GetSetting( "NAME" ) );
		}

		[PexMethod]
		public void DefaultSettings()
		{
			PexAssert.IsNull( testGroup.GetSetting( "X" ) );
			PexAssert.IsNull( testGroup.GetSetting( "NAME" ) );

			PexAssert.AreEqual( 5, testGroup.GetSetting( "X", 5 ) );
			PexAssert.AreEqual( 6, testGroup.GetSetting( "X", 6 ) );
			PexAssert.AreEqual( "7", testGroup.GetSetting( "X", "7" ) );

			PexAssert.AreEqual( "Charlie", testGroup.GetSetting( "NAME", "Charlie" ) );
			PexAssert.AreEqual( "Fred", testGroup.GetSetting( "NAME", "Fred" ) );
		}

        [PexMethod]
        public void DefaultSettingsPUT1([PexAssumeUnderTest] String settingName, [PexAssumeUnderTest]Object settingValue, [PexAssumeUnderTest]Object defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(defSettingValue != null);
            PexAssume.IsTrue(settingValue is string || settingValue is int || settingValue is bool || settingValue is Enum);
            PexAssume.IsTrue(defSettingValue is string || defSettingValue is int || defSettingValue is bool || defSettingValue is Enum);

            PexAssert.IsNull(testGroup.GetSetting(settingName));
            PexAssert.AreEqual(defSettingValue, testGroup.GetSetting(settingName, defSettingValue));
        }
                
		[PexMethod]
		public void BadSetting()
		{
			testGroup.SaveSetting( "X", "1y25" );
			PexAssert.AreEqual( 12, testGroup.GetSetting( "X", 12 ) );
		}

        [PexMethod]
        public void BadSettingPUT1([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]String settingName, [PexAssumeUnderTest]Object settingValue, int defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            //PexAssume.IsTrue(defSettingValue != null);
            PexAssume.IsTrue(settingValue is string);
            String str_sv = settingValue as string;
            PexAssume.IsTrue(Regex.IsMatch(str_sv, "@([a-z])*"));

            testGroup1.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(defSettingValue, testGroup1.GetSetting(settingName, defSettingValue));
        }

        [PexMethod]
        public void BadSettingPUT2([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]String settingName, [PexAssumeUnderTest]Object settingValue, bool defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            //PexAssume.IsTrue(defSettingValue != null);
            PexAssume.IsTrue(settingValue is string);
            PexAssume.IsTrue(!settingValue.Equals("True") && !settingValue.Equals("False"));

            testGroup1.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(defSettingValue, testGroup1.GetSetting(settingName, defSettingValue));
        }

        [PexMethod]
        public void BadSettingPUT3([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]String settingName, [PexAssumeUnderTest]Object settingValue, bool defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            //PexAssume.IsTrue(defSettingValue != null);
            PexAssume.IsTrue(settingValue is string);
            PexAssume.IsTrue(!settingValue.Equals("True") && !settingValue.Equals("False"));
            DelegateHandler dhObj1 = new DelegateHandler(testGroup1);
            testGroup1.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(defSettingValue, testGroup1.GetSetting(settingName, defSettingValue));
        }

        [PexMethod]
        public void BadSettingPUT4([PexAssumeUnderTest]String settingName, [PexAssumeUnderTest]Object settingValue, [PexAssumeUnderTest]Object defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(defSettingValue != null);
            
            SettingsGroup testGroup1 = new SettingsGroup(new MemorySettingsStorage());
            DelegateHandler dhObj1 = new DelegateHandler(testGroup1);
            if (defSettingValue is object)
            {
                PexAssert.AreEqual(defSettingValue, testGroup1.GetSetting(settingName, defSettingValue));
            }            
            if (settingValue is String && defSettingValue is int)
            {
                PexAssert.AreEqual(defSettingValue, testGroup1.GetSetting(settingName, defSettingValue));                              
            }            
        }

        [PexMethod]
        public void BadSettingPUT5([PexAssumeUnderTest]String settingName, [PexAssumeUnderTest]Object settingValue, [PexAssumeUnderTest]Object defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(defSettingValue != null && defSettingValue is String);
            PexAssume.IsTrue(settingValue is bool);

            SettingsGroup testGroup1 = new SettingsGroup(new MemorySettingsStorage());
            DelegateHandler dhObj1 = new DelegateHandler(testGroup1);
            testGroup1.SaveSetting(settingName, settingValue);
            String defSettingValue_typecast = defSettingValue as String;
            PexAssert.AreEqual(settingValue.ToString(), testGroup1.GetSetting(settingName, defSettingValue_typecast));
        }

        [PexMethod]
        public void BadSettingPUT6([PexAssumeUnderTest]String settingName, [PexAssumeUnderTest]Object settingValue, [PexAssumeUnderTest]Object defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null && settingValue is String);

            String str_sv = settingValue.ToString();
            PexAssume.IsTrue(Regex.IsMatch(str_sv, "@([a-z])*"));
            PexAssume.IsTrue(defSettingValue != null);
            
            SettingsGroup testGroup1 = new SettingsGroup(new MemorySettingsStorage());
            DelegateHandler dhObj1 = new DelegateHandler(testGroup1);
            
            if (defSettingValue is int)
            {
                testGroup1.SaveSetting(settingName, settingValue);
                int defVal = Int32.Parse(defSettingValue.ToString());                    
                PexAssert.AreEqual(defVal, testGroup1.GetSetting(settingName, defVal));
            } 
            else if (defSettingValue is string)
            {
                String defVal = defSettingValue.ToString();
                PexAssert.AreEqual(defVal, testGroup1.GetSetting(settingName, defVal));
            }
            else if (!settingValue.Equals("True") && !settingValue.Equals("False") && defSettingValue is bool)
            {
                bool defVal = Boolean.Parse(defSettingValue.ToString());
                PexAssert.AreEqual(defVal, testGroup1.GetSetting(settingName, defVal));
                testGroup1.SaveSetting(settingName, settingValue);
                PexAssert.AreEqual(defVal, testGroup1.GetSetting(settingName, defVal));
            } 
        }
	}

       

    //An event handler class written to increase the coverage 
    class DelegateHandler
    {
        public void MySettingsEventHandler(object sender, SettingsEventArgs args)
        {
            PexAssume.IsTrue(sender != null);
            Console.WriteLine("Called MySettingsEventHandler with arguments {0}", args.SettingName);            
        }

        public DelegateHandler(SettingsGroup sgObj)
        {
            SettingsEventHandler sehObj1 = new SettingsEventHandler(MySettingsEventHandler);
            sgObj.Changed += sehObj1;
        }
    }
}
