// ****************************************************************
// This is free software licensed under the NUnit license. You
// may obtain a copy of the license as well as information regarding
// copyright ownership at http://nunit.org/?p=license&r=2.4.
// ****************************************************************

using System;
using System.IO;
using System.Text;
//using System.Windows.Forms;
using Microsoft.Win32;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework.Validation;
using System.Collections;

namespace NUnit.Util.Tests
{
	/// <summary>
	/// Summary description for NUnitRegistryTests.
	/// </summary>
	[PexClass(typeof(NUnitRegistry))]
    public partial class NUnitRegistryTests
	{
		[TestCleanup]
		public void RestoreRegistry()
		{
			NUnitRegistry.TestMode = false;
		}


        //cannot parametrize it
		[PexMethod]
		public void CurrentUser()
		{
			NUnitRegistry.TestMode = false;
			using( RegistryKey key = NUnitRegistry.CurrentUser )
			{
				Assert.IsNotNull( key );
				Assert.AreEqual( @"HKEY_CURRENT_USER\Software\nunit.org\Nunit\2.4".ToLower(), key.Name.ToLower() );
			}
		}


        //cannot parametrize it
		[PexMethod]
		public void CurrentUserTestMode()
		{

			NUnitRegistry.TestMode = true;
			using( RegistryKey key = NUnitRegistry.CurrentUser )
			{
				Assert.IsNotNull( key );
				Assert.AreEqual( @"HKEY_CURRENT_USER\Software\nunit.org\Nunit-Test".ToLower(), key.Name.ToLower() );
			}
		}


        //combining method CurrentUser() and CurrentUserTestMode()
        [PexMethod]
        [PexExpectedGoals]
        public void CurrentUserTestModePUT1(bool testMode)
        {

            NUnitRegistry.TestMode = testMode;
            using (RegistryKey key = NUnitRegistry.CurrentUser)
            {
                if (key == null)
                {
                    PexAssert.IsNotNull(key);
                    throw new PexGoalException();
                }
                else
                {
                    //PexAssert.IsNotNull(key);
                    if (testMode)
                    {
                        PexAssert.IsTrue(key.Name.ToLower().EndsWith(@"HKEY_CURRENT_USER\Software\nunit.org\Nunit-Test".ToLower()), key.Name);
                    }
                    else
                    {
                        PexAssert.IsTrue(key.Name.ToLower().EndsWith(@"HKEY_CURRENT_USER\Software\nunit.org\Nunit\2.4".ToLower()), key.Name);
                    }
                }
            }
        }

        //cannot parametrize it
        [PexMethod]
        public void LocalMachine()
        {
            NUnitRegistry.TestMode = false;
            using (RegistryKey key = NUnitRegistry.LocalMachine)
            {
                PexAssert.IsNotNull(key);
               // StringAssert.EndsWith(@"Software\nunit.org\Nunit\2.4".ToLower(), key.Name.ToLower());
                Console.WriteLine(key.Name);
                PexAssert.IsTrue(key.Name.ToLower().EndsWith(@"\Software\nunit.org\Nunit\2.4".ToLower()));
            }
        }

        //cannot parametrize it
		[PexMethod]
		public void LocalMachineTestMode()
		{
			NUnitRegistry.TestMode = true;
			using( RegistryKey key = NUnitRegistry.LocalMachine )
			{
                PexAssert.IsNotNull(key);
                // StringAssert.EndsWith(@"Software\nunit.org\Nunit\2.4".ToLower(), key.Name.ToLower());
                Console.WriteLine(key.Name);
                PexAssert.IsTrue(key.Name.ToLower().EndsWith(@"\Software\nunit.org\Nunit-Test".ToLower()));
			}
		}


        [PexMethod]
        public void LocalMachinePUT1(bool testMode)
        {
            NUnitRegistry.TestMode = testMode;
            using (RegistryKey key = NUnitRegistry.LocalMachine)
            {
                PexAssert.IsNotNull(key);
                // StringAssert.EndsWith(@"Software\nunit.org\Nunit\2.4".ToLower(), key.Name.ToLower());
                Console.WriteLine(key.Name);
                if(!testMode)
                    PexAssert.IsTrue(key.Name.ToLower().EndsWith(@"\Software\nunit.org\Nunit\2.4".ToLower()));
                else
                    PexAssert.IsTrue(key.Name.ToLower().EndsWith(@"\Software\nunit.org\Nunit-Test".ToLower()));
            }
        }
       
		[PexMethod]
		public void TestClearRoutines()
		{
			NUnitRegistry.TestMode = true;
            /*
             * \0, \0, \0 - 61.76
             * \0, \0, t - 70.59
             * \0,t,t - 70.59
             * t, t, t - 70.59
             * 
             * */
			using( RegistryKey key = NUnitRegistry.CurrentUser )
			using( RegistryKey foo = key.CreateSubKey( "\0" ) )
			using( RegistryKey bar = key.CreateSubKey( "t" ) )
			using( RegistryKey footoo = foo.CreateSubKey( "\0" ) )
			{
                Console.WriteLine(key.SubKeyCount);
				key.SetValue("X", 5);
				key.SetValue("NAME", "Joe");
				foo.SetValue("Y", 17);
				bar.SetValue("NAME", "Jennifer");
				footoo.SetValue( "X", 5 );
				footoo.SetValue("NAME", "Charlie" );
				
				NUnitRegistry.ClearTestKeys();

				PexAssert.AreEqual( 0, key.ValueCount );
                PexAssert.AreEqual(0, key.SubKeyCount);
			}
		}


       // [PexMethod]
        public void TestClearRoutinesPUT1([PexAssumeUnderTest] String[] name, Object[] value, [PexAssumeUnderTest] String[] key)
        {
            //PexAssume.IsNotNull(mainKey);
          PexAssume.IsNotNull(value);
          PexAssume.IsTrue(key.Length >= 1);
          PexAssume.IsTrue(key.Length <= name.Length);
          PexAssume.IsTrue(name.Length == value.Length);

          for (int i = 0; i < name.Length; i++)
          {
              PexAssume.IsNotNullOrEmpty(name[i]);
              PexAssume.IsTrue(value[i] is String || value[i] is int);
              PexAssume.IsNotNull(value[i]);
              if(i < key.Length)
                PexAssume.IsNotNull(key[i]);
          }

          for (int i = 0; i < value.Length - key.Length; i++)
          {
              PexAssume.IsNotNull(value[i+key.Length]);
              PexAssume.IsNotNull(name[i + key.Length]);
          }
         /* for (int i = 0; i < key.Length; i++)
          {
              
          }*/
          NUnitRegistry.TestMode = true;
          using (RegistryKey mainKey = NUnitRegistry.CurrentUser){
          mainKey.SetValue(name[0], value[0]);
          int k = 0;
          for (k = 1; k < key.Length; k++)
           {
               RegistryKey subKey = mainKey.CreateSubKey(key[k-1]);
               subKey.SetValue(name[k], value[k]);
           }
          k = 0;
          for (int i = 0; i < value.Length - key.Length; i++)
          {
              RegistryKey subKey = mainKey.CreateSubKey(key[k]);
              subKey.SetValue(name[i + key.Length], value[i + key.Length]);
              k++;
              if (k == key.Length)
                  k = 0;
              //PexAssume.IsNotNull(value[i + key.Length]);
              //PexAssume.IsNotNull(key[i + key.Length]);
          }
            NUnitRegistry.ClearTestKeys();
        PexAssert.IsTrue(mainKey.ValueCount == 0);
        PexAssert.IsTrue(mainKey.SubKeyCount == 0);
          }
        }

      //  [PexMethod]
        //Case when number of subkeys are more than the name and value
        public void TestClearRoutinesPUT2([PexAssumeUnderTest] String[] name, Object[] value, [PexAssumeUnderTest] String[] key)
        {
            //PexAssume.IsNotNull(mainKey);
            PexAssume.IsNotNull(value);
            PexAssume.IsTrue(name.Length >= 1);
            PexAssume.IsTrue(key.Length >= name.Length);
            PexAssume.IsTrue(name.Length == value.Length);

            for (int i = 0; i < key.Length; i++)
            {
                if (i < name.Length)
                {
                    PexAssume.IsNotNullOrEmpty(name[i]);
                    PexAssume.IsTrue(value[i] is String || value[i] is int);
                    PexAssume.IsNotNull(value[i]);
                }
                //if (i < key.Length)
                PexAssume.IsNotNull(key[i]);
            }
            NUnitRegistry.TestMode = true;
            using (RegistryKey mainKey = NUnitRegistry.CurrentUser)
            {
                mainKey.SetValue(name[0], value[0]);
                int k = 0;
                RegistryKey prevSubKey = null;
                prevSubKey = mainKey;
                for (k = 1; k < key.Length; k++)
                {
                    RegistryKey subKey= null;
                    subKey = prevSubKey.CreateSubKey(key[k - 1]);
                    prevSubKey = subKey;
                    if(k < name.Length)
                        subKey.SetValue(name[k], value[k]);
                }
               
                NUnitRegistry.ClearTestKeys();
                PexAssert.IsTrue(mainKey.ValueCount == 0);
                PexAssert.IsTrue(mainKey.SubKeyCount == 0);
            }
        }


        [PexMethod()]
        public void TestClearRoutinesPUT3([PexAssumeUnderTest] String[] name, Object[] value, [PexAssumeUnderTest] String[] key)
        {
            //PexAssume.IsNotNull(mainKey);

            PexAssume.IsNotNull(value);
            PexAssume.IsTrue(name.Length >= 1);
            PexAssume.IsTrue(key.Length < name.Length);
            PexAssume.IsTrue(name.Length == value.Length);

            for (int i = 0; i < name.Length; i++)
            {
                //if (i < name.Length)
                //{

                    PexAssume.IsNotNullOrEmpty(name[i]);
                    PexAssume.IsTrue(value[i] is String || value[i] is int);
                    PexAssume.IsNotNull(value[i]);
               // }
                    if (i < key.Length)
                    {
                        PexAssume.IsNotNull(key[i]);
                        PexAssume.IsTrue(key[i].Contains("t"));
                    }
            }
            NUnitRegistry.TestMode = true;
            using (RegistryKey mainKey = NUnitRegistry.CurrentUser)
            {
                mainKey.SetValue(name[0], value[0]);
                int k = 0;
                ArrayList keyList = new ArrayList();
                keyList.Add(mainKey);
               for (k = 1; k < name.Length; k++)
                {
                   RegistryKey subKey = mainKey.CreateSubKey(key[k - 1]);
                   //RegistryKey testSubKey = subKey.CreateSubKey("test" + keyList.Count);
                   // testSubKey.SetValue("boo", "booo");

                    //if (k < name.Length)
                    subKey.SetValue(name[k], value[k]);
                    keyList.Add(subKey);
                }

                NUnitRegistry.ClearTestKeys();
                PexAssert.IsTrue(mainKey.ValueCount == 0);
                PexAssert.IsTrue(mainKey.SubKeyCount == 0);
            }
        }

        [PexMethod()]
        public void TestClearRoutinesPUT4([PexAssumeUnderTest] String[] name, Object[] value, [PexAssumeUnderTest] String[] key)
        {
            //PexAssume.IsNotNull(mainKey);
            PexAssume.IsNotNull(value);
            PexAssume.IsTrue(name.Length >= 1);
            //PexAssume.IsTrue(key.Length < name.Length);
            PexAssume.IsTrue(name.Length == value.Length);

            for (int i = 0; i < name.Length; i++)
            {
                //if (i < name.Length)
                //{

                PexAssume.IsNotNullOrEmpty(name[i]);
                PexAssume.IsTrue(name[i].Contains("test"));
                PexAssume.IsTrue(value[i] is String || value[i] is int);
                PexAssume.IsNotNull(value[i]);
                // }
                //if (i < key.Length)
                //    PexAssume.IsNotNull(key[i]);
            }

            for (int j = 0; j < key.Length; j++)
            {
                PexAssume.IsNotNull(key[j]);
                PexAssume.IsFalse(key[j].Equals(""));
                PexAssume.IsTrue(key[j].Contains("test"));
            }

            NUnitRegistry.TestMode = true;
            using (RegistryKey mainKey = NUnitRegistry.CurrentUser)
            {
                mainKey.SetValue(name[0], value[0]);
                int maxVal = key.Length > name.Length ? key.Length : name.Length;
                int k = 0;
                ArrayList keyList = new ArrayList();
                keyList.Add(mainKey);
                for (k = 0; k < maxVal; k++)
                {
                    Random rand = new Random(k + 1);
                    int genNum = rand.Next();
                    if (keyList.Count - 1 == 0)
                    {
                        genNum = 0;
                    }
                    else
                    {
                        genNum = genNum % (keyList.Count - 1);
                    }
                    Console.WriteLine(genNum);
                    Console.WriteLine(keyList.ToArray()[genNum].ToString());
                    RegistryKey subKey = null;

                    if (k < key.Length)
                    {
                        subKey = ((keyList.ToArray())[genNum] as RegistryKey).CreateSubKey(key[k]);
                        keyList.Add(subKey);
                    }
                    else
                        subKey = (keyList.ToArray())[genNum] as RegistryKey;
                    // RegistryKey subKey = mainKey.CreateSubKey(key[k - 1]);
                    // RegistryKey testSubKey = subKey.CreateSubKey("test" + keyList.Count);
                    // testSubKey.SetValue("boo", "booo");

                    if (k < name.Length)
                    {
                        subKey.SetValue(name[k], value[k]);                        
                    }
                }

                NUnitRegistry.ClearTestKeys();
                PexAssert.IsTrue(mainKey.ValueCount == 0);
                PexAssert.IsTrue(mainKey.SubKeyCount == 0);
            }
        }
	}
}
