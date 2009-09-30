// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Exceptions;
using Microsoft.Pex.Framework.Generated;
using Microsoft.Win32;
using NUnit.Framework;
using NUnit.Util;
using NUnit.Util.PUTs;

namespace NUnit.Util.PUTs
{
    public partial class SettingsGroupTests
    {
        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void SubGroupSettingsPUT101()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              settingsGroup = new SettingsGroup((ISettingsStorage)null);
              disposables.Add((IDisposable)settingsGroup);
              object s0 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "", s0);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(NullReferenceException))]
        public void SubGroupSettingsPUT102()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage((RegistryKey)null);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(ObjectDisposedException))]
        public void SubGroupSettingsPUT103()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage(Registry.DynData);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT104()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              MemorySettingsStorage memorySettingsStorage = new MemorySettingsStorage();
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object s0 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "", s0);
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT105()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              XmlSettingsStorage xmlSettingsStorage;
              SettingsGroup settingsGroup;
              xmlSettingsStorage = new XmlSettingsStorage((string)null);
              disposables.Add((IDisposable)xmlSettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)xmlSettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object s0 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "", s0);
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(NullReferenceException))]
        public void SubGroupSettingsPUT106()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage((RegistryKey)null);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "\0", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(NullReferenceException))]
        public void SubGroupSettingsPUT107()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage((RegistryKey)null);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, ".", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(NullReferenceException))]
        public void SubGroupSettingsPUT108()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage((RegistryKey)null);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "\0..", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(ObjectDisposedException))]
        public void SubGroupSettingsPUT109()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage(Registry.DynData);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, ".", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT110()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[1];
              object[] os = new object[1];
              ss[0] = "";
              os[0] = "";
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object s0 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "", s0);
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT111()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[1];
              object[] os = new object[1];
              ss[0] = "";
              os[0] = "";
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              this.SubGroupSettingsPUT1(settingsGroup, "", "");
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT112()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[1];
              object[] os = new object[1];
              ss[0] = "\u0100\uf5a0\ub4ac\u661c";
              os[0] = "";
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              this.SubGroupSettingsPUT1(settingsGroup, "\u0100\uf5a0\ub4ac\u661c", 
              "\u0100\uf5a0\ub4ac\u661c\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100");
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT113()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[2];
              object[] os = new object[2];
              ss[0] = "";
              ss[1] = "";
              os[0] = "";
              object boxb = (object)(default(bool));
              os[1] = boxb;
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object s0 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "", s0);
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT114()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[2];
              object[] os = new object[2];
              ss[0] = "";
              ss[1] = "";
              os[0] = "";
              object boxb = (object)(default(bool));
              os[1] = boxb;
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object boxb1 = (object)(default(bool));
              this.SubGroupSettingsPUT1(settingsGroup, "", boxb1);
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT115()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[2];
              object[] os = new object[2];
              ss[0] = "";
              ss[1] = "";
              os[0] = "";
              object boxb = (object)(default(bool));
              os[1] = boxb;
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object boxb1 = (object)(default(bool));
              PexSafeHelpers.AssignBoxedValue<bool>(boxb1, true);
              this.SubGroupSettingsPUT1(settingsGroup, "", boxb1);
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT116()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[2];
              object[] os = new object[2];
              ss[0] = "";
              ss[1] = "";
              os[0] = "";
              object boxi = (object)(default(int));
              os[1] = boxi;
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object s0 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "", s0);
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        public void SubGroupSettingsPUT117()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[2];
              object[] os = new object[2];
              ss[0] = "";
              ss[1] = "";
              os[0] = "";
              object boxi = (object)(default(int));
              os[1] = boxi;
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object boxi1 = (object)(default(int));
              this.SubGroupSettingsPUT1(settingsGroup, "", boxi1);
              disposables.Dispose();
              PexAssert.IsNotNull((object)settingsGroup);
              PexAssert.IsNull(settingsGroup.Storage);
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(ObjectDisposedException))]
        public void SubGroupSettingsPUT118()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage(Registry.DynData);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "\0..", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(ObjectDisposedException))]
        public void SubGroupSettingsPUT119()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage(Registry.DynData);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "\\..", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(ObjectDisposedException))]
        public void SubGroupSettingsPUT120()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage(Registry.DynData);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "\\\0.", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(ObjectDisposedException))]
        public void SubGroupSettingsPUT121()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              SettingsGroup settingsGroup;
              RegistrySettingsStorage s0 = new RegistrySettingsStorage(Registry.DynData);
              settingsGroup = new SettingsGroup((ISettingsStorage)s0);
              disposables.Add((IDisposable)settingsGroup);
              object s1 = new object();
              this.SubGroupSettingsPUT1(settingsGroup, "\\\\.", s1);
              disposables.Dispose();
            }
        }

        [Test]
        [PexGeneratedBy(typeof(SettingsGroupTests))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void SubGroupSettingsPUT122()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              SettingsGroup settingsGroup;
              string[] ss = new string[2];
              object[] os = new object[2];
              ss[0] = "";
              ss[1] = "";
              os[0] = "";
              object boxb = (object)(default(bool));
              os[1] = boxb;
              PexSafeHelpers.AssignBoxedValue<bool>
                  (boxb, PexSafeHelpers.ByteToBoolean((byte)128));
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              settingsGroup = new SettingsGroup((ISettingsStorage)memorySettingsStorage);
              disposables.Add((IDisposable)settingsGroup);
              object boxb1 = (object)(default(bool));
              PexSafeHelpers.AssignBoxedValue<bool>
                  (boxb1, PexSafeHelpers.ByteToBoolean((byte)128));
              this.SubGroupSettingsPUT1(settingsGroup, "", boxb1);
              disposables.Dispose();
            }
        }
    }
}
