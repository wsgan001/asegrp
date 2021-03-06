using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Factories;
using System;
using NUnit.Util;

namespace NUnit.Util
{
    [PexFactoryClass]
    public partial class SettingsGroupFactory
    {
        [PexFactoryMethod(typeof(SettingsGroup))]
        public static SettingsGroup Create([PexAssumeUnderTest]String[] settingNames, [PexAssumeUnderTest] Object[] settingValues)
        {
            PexAssume.IsTrue(settingNames.Length == settingValues.Length);
            PexAssume.IsTrue(settingNames.Length > 0);

            MemorySettingsStorage storage = new MemorySettingsStorage();
            SettingsGroup settingsGroup = new SettingsGroup(storage);

            for (int count = 0; count < settingNames.Length; count++)
            {
                PexAssume.IsTrue(settingValues[count] is string || settingValues[count] is int || settingValues[count] is bool || settingValues[count] is Enum);
                //PexAssume.IsTrue(settingNames[count] != null && !settingNames[count].Equals(""));
                settingsGroup.SaveSetting(settingNames[count], settingValues[count]);
            }
            
            return settingsGroup;
        }
    }
}
