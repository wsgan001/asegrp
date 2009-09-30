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
using NUnit.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework;

namespace NUnit.Util.Tests
{
	/// <summary>
	/// Summary description for NUnitRegistryTests.
	/// </summary>
	//[PexClass] 
    [TestFixture]
    public partial class NUnitRegistryTests
	{
		//[TestCleanup] //
        [TearDown]
		public void RestoreRegistry()
		{
			NUnitRegistry.TestMode = false;
		}

		[Test] //[PexMethod]
		public void CurrentUser()
		{
			NUnitRegistry.TestMode = false;
			using( RegistryKey key = NUnitRegistry.CurrentUser )
			{
				Assert.IsNotNull( key );
				Assert.AreEqual( @"HKEY_CURRENT_USER\Software\nunit.org\Nunit\2.4".ToLower(), key.Name.ToLower() );
			}
		}

		[Test] //[PexMethod]
		public void LocalMachine()
		{
			NUnitRegistry.TestMode = false;
			using( RegistryKey key = NUnitRegistry.LocalMachine )
			{
				Assert.IsNotNull( key );
				PexAssert.IsTrue(@"Software\nunit.org\Nunit\2.4".ToLower().EndsWith(key.Name.ToLower() ));
			}
		}

		[Test] //[PexMethod]
		public void CurrentUserTestMode()
		{

			NUnitRegistry.TestMode = true;
			using( RegistryKey key = NUnitRegistry.CurrentUser )
			{
				Assert.IsNotNull( key );
				Assert.AreEqual( @"HKEY_CURRENT_USER\Software\nunit.org\Nunit-Test".ToLower(), key.Name.ToLower() );
			}
		}

		[Test] //[PexMethod]
		public void LocalMachineTestMode()
		{
			NUnitRegistry.TestMode = true;
			using( RegistryKey key = NUnitRegistry.LocalMachine )
			{
				Assert.IsNotNull( key );
				PexAssert.IsTrue(@"Software\nunit.org\Nunit-Test".ToLower().EndsWith(key.Name.ToLower()));
			}
		}

		[Test] //[PexMethod]
		public void TestClearRoutines()
		{
			NUnitRegistry.TestMode = true;

			using( RegistryKey key = NUnitRegistry.CurrentUser )
			using( RegistryKey foo = key.CreateSubKey( "foo" ) )
			using( RegistryKey bar = key.CreateSubKey( "bar" ) )
			using( RegistryKey footoo = foo.CreateSubKey( "foo" ) )
			{
				key.SetValue("X", 5);
				key.SetValue("NAME", "Joe");
				foo.SetValue("Y", 17);
				bar.SetValue("NAME", "Jennifer");
				footoo.SetValue( "X", 5 );
				footoo.SetValue("NAME", "Charlie" );
				
				NUnitRegistry.ClearTestKeys();

				Assert.AreEqual( 0, key.ValueCount );
				Assert.AreEqual( 0, key.SubKeyCount );
			}
		}
	}
}
