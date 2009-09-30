// <copyright file="$(FileName)" company="">Copyright ©  2009</copyright>
// <auto-generated>This code was generated by a Microsoft Pex.
// 
// Changes to this file may cause incorrect behavior and will be lost if
// the code is regenerated.</auto-generated>
using Microsoft.Pex.Framework;
using Microsoft.Pex.Framework.Generated;
using NUnit.Framework;
using QuickGraph.Providers;
using QuickGraph.Representations;
using QuickGraphNUnit.GraphConcepts;

namespace QuickGraphNUnit.GraphConcepts
{
    public partial class BidirectionaleMutableGraphTest
    {
        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        public void ClearVertexSourceTarget01()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, false, 0);
            ((GenericMutableGraphTestNEW)this).ClearVertexSourceTarget(adjacencyGraph);
            PexAssert.IsNotNull((object)adjacencyGraph);
            PexAssert.AreEqual<bool>(true, adjacencyGraph.IsDirected);
            PexAssert.IsNotNull(adjacencyGraph.Provider);
            PexAssert.IsNotNull(adjacencyGraph.Edges);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        public void ClearVertexSourceTarget02()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, true, 0);
            ((GenericMutableGraphTestNEW)this).ClearVertexSourceTarget(adjacencyGraph);
            PexAssert.IsNotNull((object)adjacencyGraph);
            PexAssert.AreEqual<bool>(true, adjacencyGraph.IsDirected);
            PexAssert.IsNotNull(adjacencyGraph.Provider);
            PexAssert.IsNotNull(adjacencyGraph.Edges);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        public void ClearVertexSourceTarget03()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateAcyclicGraph(s0, false, 16);
            ((GenericMutableGraphTestNEW)this).ClearVertexSourceTarget(adjacencyGraph);
            PexAssert.IsNotNull((object)adjacencyGraph);
            PexAssert.AreEqual<bool>(true, adjacencyGraph.IsDirected);
            PexAssert.IsNotNull(adjacencyGraph.Provider);
            PexAssert.IsNotNull(adjacencyGraph.Edges);
        }

        [Test]
        [PexGeneratedBy(typeof(BidirectionaleMutableGraphTest))]
        public void ClearVertexSourceTarget04()
        {
            AdjacencyGraph adjacencyGraph;
            VertexAndEdgeProvider s0 = new VertexAndEdgeProvider();
            adjacencyGraph = AdjacencyGraphFactory.CreateCyclicGraph(s0, false, 0);
            ((GenericMutableGraphTestNEW)this).ClearVertexSourceTarget(adjacencyGraph);
            PexAssert.IsNotNull((object)adjacencyGraph);
            PexAssert.AreEqual<bool>(true, adjacencyGraph.IsDirected);
            PexAssert.IsNotNull(adjacencyGraph.Provider);
            PexAssert.IsNotNull(adjacencyGraph.Edges);
        }
    }
}
