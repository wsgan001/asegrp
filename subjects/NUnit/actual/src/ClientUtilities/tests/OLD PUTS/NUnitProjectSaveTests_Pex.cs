// ****************************************************************
// This is free software licensed under the NUnit license. You
// may obtain a copy of the license as well as information regarding
// copyright ownership at http://nunit.org/?p=license&r=2.4.
// ****************************************************************

using System;
using System.Text;
using System.Xml;
using System.IO;
//using NUnit.Framework;
using Microsoft.Pex.Framework;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework.Validation;
using NUnit.Framework;

namespace NUnit.Util.PUTs
{
    [TestClass]
    [TestFixture]
	[PexClass]//(typeof(NUnitProject))]
    public partial class NUnitProjectSaveTests_Pex
	{
		static readonly string xmlfile = "test.nunit";

		private NUnitProject project;

		[TestInitialize]
		public void SetUp()
		{
            //Console.WriteLine("testinitiliaze");
			project = NUnitProject.EmptyProject();
		}

		[TestCleanup]
		public void TearDown()
		{
			//if ( File.Exists( xmlfile ))
			//	File.Delete( xmlfile );
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
            StreamReader reader = new StreamReader(xmlFILE);
            string contents = reader.ReadToEnd();
            reader.Close();
            PexAssert.AreEqual(expected, contents);
        }

        //PHASE 1
        [PexMethod]
        public void SaveEmptyProjectPUT1([PexAssumeUnderTest]String test)
        {
            PexAssume.IsTrue(test.Contains("tst"));
            NUnitProject savedProject = NUnitProject.EmptyProject();
            savedProject.Save(test,false);
            CheckContentsPUT(NUnitProjectXml.EmptyProject, savedProject.ProjectPath);

        }

        [PexMethod]
        public void SaveEmptyConfigsPUT1([PexAssumeUnderTest] String config1, [PexAssumeUnderTest] String config2,[PexAssumeUnderTest] String _xmlFile)
		{
            NUnitProject savedProject = NUnitProject.EmptyProject();
            PexAssume.IsTrue(config1.Contains("t"));
            PexAssume.IsTrue(config2.Contains("t"));
            savedProject.Configs.Add(config1);
            savedProject.Configs.Add(config2);

            Console.WriteLine(savedProject.Configs.Count);
            
            savedProject.Save(_xmlFile,true);

            String testOracle = NUnitProjectXml.EmptyConfigs.Replace("Debug", config1);
            testOracle = testOracle.Replace("Release", config2);
            Console.WriteLine(CreatedProjects.currentProject);
            PexAssert.AreEqual(testOracle, CreatedProjects.currentProject,"Testing of empty project failed");
            //CheckContentsPUT(testOracle, _xmlFile );	
        }

        [PexMethod]
        [PexAllowedException(typeof(IOException))]
        public void SaveNormalProjectPUT1([PexAssumeUnderTest]String config1, [PexAssumeUnderTest]String config2, [PexAssumeUnderTest]String _xmlFile)
        {
            NUnitProject savedProject = NUnitProject.EmptyProject();
            PexAssume.IsTrue(config1.Contains("t"));
            PexAssume.IsTrue(config2.Contains("t"));

            ProjectConfig config = new ProjectConfig(config1);
            config.BasePath = "bin" + Path.DirectorySeparatorChar + config1;
            config.Assemblies.Add(Path.GetFullPath("bin" + Path.DirectorySeparatorChar + config1 + Path.DirectorySeparatorChar + "assembly1.dll"));
            config.Assemblies.Add(Path.GetFullPath("bin" + Path.DirectorySeparatorChar + config1 + Path.DirectorySeparatorChar + "assembly2.dll"));
            savedProject.Configs.Add(config);

            ProjectConfig _config = new ProjectConfig(config2);
            _config.BasePath = "bin" + Path.DirectorySeparatorChar + config2;
            _config.Assemblies.Add(Path.GetFullPath("bin" + Path.DirectorySeparatorChar + config2 + Path.DirectorySeparatorChar + "assembly1.dll"));
            _config.Assemblies.Add(Path.GetFullPath("bin" + Path.DirectorySeparatorChar + config2 + Path.DirectorySeparatorChar + "assembly2.dll"));
            savedProject.Configs.Add(config);

            savedProject.Save(_xmlFile, true);
            String testOracle = NUnitProjectXml.NormalProject.Replace("Debug", config1);
            testOracle = testOracle.Replace("Release", config2);
            Console.WriteLine(CreatedProjects.currentProject);
            PexAssert.AreEqual(testOracle, CreatedProjects.currentProject);
        }
	}
}
