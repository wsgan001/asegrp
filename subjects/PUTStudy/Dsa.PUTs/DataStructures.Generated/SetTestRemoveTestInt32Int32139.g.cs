// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using Dsa.PUTs.DataStructures;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;

namespace Dsa.PUTs.DataStructures
{
    public partial class SetTest
    {
        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest22()
        {
            int[] ints = new int[1];
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest23()
        {
            int[] ints = new int[2];
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        [PexRaisedException(typeof(NullReferenceException))]
        public void RemoveTest24()
        {
            int[] ints = new int[2];
            ints[1] = -2147483647;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        [PexRaisedException(typeof(NullReferenceException))]
        public void RemoveTest25()
        {
            int[] ints = new int[2];
            ints[1] = 1;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest26()
        {
            int[] ints = new int[2];
            ints[1] = 1;
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest27()
        {
            int[] ints = new int[2];
            ints[0] = 1;
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest28()
        {
            int[] ints = new int[3];
            ints[1] = -2147483647;
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest29()
        {
            int[] ints = new int[3];
            ints[1] = -2147483647;
            ints[2] = 1;
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest30()
        {
            int[] ints = new int[3];
            ints[0] = 1412440065;
            ints[1] = -734527484;
            ints[2] = 1144528896;
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest31()
        {
            int[] ints = new int[3];
            ints[0] = 1678770178;
            ints[1] = 606093313;
            ints[2] = -451936253;
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest32()
        {
            int[] ints = new int[3];
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        [PexRaisedException(typeof(NullReferenceException))]
        public void RemoveTest33()
        {
            int[] ints = new int[4];
            ints[0] = 1678770178;
            ints[1] = 606093313;
            ints[2] = -451936253;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest34()
        {
            int[] ints = new int[4];
            ints[1] = -2147483647;
            ints[2] = 1;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest35()
        {
            int[] ints = new int[5];
            ints[0] = 268566528;
            ints[1] = -1878917119;
            ints[2] = -2143158264;
            ints[3] = -2147352565;
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest36()
        {
            int[] ints = new int[4];
            ints[0] = -634847228;
            ints[1] = 1510612993;
            ints[2] = -894959615;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest37()
        {
            int[] ints = new int[4];
            ints[0] = 16384;
            ints[1] = 32772;
            ints[2] = -2147467263;
            ints[3] = 1073741825;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest38()
        {
            int[] ints = new int[5];
            ints[0] = 16384;
            ints[1] = 32772;
            ints[2] = -2147467263;
            ints[3] = 1073741825;
            this.RemoveTest(ints, 1);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest39()
        {
            int[] ints = new int[6];
            ints[0] = 16384;
            ints[1] = 32772;
            ints[2] = -2147467263;
            ints[3] = 1073741825;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest40()
        {
            int[] ints = new int[6];
            ints[0] = -2147467264;
            ints[1] = -1073741823;
            ints[2] = -2147483647;
            ints[3] = 1073741824;
            ints[4] = -2147475200;
            ints[5] = -2147475456;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest41()
        {
            int[] ints = new int[6];
            ints[0] = -2037217620;
            ints[1] = -426702107;
            ints[2] = -2080242015;
            ints[3] = 914949542;
            ints[4] = -2046294368;
            ints[5] = -2037775680;
            this.RemoveTest(ints, 0);
        }

        [Test]
        [PexGeneratedBy(typeof(SetTest), IsCustomInput = true)]
        public void RemoveTest42()
        {
            int[] ints = new int[5];
            ints[0] = -167772159;
            ints[1] = 1979711488;
            ints[2] = -167772159;
            ints[3] = -1140850624;
            this.RemoveTest(ints, 1);
        }
    }
}
