// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using Microsoft.Pex.Framework.Exceptions;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;
using NUnit.Util.PUTs;

namespace NUnit.Util.PUTs
{
    public partial class PathUtilTests_Windows
    {
        [Test]
        [PexGeneratedBy(typeof(PathUtilTests_Windows))]
        public void PathFromUriPUT101()
        {
            this.PathFromUriPUT1(".dll");
        }

        [Test]
        [PexGeneratedBy(typeof(PathUtilTests_Windows))]
        public void PathFromUriPUT102()
        {
            this.PathFromUriPUT1("l.dll");
        }

        [Test]
        [PexGeneratedBy(typeof(PathUtilTests_Windows))]
        public void PathFromUriPUT103()
        {
            this.PathFromUriPUT1("file:///.:.dll");
        }

        [Test]
        [PexGeneratedBy(typeof(PathUtilTests_Windows))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void PathFromUriPUT104()
        {
            this.PathFromUriPUT1("file:///.dll");
        }

        [Test]
        [PexGeneratedBy(typeof(PathUtilTests_Windows))]
        public void PathFromUriPUT105()
        {
            this.PathFromUriPUT1("file://..dll");
        }
    }
}
