using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Pex.Framework.Factories;
using Microsoft.Pex.Framework;
using System.IO;
using Microsoft.Win32;

namespace NUnit.Util.Factories
{
    [PexFactoryClass]
    public partial class NUnitProjectSaveFactory
    {
        [PexFactoryMethod(typeof(NUnitProject))]
        public static NUnitProject Create()
        {
            //PexAssume.IsTrue(name.Contains("tst"));
            NUnitProject savedProject = NUnitProject.EmptyProject();
            savedProject.Save("test.nunit",true);
            return savedProject;
        }

        [PexFactoryMethod(typeof(ProjectConfig))]
        public static ProjectConfig Create([PexAssumeUnderTest] String category, [PexAssumeUnderTest] String dllName)
        {
            PexAssume.IsTrue(dllName.Length > 0);
            PexAssume.IsTrue(category.Equals("Debug") || category.Equals("Release"));
            ProjectConfig config1 = new ProjectConfig(category);
            config1.BasePath = "bin" + Path.DirectorySeparatorChar + category;
            config1.Assemblies.Add(Path.GetFullPath("bin" + Path.DirectorySeparatorChar + category + Path.DirectorySeparatorChar + dllName));
            config1.Assemblies.Add(Path.GetFullPath("bin" + Path.DirectorySeparatorChar + category + Path.DirectorySeparatorChar + dllName));
            return config1;
        }

        
    }
}
