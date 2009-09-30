// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System.Runtime.Remoting.Channels.Tcp;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Exceptions;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;
using NUnit.Util.PUTs;

namespace NUnit.Util.PUTs
{
    public partial class ServerUtilityTests
    {
        [Test]
        [PexGeneratedBy(typeof(ServerUtilityTests))]
        public void CanGetTcpChannelOnSpecifiedPortPUT101()
        {
            TcpChannel tcpChannel;
            tcpChannel = new TcpChannel(0);
            this.CanGetTcpChannelOnSpecifiedPortPUT1(tcpChannel, tcpChannel);
            PexAssert.IsNotNull((object)tcpChannel);
            PexAssert.AreEqual<int>(1, tcpChannel.ChannelPriority);
            PexAssert.AreEqual<string>("tcp", tcpChannel.ChannelName);
            PexAssert.IsNotNull((object)tcpChannel);
            PexAssert.IsTrue(object.ReferenceEquals((object)tcpChannel, (object)tcpChannel));
        }

        [Test]
        [PexGeneratedBy(typeof(ServerUtilityTests))]
        [PexRaisedException(typeof(PexAssertFailedException))]
        public void CanGetTcpChannelOnSpecifiedPortPUT102()
        {
            TcpChannel tcpChannel;
            TcpChannel tcpChannel1;
            tcpChannel = new TcpChannel(int.MinValue);
            tcpChannel1 = new TcpChannel(0);
            this.CanGetTcpChannelOnSpecifiedPortPUT1(tcpChannel1, tcpChannel);
        }
    }
}
