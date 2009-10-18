// <copyright file="Url.Method3.g.cs" company="">Copyright ©  2009</copyright>
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

namespace Reggae.WWW_Subjects
{
    public partial class Url
    {
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method301()
{
    bool b;
    b = this.Method3("");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method302()
{
    bool b;
    b = this.Method3("\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method303()
{
    bool b;
    b = this.Method3("`\00");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method304()
{
    bool b;
    b = this.Method3("i");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method305()
{
    bool b;
    b = this.Method3("f");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method306()
{
    bool b;
    b = this.Method3("a\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method307()
{
    bool b;
    b = this.Method3("y.");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method308()
{
    bool b;
    b = this.Method3("f\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method309()
{
    bool b;
    b = this.Method3("ft");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method310()
{
    bool b;
    b = this.Method3("d.\0rrr");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method311()
{
    bool b;
    b = this.Method3("d.-");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method312()
{
    bool b;
    b = this.Method3("a.-@");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method313()
{
    bool b;
    b = this.Method3("d.-...");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method314()
{
    bool b;
    b = this.Method3("ft\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method315()
{
    bool b;
    b = this.Method3("h");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method316()
{
    bool b;
    b = this.Method3("ht");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method317()
{
    bool b;
    b = this.Method3("a\000");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method318()
{
    bool b;
    b = this.Method3("h\00A");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method319()
{
    bool b;
    b = this.Method3("w\0wa");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method320()
{
    bool b;
    b = this.Method3("wwA\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method321()
{
    bool b;
    b = this.Method3("http");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method322()
{
    bool b;
    b = this.Method3("https");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method323()
{
    bool b;
    b = this.Method3("https\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method324()
{
    bool b;
    b = this.Method3("http\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method325()
{
    bool b;
    b = this.Method3("https:///");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method351()
{
    bool b;
    b = this.Method3("https:://\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method352()
{
    bool b;
    b = this.Method3("https://w\0\0\0\0\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method353()
{
    bool b;
    b = this.Method3("https://www\0\0\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method354()
{
    bool b;
    b = this.Method3("https:/\0\0\0");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method355()
{
    bool b;
    b = this.Method3("w\0w\\\0aa");
    Assert.AreEqual<bool>(false, b);
}
[TestMethod]
[PexGeneratedBy(typeof(Url))]
public void Method356()
{
    bool b;
    b = this.Method3("w\0w\\\0aa\0");
    Assert.AreEqual<bool>(false, b);
}
    }
}
