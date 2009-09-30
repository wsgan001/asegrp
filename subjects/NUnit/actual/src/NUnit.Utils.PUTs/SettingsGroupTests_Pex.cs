// ****************************************************************
// This is free software licensed under the NUnit license. You
// may obtain a copy of the license as well as information regarding
// copyright ownership at http://nunit.org/?p=license&r=2.4.
// ****************************************************************

using System;
//using NUnit.Framework;
using Microsoft.Win32;
using Microsoft.Pex.Framework;

using System.Text.RegularExpressions;
using NUnit.Framework;

namespace NUnit.Util.PUTs
{
   [TestFixture]
    [PexClass]//(typeof(SettingsGroup))]
    public partial class SettingsGroupTests
	{
		private SettingsGroup testGroup;

		[SetUp]
		public void BeforeEachTest()
		{
			MemorySettingsStorage storage = new MemorySettingsStorage();
			testGroup = new SettingsGroup( storage );
		}

		[TearDown]
		public void AfterEachTest()
		{
			testGroup.Dispose();
		}

        [PexMethod]
        public void TopLevelSettingsPUT1([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeNotNull]Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(testGroup1.GetSetting(settingName) == null);            
            testGroup1.SaveSetting(settingName, settingValue);
            PexAssume.AreEqual(settingValue, testGroup1.GetSetting(settingName));

        }

        [PexMethod]
        public void TopLevelSettingsPUT2([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeNotNull]Object settingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(testGroup1.GetSetting(settingName) != null);
            testGroup1.RemoveSetting(settingName);                
            PexAssume.IsNull(testGroup1.GetSetting(settingName));
        }

       /* [PexMethod] //PHASE 2
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
        }*/

        [PexMethod]
        public void SubGroupSettingsPUT1([PexAssumeUnderTest] SettingsGroup testGroup1, [PexAssumeUnderTest]
                            String settingName, [PexAssumeNotNull]Object settingValue)
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
                            String settingName, [PexAssumeNotNull]Object settingValue)
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

      /* PHASE 2
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
        } */

        [PexMethod]
        public void DefaultSettingsPUT1([PexAssumeUnderTest] String settingName, [PexAssumeNotNull]Object settingValue, [PexAssumeNotNull]Object defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            PexAssume.IsTrue(defSettingValue != null);
            PexAssume.IsTrue(settingValue is string || settingValue is int || settingValue is bool);//|| settingValue is Enum);
            PexAssume.IsTrue(defSettingValue is string || defSettingValue is int || defSettingValue is bool);// || defSettingValue is Enum);

            PexAssert.IsNull(testGroup.GetSetting(settingName));
            PexAssert.AreEqual(defSettingValue, testGroup.GetSetting(settingName, defSettingValue));
        }
     
        [PexMethod]
        public void BadSettingPUT1([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]String settingName, [PexAssumeNotNull]Object settingValue, int defSettingValue)
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
        public void BadSettingPUT2([PexAssumeUnderTest]SettingsGroup testGroup1, [PexAssumeUnderTest]String settingName, [PexAssumeNotNull]Object settingValue, bool defSettingValue)
        {
            PexAssume.IsTrue(settingName != null);
            PexAssume.IsTrue(settingValue != null);
            //PexAssume.IsTrue(defSettingValue != null);
            PexAssume.IsTrue(settingValue is string);
            PexAssume.IsTrue(!settingValue.Equals("True") && !settingValue.Equals("False"));

            testGroup1.SaveSetting(settingName, settingValue);
            PexAssert.AreEqual(defSettingValue, testGroup1.GetSetting(settingName, defSettingValue));
        }

        //PHASE 2
        /*[PexMethod]
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
        }*/
	}

       

    //An event handler class written to increase the coverage 
  /*  class DelegateHandler
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
    }*/
}
