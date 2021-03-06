// <copyright file="PathUtilTests_Windows.IsAssemblyFileTypePUT1.g.cs" company="">Copyright ©  2009</copyright>
// <auto-generated>
// This file contains automatically generated unit tests.
// Do NOT modify this file manually.
// 
// When Pex is invoked again,
// it might remove or update any previously generated unit tests.
// 
// If the contents of this file becomes outdated, e.g. if it does not
// compile anymore, you may delete this file and invoke Pex again.
// </auto-generated>
using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework.Generated;

namespace NUnit.Util.PUTs
{
    public partial class PathUtilTests_Windows
    {
[TestMethod]
[PexGeneratedBy(typeof(PathUtilTests_Windows))]
public void IsAssemblyFileTypePUT106()
{
    this.IsAssemblyFileTypePUT1("");
}
[TestMethod]
[PexGeneratedBy(typeof(PathUtilTests_Windows))]
[PexRaisedException(typeof(ArgumentException))]
public void IsAssemblyFileTypePUT107()
{
    this.IsAssemblyFileTypePUT1("\0\0\0\0");
}
[TestMethod]
[PexGeneratedBy(typeof(PathUtilTests_Windows))]
public void IsAssemblyFileTypePUT108()
{
    this.IsAssemblyFileTypePUT1(".exe");
}
[TestMethod]
[PexGeneratedBy(typeof(PathUtilTests_Windows))]
public void IsAssemblyFileTypePUT109()
{
    this.IsAssemblyFileTypePUT1(".dll");
}
[TestMethod]
[PexGeneratedBy(typeof(PathUtilTests_Windows))]
[PexRaisedException(typeof(ArgumentException))]
public void IsAssemblyFileTypePUT110()
{
    this.IsAssemblyFileTypePUT1("\0\0\0\0\0");
}
    }
}
