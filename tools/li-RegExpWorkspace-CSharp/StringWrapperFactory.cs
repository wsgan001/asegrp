using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Factories;
using CloneFinder;

namespace AutomatonToCode
{
    [PexFactoryClass]
    public class StringWrapperFactory
    {
        [PexFactoryMethod(typeof(StringWrapper))]
        public static StringWrapper Create()
        {
            StringWrapper si = new StringWrapper();
            si.setS(RandomString.RandomStrings());
            return si;
        }
    }
}

