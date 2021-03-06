using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Factories;
using System;
using NUnit.Util;

namespace NUnit.Util
{
    [PexFactoryClass]
    public partial class MemorySettingsStorageFactory
    {
        [PexFactoryMethod(typeof(MemorySettingsStorage))]
        public static MemorySettingsStorage Create([PexAssumeUnderTest]String[] settingNames, [PexAssumeUnderTest] Object[] settingValues)
        {
            PexAssume.IsTrue(settingNames.Length == settingValues.Length);
            PexAssume.IsTrue(settingNames.Length > 0);

            MemorySettingsStorage memorySettingsStorage = new MemorySettingsStorage();
            for (int count = 0; count < settingNames.Length; count++)
            {
                PexAssume.IsTrue(settingValues[count] is string || settingValues[count] is int || settingValues[count] is bool || settingValues[count] is Enum);
                memorySettingsStorage.SaveSetting(settingNames[count], settingValues[count]);
            }
            return memorySettingsStorage;
        }

    }
}
