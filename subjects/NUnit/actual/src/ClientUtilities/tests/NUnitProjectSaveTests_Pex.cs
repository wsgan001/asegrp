// ****************************************************************
// This is free software licensed under the NUnit license. You
// may obtain a copy of the license as well as information regarding
// copyright ownership at http://nunit.org/?p=license&r=2.4.
// ****************************************************************

using System;
using System.Text;
using System.Xml;
using System.IO;
using NUnit.Framework;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace NUnit.Util.Tests
{
	[PexClass(typeof(NUnitProject))]
    public partial class NUnitProjectSaveTests_Pex
	{
		static readonly string xmlfile = "test.nunit";

		private NUnitProject project;

		//[TestInitialize]
		public void SetUp()
		{
            //Console.WriteLine("testinitiliaze");
			project = NUnitProject.EmptyProject();
		}

		[TestCleanup]
		public void TearDown()
		{
			if ( File.Exists( xmlfile ) )
				File.Delete( xmlfile );
		}

		private void CheckContents( string expected )
		{
			StreamReader reader = new StreamReader( xmlfile );
			string contents = reader.ReadToEnd();
			reader.Close();
			Assert.AreEqual( expected, contents );
		}

        private void CheckContentsPUT(string expected, string xmlFILE)
        {
            StreamReader reader = new StreamReader(xmlFILE+xmlfile);
            string contents = reader.ReadToEnd();
            reader.Close();
            PexAssert.AreEqual(expected, contents);
        }

		[PexMethod]
		public void SaveEmptyProject()
		{
			project.Save( xmlfile );

			CheckContents( NUnitProjectXml.EmptyProject );
		}

        //PUTs
        [PexMethod]
        public void TestSaveEmptyProjectPUT1([PexAssumeUnderTest]NUnitProject savedProject)
        {
            //PexAssume.IsTrue(!xmlFILE.Equals(""));
            //project.Save(xmlFILE);
            //observer method
            CheckContentsPUT(NUnitProjectXml.EmptyProject, savedProject.ProjectPath);

        }

		[PexMethod]
		public void SaveEmptyConfigs()
		{
			project.Configs.Add( "Debug" );
			project.Configs.Add( "Release" );

			project.Save( xmlfile );

			CheckContents( NUnitProjectXml.EmptyConfigs );			
		}

		[PexMethod]
		public void SaveNormalProject()
		{
			ProjectConfig config1 = new ProjectConfig( "Debug" );
			config1.BasePath = "bin" + Path.DirectorySeparatorChar + "debug";
			config1.Assemblies.Add( Path.GetFullPath( "bin" + Path.DirectorySeparatorChar + "debug" + Path.DirectorySeparatorChar + "assembly1.dll" ) );
			config1.Assemblies.Add( Path.GetFullPath( "bin" + Path.DirectorySeparatorChar + "debug" + Path.DirectorySeparatorChar + "assembly2.dll" ) );

			ProjectConfig config2 = new ProjectConfig( "Release" );
			config2.BasePath = "bin" + Path.DirectorySeparatorChar + "release";
			config2.Assemblies.Add( Path.GetFullPath( "bin" + Path.DirectorySeparatorChar + "release" + Path.DirectorySeparatorChar + "assembly1.dll" ) );
			config2.Assemblies.Add( Path.GetFullPath( "bin" + Path.DirectorySeparatorChar + "release" + Path.DirectorySeparatorChar + "assembly2.dll" ) );

			project.Configs.Add( config1 );
			project.Configs.Add( config2 );

			project.Save( xmlfile );

			CheckContents( NUnitProjectXml.NormalProject );
		}
	}
}
