using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;
using CloneFinder;

namespace AutomatonToCode
{
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

