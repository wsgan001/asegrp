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
    public static partial class AdjacencyGraphFactory
    {
        /// <summary>A factory for QuickGraph.Representations.AdjacencyGraph instances</summary>
        /// A factory method for the acyclic graph
        [PexFactoryMethod(typeof(AdjacencyGraph))]
        public static AdjacencyGraph CreateAcyclicGraph(
            VertexAndEdgeProvider provider_iVertexAndEdgeProvider,
            bool allowParallelEdges_b,
            int numberOfVertices            
        )
        {
            PexAssume.IsTrue(numberOfVertices < 50);

            AdjacencyGraph adjacencyGraph
               = new AdjacencyGraph(provider_iVertexAndEdgeProvider, allowParallelEdges_b);

            IVertex u = adjacencyGraph.AddVertex();
            IVertex v = adjacencyGraph.AddVertex();
            IVertex w = adjacencyGraph.AddVertex();
            adjacencyGraph.AddEdge(u, v);
            adjacencyGraph.AddEdge(v, w);
            adjacencyGraph.AddEdge(u, w);

            //Adding remaining number of vertices
            for (int count = 3; count < numberOfVertices; count++)
            {
                adjacencyGraph.AddVertex(); 
            }
            
            return adjacencyGraph;
        }

        /// <summary>A factory for QuickGraph.Representations.AdjacencyGraph instances</summary>
        /// A factory method for the acyclic graph
        [PexFactoryMethod(typeof(AdjacencyGraph))]
        public static AdjacencyGraph CreateCyclicGraph(
            VertexAndEdgeProvider provider_iVertexAndEdgeProvider,
            bool allowParallelEdges_b,
            int numberOfVertices            
        )
        {
            PexAssume.IsTrue(numberOfVertices < 50);

            AdjacencyGraph adjacencyGraph
               = new AdjacencyGraph(provider_iVertexAndEdgeProvider, allowParallelEdges_b);

            IVertex u = adjacencyGraph.AddVertex();
            IVertex v = adjacencyGraph.AddVertex();
            IVertex w = adjacencyGraph.AddVertex();
            adjacencyGraph.AddEdge(u, v);
            adjacencyGraph.AddEdge(v, w);
            adjacencyGraph.AddEdge(w, u);

            //Adding remaining number of vertices
            for (int count = 3; count < numberOfVertices; count++)
            {
                adjacencyGraph.AddVertex();
            }

            return adjacencyGraph;
        }
    }
}
