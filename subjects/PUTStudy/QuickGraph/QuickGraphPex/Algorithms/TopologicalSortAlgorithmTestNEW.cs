using System;
using Microsoft.Pex.Framework.Validation;

namespace QuickGraphNUnit.Algorithms
{
	using NUnit.Framework;
	using QuickGraphNUnit.Generators;
	using QuickGraph.Exceptions;
	using QuickGraph.Representations;
	using QuickGraph.Providers;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Algorithms;
	using System.Collections;
    using Microsoft.Pex.Framework;
    using Microsoft.Pex.Framework.Validation;
    using QuickGraph;
   // using Microsoft.VisualStudio.TestTools.UnitTesting;
    using System.Collections.Generic;

	[TestFixture]
    //[TestClass]
    [PexClass]
    public partial class TopologicalSortAlgorithmTestNEW
	{
        [PexMethod(MaxConstraintSolverTime = 2, MaxBranches = 20000)]
        [PexAllowedException(typeof(NonAcyclicGraphException)), PexAllowedException(typeof(ArgumentNullException))]
		public void SortPUT_NEW(bool allowParallelEdges_b, int numberOfVertices, bool toNull, bool makeNull)
		{
            TopologicalSortAlgorithm topo = null;
            AdjacencyGraph g = null;
            if(!makeNull)
                g = AdjacencyGraphFactory.CreateAcyclicGraph1(allowParallelEdges_b, numberOfVertices, toNull);
            else
                topo = new TopologicalSortAlgorithm(g);
            Hashtable iv = new Hashtable();

            int i = 0;
            IVertex a = g.AddVertex();
            iv[i++] = a;
            IVertex b = g.AddVertex();
            iv[i++] = b;
            IVertex c = g.AddVertex();
            iv[i++] = c;
            IVertex d = g.AddVertex();
            iv[i++] = d;
            IVertex e = g.AddVertex();
            iv[i++] = e;

            g.AddEdge(a, b);
            g.AddEdge(a, c);
            g.AddEdge(b, c);
            g.AddEdge(c, d);
            g.AddEdge(a, e);

            topo = new TopologicalSortAlgorithm(g);
            topo.Compute();

           for (int j = 0; j < topo.SortedVertices.Count; ++j)
            {
                //PexAssert.AreEqual((IVertex)iv[j], topo.SortedVertices[j]);
                PexObserve.ValueForViewing<IVertex>("Sorted Vertex", (IVertex)topo.SortedVertices[j]);
            }
		}

        [PexMethod(MaxBranches = 20000)]
        [PexAllowedException(typeof(NonAcyclicGraphException)), PexAllowedException(typeof(ArgumentNullException))]
        public void SortCyclicPUT_NEW(AdjacencyGraph g, IVertex[] listOfVertices, bool rndConstructor)
		{
            TopologicalSortAlgorithm topo;// = new TopologicalSortAlgorithm(g);
            List<IVertex> list = null;
            if (listOfVertices != null && g!= null)
            {
                list = new List<IVertex>();
                foreach (IVertex v in listOfVertices)
                {
                    IVertex a = g.AddVertex();
                    list.Add(v);
                }
                g = createCycle(g);
            }
            if (rndConstructor)
                topo = new TopologicalSortAlgorithm(g, list);
            else
                topo = new TopologicalSortAlgorithm(g);
                    
			topo.Compute();
            for (int j = 0; j < topo.SortedVertices.Count; ++j)
            {
                PexObserve.ValueForViewing<IVertex>("Sorted Vertex", (IVertex)topo.SortedVertices[j]);
            }

		}

        public AdjacencyGraph createCycle(AdjacencyGraph g)
        {
            foreach (IVertex v1 in g.Vertices)
            {
                foreach (IVertex v2 in g.Vertices)
                {
                    if (v1 != v2 && g.ContainsEdge(v1, v2))
                    {
                        g.AddEdge(v1, v2);
                    }
                }
            }
            return g;
        }

        public void SortCyclicPUT_NEW1(AdjacencyGraph g, bool rndConstructor, bool allowParallelEdges, int numberOfVertices, bool toNull)
        {
            g = AdjacencyGraphFactory.CreateAcyclicGraph1(allowParallelEdges, numberOfVertices, toNull);
            TopologicalSortAlgorithm topo;
            List<IVertex> list = null;
            if (g != null && g.VerticesCount > 0)
            {
                list = new List<IVertex>();
                foreach (IVertex v in g.Vertices)
                {
                    IVertex a = g.AddVertex();
                    list.Add(v);
                }
                g = createCycle(g);
            }
            if (rndConstructor)
                topo = new TopologicalSortAlgorithm(g, list);
            else
                topo = new TopologicalSortAlgorithm(g);

            topo.Compute();
            for (int j = 0; j < topo.SortedVertices.Count; ++j)
            {
                PexObserve.ValueForViewing<IVertex>("Sorted Vertex", (IVertex)topo.SortedVertices[j]);
            }

        }

	}
}
