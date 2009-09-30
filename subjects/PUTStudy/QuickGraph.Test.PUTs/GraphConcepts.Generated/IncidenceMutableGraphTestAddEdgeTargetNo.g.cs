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
    public partial class IncidenceMutableGraphTest
    {
        [Test]
        [PexGeneratedBy(typeof(IncidenceMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeTargetNotFound01()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, false, 0);
            ((GenericMutableGraphTestNEW)this).AddEdgeTargetNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(IncidenceMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeTargetNotFound02()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, true, 7);
            ((GenericMutableGraphTestNEW)this).AddEdgeTargetNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(IncidenceMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeTargetNotFound03()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, true, 8);
            ((GenericMutableGraphTestNEW)this).AddEdgeTargetNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(IncidenceMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeTargetNotFound04()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, false, 11);
            ((GenericMutableGraphTestNEW)this).AddEdgeTargetNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(IncidenceMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeTargetNotFound05()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, false, 12);
            ((GenericMutableGraphTestNEW)this).AddEdgeTargetNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(IncidenceMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeTargetNotFound06()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateCyclicGraph(s0, true, 16);
            ((GenericMutableGraphTestNEW)this).AddEdgeTargetNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(IncidenceMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeTargetNotFound07()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateCyclicGraph(s0, false, 5);
            ((GenericMutableGraphTestNEW)this).AddEdgeTargetNotFound(adjacencyGraph);
        }

        [Test]
        [PexGeneratedBy(typeof(IncidenceMutableGraphTest))]
        [ExpectedException(typeof(VertexNotFoundException))]
        public void AddEdgeTargetNotFound08()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateCyclicGraph(s0, false, 12);
            ((GenericMutableGraphTestNEW)this).AddEdgeTargetNotFound(adjacencyGraph);
        }
    }
}
