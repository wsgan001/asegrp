using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Pex.Framework;

using Microsoft.Pex.Framework;
using Microsoft.Win32;

namespace NUnit.Util.PUTs
{
   // [PexFactoryClass]
    public static class NUnitRegistryFactory
    {
        //[PexFactoryMethod(typeof(RegistryKey))]
        public static RegistryKey Create(String[] nameOfKey, String[] name, Object[] value)
        {
            Console.WriteLine("DUMB IS HERE");
            PexAssume.IsTrue(nameOfKey.Length >= 1);
            PexAssume.IsTrue(nameOfKey.Length == name.Length);
            PexAssume.IsTrue(name.Length == value.Length);
            RegistryKey mainKey = Registry.CurrentUser.CreateSubKey(NUnitRegistry.TEST_KEY);
            mainKey.SetValue(name[0], value[0]);
            for (int k = 0; k < nameOfKey.Length; k++)
            {
                RegistryKey subKey = mainKey.CreateSubKey(nameOfKey[k]);
                subKey.SetValue(name[k + 1], value[k + 1]);
            }

            return mainKey;
        }

    }
}
