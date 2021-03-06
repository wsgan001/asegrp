// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using System.IO;
using Microsoft.Pex.Framework.Exceptions;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;
using NUnit.Util.PUTs;

namespace NUnit.Util.PUTs
{
    public partial class RegistrySettingsStorageTests_Pex
    {
        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void StorageHasCorrectKeyPUT101()
        {
            this.StorageHasCorrectKeyPUT1(
            "t\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0"
            );
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        public void StorageHasCorrectKeyPUT102()
        {
            this.StorageHasCorrectKeyPUT1("t");
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(IOException))]
        public void StorageHasCorrectKeyPUT103()
        {
            this.StorageHasCorrectKeyPUT1("\\t");
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void StorageHasCorrectKeyPUT104()
        {
            this.StorageHasCorrectKeyPUT1("t\0\\");
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void StorageHasCorrectKeyPUT105()
        {
            this.StorageHasCorrectKeyPUT1("t\\\\");
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(IOException))]
        public void StorageHasCorrectKeyPUT106()
        {
            this.StorageHasCorrectKeyPUT1("\\\\t");
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(IOException))]
        public void StorageHasCorrectKeyPUT107()
        {
            this.StorageHasCorrectKeyPUT1("\\t\\\\");
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void StorageHasCorrectKeyPUT108()
        {
            this.StorageHasCorrectKeyPUT1("\0\\\\\\t");
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void StorageHasCorrectKeyPUT109()
        {
            this.StorageHasCorrectKeyPUT1(
            "t\0\\\\\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\\\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\\\\\\\\\\\\"
            );
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void StorageHasCorrectKeyPUT110()
        {
            this.StorageHasCorrectKeyPUT1("t\0\\\\\0\0\0\0\\\0\\\\\0\\");
        }
    }
}
