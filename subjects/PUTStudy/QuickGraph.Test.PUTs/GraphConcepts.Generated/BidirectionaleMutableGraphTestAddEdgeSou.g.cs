// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;
using QuickGraph.Exceptions;
using QuickGraph.Providers;
using QuickGraph.Representations;
using QuickGraphNUnit.GraphConcepts;

namespace QuickGraphNUnit.GraphConcepts
{
    public partial class BidirectionaleMutableGraphTest
    {
        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeSourceNotFound01()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, false, 16);
            ((GenericMutableGraphTestNEW)this).AddEdgeSourceNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeSourceNotFound02()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateCyclicGraph(s0, true, 49);
            ((GenericMutableGraphTestNEW)this).AddEdgeSourceNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeSourceNotFound03()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, true, 49);
            ((GenericMutableGraphTestNEW)this).AddEdgeSourceNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeSourceNotFound04()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, false, 6);
            ((GenericMutableGraphTestNEW)this).AddEdgeSourceNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeSourceNotFound05()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateCyclicGraph(s0, true, 6);
            ((GenericMutableGraphTestNEW)this).AddEdgeSourceNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeSourceNotFound06()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateCyclicGraph(s0, true, 47);
            ((GenericMutableGraphTestNEW)this).AddEdgeSourceNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeSourceNotFound07()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateCyclicGraph(s0, true, 45);
            ((GenericMutableGraphTestNEW)this).AddEdgeSourceNotFound(adjacencyGraph);
        }
    }
}
