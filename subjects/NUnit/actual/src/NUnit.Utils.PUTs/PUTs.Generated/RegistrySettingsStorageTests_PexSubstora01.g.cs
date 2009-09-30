// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using System.IO;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;
using NUnit.Util.PUTs;

namespace NUnit.Util.PUTs
{
    public partial class RegistrySettingsStorageTests_Pex
    {
        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        public void SubstorageSettingsPUT201()
        {
            string[] ss = new string[0];
            object[] os = new object[0];
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT202()
        {
            string[] ss = new string[0];
            object[] os = new object[0];
            this.SubstorageSettingsPUT2(new string('\0', 256), ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        public void SubstorageSettingsPUT203()
        {
            string[] ss = new string[0];
            object[] os = new object[0];
            this.SubstorageSettingsPUT2("\\", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT204()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] =
              "t\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
            os[0] =
              "t\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        public void SubstorageSettingsPUT205()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] = "t";
            os[0] = "t";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT206()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] =
              ".tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt";
            os[0] =
              ".tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT207()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] =
              "t...............................................................................................................................................................................................................................................................................................................................";
            os[0] = "\\";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(IOException))]
        public void SubstorageSettingsPUT208()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] = "\\t.";
            os[0] = "\\\\";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        public void SubstorageSettingsPUT209()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] = ".t";
            os[0] = ".t";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT210()
        {
            string[] ss = new string[0];
            object[] os = new object[0];
            this.SubstorageSettingsPUT2
                ("\\\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\\\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\", 
                ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(IOException))]
        public void SubstorageSettingsPUT211()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] = "\\\\t.";
            os[0] = "\\\\\\";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT212()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] =
              ".\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\t";
            os[0] = new string('\\', 257);
            this.SubstorageSettingsPUT2("\\", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT213()
        {
            string[] ss = new string[2];
            object[] os = new object[2];
            ss[0] =
              "t\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
            ss[1] = "t";
            os[0] = "t";
            os[1] = "t";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT214()
        {
            string[] ss = new string[2];
            object[] os = new object[2];
            ss[0] =
              "t\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
            ss[1] = "t";
            os[0] = "t";
            object boxi = (object)(default(int));
            os[1] = boxi;
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT215()
        {
            string[] ss = new string[2];
            object[] os = new object[2];
            ss[0] = "t";
            ss[1] =
              "t\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
            os[0] =
              "t\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
            os[1] =
              "t\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(IOException))]
        public void SubstorageSettingsPUT216()
        {
            string[] ss = new string[0];
            object[] os = new object[0];
            this.SubstorageSettingsPUT2("\\\\\\", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        public void SubstorageSettingsPUT217()
        {
            string[] ss = new string[1];
            object[] os = new object[1];
            ss[0] = "t";
            object boxi = (object)(default(int));
            os[0] = boxi;
            this.SubstorageSettingsPUT2("", ss, os);
        }

        [Test]
        [PexGeneratedBy(typeof(RegistrySettingsStorageTests_Pex))]
        [PexRaisedException(typeof(ArgumentException))]
        public void SubstorageSettingsPUT218()
        {
            string[] ss = new string[6];
            object[] os = new object[6];
            ss[0] = "t";
            ss[1] = "t";
            ss[2] = "t";
            ss[3] = "t";
            ss[4] = "t";
            ss[5] = "t";
            os[0] = "\0";
            os[1] = "\0";
            object boxi = (object)(default(int));
            os[2] = boxi;
            object boxi1 = (object)(default(int));
            os[3] = boxi1;
            object boxi2 = (object)(default(int));
            os[4] = boxi2;
            object boxi3 = (object)(default(int));
            os[5] = boxi3;
            this.SubstorageSettingsPUT2(new string('\0', 256), ss, os);
        }
    }
}
