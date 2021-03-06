// <copyright file="AdjacencyGraphFactory.cs" company=""></copyright>

using System;
using Microsoft.Pex.Framework;
using QuickGraph.Representations;
using QuickGraph.Concepts.Providers;
using QuickGraph.Providers;
using QuickGraph.Concepts;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace QuickGraph.Representations
{
    
    [PexClass]
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

     //   [PexFactoryMethod(typeof(AdjacencyGraph))]
        public static AdjacencyGraph CreateAcyclicGraph1(bool allowParallelEdges_b, int numberOfVertices, bool toNull)
        {
            AdjacencyGraph adjacencyGraph;
            if (toNull)
            {
                adjacencyGraph = new AdjacencyGraph(null, allowParallelEdges_b);
                return null;
            }
            
            PexAssume.IsTrue(numberOfVertices < 10);

            adjacencyGraph = new AdjacencyGraph(new VertexAndEdgeProvider(), allowParallelEdges_b);

           /* IVertex u = adjacencyGraph.AddVertex();
            IVertex v = adjacencyGraph.AddVertex();
            IVertex w = adjacencyGraph.AddVertex();
            adjacencyGraph.AddEdge(u, v);
            adjacencyGraph.AddEdge(v, w);
            adjacencyGraph.AddEdge(w, u);*/

            //Adding remaining number of vertices
            for (int count = 0; count < numberOfVertices; count++)
            {
                adjacencyGraph.AddVertex();
            }

            return adjacencyGraph;
        }
    }
}
