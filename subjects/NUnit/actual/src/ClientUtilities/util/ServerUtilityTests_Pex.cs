// ****************************************************************
// Copyright 2007, Charlie Poole
// This is free software licensed under the NUnit license. You may
// obtain a copy of the license at http://nunit.org/?p=license&r=2.4
// ****************************************************************
using System;
using System.Collections;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Tcp;
using NUnit.Framework;
using Microsoft.Win32;
using Microsoft.Pex.Framework;
using Microsoft.Pex.Graphs;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.Pex.Framework.Validation;

namespace NUnit.Util.Tests
{
	/// <summary>
	/// Summary description for RemotingUtilitiesTests.
	/// </summary>
    [PexClass(typeof(ServerUtilities))]
    public partial class ServerUtilityTests
	{
		TcpChannel channel1;
		TcpChannel channel2;

		[TestCleanup]
		public void ReleaseChannels()
		{
			ServerUtilities.SafeReleaseChannel( channel1 );
			ServerUtilities.SafeReleaseChannel( channel2 );
		}

		[PexMethod]
		public void CanGetTcpChannelOnSpecifiedPort()
		{
			channel1 = ServerUtilities.GetTcpChannel( "test", 1234 );
			PexAssert.AreEqual( "test", channel1.ChannelName );
			channel2 = ServerUtilities.GetTcpChannel( "test", 4321 );
            PexAssert.AreEqual("test", channel2.ChannelName);
            PexAssert.AreEqual(channel1, channel2);
            PexAssert.AreSame(channel1, channel2);
			ChannelDataStore cds = (ChannelDataStore)channel1.ChannelData;
            PexAssert.AreEqual("tcp://127.0.0.1:1234", cds.ChannelUris[0]);
		}

        [PexMethod]        
        public void CanGetTcpChannelOnSpecifiedPortPUT1([PexAssumeUnderTest]TcpChannel chan1, [PexAssumeUnderTest]TcpChannel chan2)
        {
            PexAssume.AreEqual(chan1.ChannelName, chan2.ChannelName);
            PexAssert.AreSame(chan1, chan2);
            PexAssert.AreEqual(chan1, chan2);
        }

        [PexMethod]
        [PexExpectedGoals]
        //An exception is thrown when the channel is null or re-registered.
        //It is very difficult to get the exception thrown here....
        public void CanGetTcpChannelOnSpecifiedPortPUT2(String name, int port)
        {            
            try
            {
                TcpChannel channel1 = ServerUtilities.GetTcpChannel(name, port);
                TcpChannel channel2 = ServerUtilities.GetTcpChannel(name, port);                
            }
            catch (Exception e)
            {
                throw new PexGoalException(e.Message);
            }
        }

        [PexMethod]
        public void CanGetTcpChannelOnSpecifiedPortPUT3([PexAssumeUnderTest]String name, int port)
        {
            PexAssume.IsFalse(name.Equals(""));
            //PexAssume.IsTrue(port > 1000 && port < 5000);
            TcpChannel chan1 = ServerUtilities.GetTcpChannel(name, port);
            ChannelDataStore cds = (ChannelDataStore)chan1.ChannelData;
            String uriInfo = "tcp://127.0.0.1:" + port;
            PexAssert.AreEqual(uriInfo, cds.ChannelUris[0]);
        }

        [PexMethod]
		public void CanGetTcpChannelOnUnpecifiedPort()
		{
			channel1 = ServerUtilities.GetTcpChannel( "test", 0 );
            PexAssert.AreEqual("test", channel1.ChannelName);
			channel2 = ServerUtilities.GetTcpChannel( "test", 0 );
            PexAssert.AreEqual("test", channel2.ChannelName);
            PexAssert.AreEqual(channel1, channel2);
            PexAssert.AreSame(channel1, channel2);
		}

        [PexMethod]
        public void CanGetTcpChannelOnUnpecifiedPortPUT1([PexAssumeUnderTest]String name)
        {
            channel1 = ServerUtilities.GetTcpChannel(name, 0);
            channel2 = ServerUtilities.GetTcpChannel(name, 0);
            PexAssert.AreEqual(channel1, channel2);
            PexAssert.AreSame(channel1, channel2);
        }
	}
}
