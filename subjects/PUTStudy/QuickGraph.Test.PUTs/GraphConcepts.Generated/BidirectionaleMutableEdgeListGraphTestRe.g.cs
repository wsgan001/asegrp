// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using System;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;
using QuickGraph.Providers;
using QuickGraph.Representations;
using QuickGraphNUnit.GraphConcepts;

namespace QuickGraphNUnit.GraphConcepts
{
    public partial class BidirectionaleMutableEdgeListGraphTest
    {
        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableEdgeListGraphTest))]
        [ExpectedException(typeof(ArgumentNullException))]
        public void RemoveEdgeIf01()
        {
            ((GenericMutableEdgeListGraphTest)this).RemoveEdgeIf((BidirectionalGraph)null);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableEdgeListGraphTest))]
        public void RemoveEdgeIf02()
        {
            BidirectionalGraph bidirectionalGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            bidirectionalGraph = BidirectionalGraphFactory.CreateAcyclicGraph(s0, false, 0);
            ((GenericMutableEdgeListGraphTest)this).RemoveEdgeIf(bidirectionalGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableEdgeListGraphTest))]
        public void RemoveEdgeIf03()
        {
            BidirectionalGraph bidirectionalGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            bidirectionalGraph = BidirectionalGraphFactory.CreateAcyclicGraph(s0, false, 16);
            ((GenericMutableEdgeListGraphTest)this).RemoveEdgeIf(bidirectionalGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableEdgeListGraphTest))]
        public void RemoveEdgeIf04()
        {
            BidirectionalGraph bidirectionalGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            bidirectionalGraph = BidirectionalGraphFactory.CreateCyclicGraph(s0, false, 0);
            ((GenericMutableEdgeListGraphTest)this).RemoveEdgeIf(bidirectionalGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableEdgeListGraphTest))]
        public void RemoveEdgeIf05()
        {
            BidirectionalGraph bidirectionalGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            bidirectionalGraph = BidirectionalGraphFactory.CreateCyclicGraph(s0, false, 16);
            ((GenericMutableEdgeListGraphTest)this).RemoveEdgeIf(bidirectionalGraph);
        }
    }
}
