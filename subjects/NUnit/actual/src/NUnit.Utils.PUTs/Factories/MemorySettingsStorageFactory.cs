using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework;
using System;
using NUnit.Util;

namespace NUnit.Util.PUTs
{
    public static partial class MemorySettingsStorageFactory
    {
        [PexFactoryMethod(typeof(MemorySettingsStorage))]
        public static MemorySettingsStorage Create([PexAssumeUnderTest]String[] settingNames, [PexAssumeNotNull] Object[] settingValues)
        {
            PexAssume.IsTrue(settingNames.Length == settingValues.Length);
            PexAssume.IsTrue(settingNames.Length > 0);

            MemorySettingsStorage memorySettingsStorage = new MemorySettingsStorage();
            for (int count = 0; count < settingNames.Length; count++)
            {
                PexAssume.IsTrue(settingValues[count] is string || settingValues[count] is int || settingValues[count] is bool);
                memorySettingsStorage.SaveSetting(settingNames[count], settingValues[count]);
            }
            return memorySettingsStorage;
        }

    }
}
