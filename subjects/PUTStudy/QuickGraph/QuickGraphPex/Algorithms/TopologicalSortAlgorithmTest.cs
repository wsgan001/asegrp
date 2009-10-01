///// QuickGraph Library 
///// 
///// Copyright (c) 2003 Jonathan de Halleux
/////
///// This software is provided 'as-is', without any express or implied warranty. 
///// 
///// In no event will the authors be held liable for any damages arising from 
///// the use of this software.
///// Permission is granted to anyone to use this software for any purpose, 
///// including commercial applications, and to alter it and redistribute it 
///// freely, subject to the following restrictions:
/////
/////		1. The origin of this software must not be misrepresented; 
/////		you must not claim that you wrote the original software. 
/////		If you use this software in a product, an acknowledgment in the product 
/////		documentation would be appreciated but is not required.
/////
/////		2. Altered source versions must be plainly marked as such, and must 
/////		not be misrepresented as being the original software.
/////
/////		3. This notice may not be removed or altered from any source 
/////		distribution.
/////		
/////		QuickGraph Library HomePage: http://www.dotnetwiki.org
/////		Author: Jonathan de Halleux


//using System;
//using Microsoft.Pex.Framework.Validation;

//namespace QuickGraphNUnit.Algorithms
//{
//    using NUnit.Framework;
//    using QuickGraphNUnit.Generators;
//    using QuickGraph.Exceptions;
//    using QuickGraph.Representations;
//    using QuickGraph.Providers;
//    using QuickGraph.Concepts;
//    using QuickGraph.Concepts.Traversals;
//    using QuickGraph.Algorithms;
//    using System.Collections;
//    using Microsoft.Pex.Framework;
//    using Microsoft.Pex.Framework.Validation;
//    using QuickGraph;

//    [TestFixture]
//    [PexClass]
//    public partial class TopologicalSortAlgorithmTest
//    {
//        [PexMethod(MaxConstraintSolverTime = 2, MaxBranches = 20000)]
//        [PexAllowedException(typeof(NonAcyclicGraphException)), PexAllowedException(typeof(ArgumentNullException))]
//        /// Summary
//        /// Time: 23 min 45 sec (still more) "TODO" (Seems to have some bug in Pex)
//        /// Pattern: AAAA, Constructor test, State relation (as state is preserved in a hashtable and is verified again).
//        /// Reason for more time in writing PUT: Hit Pex limitations in floating point coversion and multiplication, addressed the issue
//        ///     by removing Hashtable from the parameter list
//        ///     
//        public void SortPUT(bool allowParallelEdges_b, int numberOfVertices, bool toNull, bool makeNull)
//        {
//            TopologicalSortAlgorithm topo = null;
//            AdjacencyGraph g = null;
//            if(!makeNull)
//                g = AdjacencyGraphFactory.CreateAcyclicGraph1(allowParallelEdges_b, numberOfVertices, toNull);
//            else
//                topo = new TopologicalSortAlgorithm(g);
//            Hashtable iv = new Hashtable();

//            int i = 0;
//            IVertex a = g.AddVertex();
//            iv[i++] = a;
//            IVertex b = g.AddVertex();
//            iv[i++] = b;
//            IVertex c = g.AddVertex();
//            iv[i++] = c;
//            IVertex d = g.AddVertex();
//            iv[i++] = d;
//            IVertex e = g.AddVertex();
//            iv[i++] = e;

//            g.AddEdge(a, b);
//            g.AddEdge(a, c);
//            g.AddEdge(b, c);
//            g.AddEdge(c, d);
//            g.AddEdge(a, e);

//            topo = new TopologicalSortAlgorithm(g);
//            topo.Compute();

//           for (int j = 0; j < topo.SortedVertices.Count; ++j)
//            {
//                //PexAssert.AreEqual((IVertex)iv[j], topo.SortedVertices[j]);
//                PexObserve.ValueForViewing<IVertex>("Sorted Vertex", (IVertex)topo.SortedVertices[j]);
//            }
//        }

//        [PexMethod(MaxBranches = 20000)]
//        [PexAllowedException(typeof(NonAcyclicGraphException))]
//        /// Summary
//        /// Time: 1 min 
//        /// Pattern: Constructor test, Allowed Exception
//        public void SortCyclicPUT([PexAssumeUnderTest]AdjacencyGraph g)
//        {			
//            IVertex a = g.AddVertex();
//            IVertex b = g.AddVertex();
//            IVertex c = g.AddVertex();
//            IVertex d = g.AddVertex();
//            IVertex e = g.AddVertex();

//            g.AddEdge(a,b);
//            g.AddEdge(a,c);
//            g.AddEdge(b,c);
//            g.AddEdge(c,d);				
//            g.AddEdge(a,e);				
//            g.AddEdge(c,a);				

//            TopologicalSortAlgorithm topo = new TopologicalSortAlgorithm(g);
//            topo.Compute();
//        }
//    }
//}
