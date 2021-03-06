// <copyright file="AdjacencyGraphFactory.cs" company=""></copyright>

using System;
using Microsoft.Pex.Framework;
using QuickGraph.Representations;
using QuickGraph.Concepts.Providers;
using QuickGraph.Providers;
using QuickGraph.Concepts;

namespace QuickGraph.Representations
{
    /// <summary>A factory for QuickGraph.Representations.AdjacencyGraph instances</summary>
    public static partial class BidirectionalGraphFactory
    {
        /// <summary>A factory for QuickGraph.Representations.AdjacencyGraph instances</summary>
        /// A factory method for the acyclic graph
        [PexFactoryMethod(typeof(BidirectionalGraph))]
        public static BidirectionalGraph CreateAcyclicGraph(
            VertexAndEdgeProvider provider_iVertexAndEdgeProvider,
            bool allowParallelEdges_b,
            int numberOfVertices)
        {
            PexAssume.IsTrue(numberOfVertices < 50);

            BidirectionalGraph biGraph
               = new BidirectionalGraph(provider_iVertexAndEdgeProvider, allowParallelEdges_b);

            IVertex u = biGraph.AddVertex();
            IVertex v = biGraph.AddVertex();
            IVertex w = biGraph.AddVertex();
            biGraph.AddEdge(u, v);
            biGraph.AddEdge(v, w);
            biGraph.AddEdge(u, w);

            //Adding remaining number of vertices
            for (int count = 3; count < numberOfVertices; count++)
            {
                biGraph.AddVertex(); 
            }
            
            return biGraph;
        }

        /// <summary>A factory for QuickGraph.Representations.AdjacencyGraph instances</summary>
        /// A factory method for the acyclic graph
        [PexFactoryMethod(typeof(BidirectionalGraph))]
        public static BidirectionalGraph CreateCyclicGraph(
            VertexAndEdgeProvider provider_iVertexAndEdgeProvider,
            bool allowParallelEdges_b,
            int numberOfVertices)
        {
            PexAssume.IsTrue(numberOfVertices < 50);

            BidirectionalGraph biGraph
               = new BidirectionalGraph(provider_iVertexAndEdgeProvider, allowParallelEdges_b);

            IVertex u = biGraph.AddVertex();
            IVertex v = biGraph.AddVertex();
            IVertex w = biGraph.AddVertex();
            biGraph.AddEdge(u, v);
            biGraph.AddEdge(v, w);
            biGraph.AddEdge(w, u);

            //Adding remaining number of vertices
            for (int count = 3; count < numberOfVertices; count++)
            {
                biGraph.AddVertex();
            }

            return biGraph;
        }
    }
}
