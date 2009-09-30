// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;
using NUnit.Util;
using NUnit.Util.PUTs;

namespace NUnit.Util.PUTs
{
    public partial class MemorySettingsStorageTests
    {
        [Test]
        [PexGeneratedBy(typeof(MemorySettingsStorageTests))]
        public void SaveAndLoadSettingsPUT101()
        {
            using (PexDisposableContext disposables = PexDisposableContext.Create())
            {
              MemorySettingsStorage memorySettingsStorage;
              string[] ss = new string[3];
              object[] os = new object[3];
              ss[0] = "\0\0";
              ss[1] = "\0\0";
              ss[2] = "\0\0";
              os[0] = "\0\0";
              object boxi = (object)(default(int));
              os[1] = boxi;
              object boxb = (object)(default(bool));
              os[2] = boxb;
              memorySettingsStorage = MemorySettingsStorageFactory.Create(ss, os);
              disposables.Add((IDisposable)memorySettingsStorage);
              object s0 = new object();
              this.SaveAndLoadSettingsPUT1(memorySettingsStorage, "", s0);
              disposables.Dispose();
              PexAssert.IsNotNull((object)memorySettingsStorage);
            }
        }
    }
}
