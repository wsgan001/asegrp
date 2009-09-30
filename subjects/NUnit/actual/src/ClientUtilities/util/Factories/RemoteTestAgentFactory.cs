using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Pex.Framework.Factories;
using Microsoft.Pex.Framework;

namespace NUnit.Util.Factories
{
    [PexFactoryClass]
    public partial class RemoteTestAgentFactory
    {
       [PexFactoryMethod(typeof(RemoteTestAgent))]
       public static RemoteTestAgent Create([PexAssumeUnderTest] String name){
           PexAssume.IsNotNullOrEmpty(name);
           return new RemoteTestAgent(name);
       }
    }
    
}
