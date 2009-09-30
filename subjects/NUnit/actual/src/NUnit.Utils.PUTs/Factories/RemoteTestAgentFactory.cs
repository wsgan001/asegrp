using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Pex.Framework;

namespace NUnit.Util.PUTs
{
    public static class RemoteTestAgentFactory
    {
       [PexFactoryMethod(typeof(RemoteTestAgent))]
       public static RemoteTestAgent Create([PexAssumeUnderTest] String name){
           PexAssume.IsNotNullOrEmpty(name);
           return new RemoteTestAgent(name);
       }
    }
    
}
