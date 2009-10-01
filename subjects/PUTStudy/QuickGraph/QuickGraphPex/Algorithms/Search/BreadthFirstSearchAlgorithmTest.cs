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
//using Microsoft.VisualStudio.TestTools.UnitTesting;
//using Microsoft.Pex.Framework.Validation;

//namespace QuickGraphNUnit.Algorithms.Search
//{
//    //using NUnit.Framework;

//    using QuickGraphNUnit.Generators;

//    using QuickGraph.Concepts;
//    using QuickGraph.Concepts.Traversals;
//    using QuickGraph.Concepts.Modifications;
//    using QuickGraph.Algorithms;
//    using QuickGraph.Algorithms.Search;
//    using QuickGraph.Collections;
//    using QuickGraph.Representations;
//    using Microsoft.Pex.Framework;
//    using System.Collections;
//    using Microsoft.Pex.Framework.Domains;
	
//    //[TestFixture]
//    [TestClass]
//    [PexClass]
//    public partial class BreadthFirstAlgorithmSearchTest
//    {
//        private VertexVertexDictionary m_Parents;
//        private VertexIntDictionary m_Distances;	
//        private IVertex m_CurrentVertex;
//        private IVertex m_SourceVertex;
//        private int m_CurrentDistance;

//        public VertexVertexDictionary Parents
//        {
//            get
//            {
//                return m_Parents;
//            }
//        }

//        public VertexIntDictionary Distances
//        {
//            get
//            {
//                return m_Distances;
//            }
//        }

//        public IVertex CurrentVertex
//        {
//            get
//            {
//                return m_CurrentVertex;
//            }
//            set
//            {
//                m_CurrentVertex = value;
//            }
//        }

//        public IVertex SourceVertex
//        {
//            get
//            {
//                return m_SourceVertex;
//            }
//        }
		
//        public int CurrentDistance
//        {
//            get
//            {
//                return m_CurrentDistance;
//            }
//            set
//            {
//                m_CurrentDistance = value;
//            }
//        }

//        public void InitializeVertex(Object sender, VertexEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            Assert.AreEqual(algo.Colors[args.Vertex], GraphColor.White);
//        }

//        public void ExamineVertex(Object sender, VertexEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            IVertex u = args.Vertex;
//            CurrentVertex = u;
//            // Ensure that the distances monotonically increase.
//            Assert.IsTrue(
//                   Distances[u] == CurrentDistance
//                || Distances[u] == CurrentDistance + 1
//                );

//            if (Distances[u] == CurrentDistance + 1) // new level
//                ++CurrentDistance;
//        }

//        public void DiscoverVertex(Object sender, VertexEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            IVertex u = args.Vertex;
			
//            Assert.AreEqual(algo.Colors[u],GraphColor.Gray );
//            if (u == SourceVertex) 
//                CurrentVertex = SourceVertex;
//            else 
//            {
//                Assert.AreSame( Parents[u], CurrentVertex );
//                Assert.AreEqual( Distances[u], CurrentDistance + 1 );
//                Assert.AreEqual( Distances[u], Distances[ Parents[u] ] + 1 );
//            }
//        }

//        public void ExamineEdge(Object sender, EdgeEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            Assert.AreSame( args.Edge.Source, CurrentVertex  );
//        }

//        public void TreeEdge(Object sender, EdgeEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            IVertex u = args.Edge.Source;
//            IVertex v = args.Edge.Target;

//            Assert.AreEqual(algo.Colors[v], GraphColor.White);
//            Assert.AreEqual(Distances[u], CurrentDistance );
//            Parents[v] = u;
//            Distances[v] = Distances[u] + 1;
//        }

//        public void NonTreeEdge(Object sender, EdgeEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            IVertex u = args.Edge.Source;
//            IVertex v = args.Edge.Target;

//            Assert.IsFalse(algo.Colors[v]  == GraphColor.White );

//            if (algo.VisitedGraph.IsDirected)
//            {
//                // cross or back edge
//                Assert.IsTrue(Distances[v] <= Distances[u] + 1);
//            }
//            else 
//            {
//                // cross edge (or going backwards on a tree edge)
//                Assert.IsTrue(
//                    Distances[v] == Distances[u] 
//                    ||  Distances[v] == Distances[u] + 1
//                    ||  Distances[v] == Distances[u] - 1
//                    );
//            }
//        }

//        public void GrayTarget(Object sender, EdgeEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            Assert.AreEqual(algo.Colors[args.Edge.Target], GraphColor.Gray);
//        }

//        public void BlackTarget(Object sender, EdgeEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            Assert.AreEqual(algo.Colors[args.Edge.Target], GraphColor.Black);

//            foreach(IEdge e in algo.VisitedGraph.OutEdges(args.Edge.Target))
//                Assert.IsFalse(algo.Colors[e.Target]== GraphColor.White);
//        }

//        public void FinishVertex(Object sender, VertexEventArgs args)
//        {
//            Assert.IsTrue( sender is BreadthFirstSearchAlgorithm );
//            BreadthFirstSearchAlgorithm algo = (BreadthFirstSearchAlgorithm)sender;

//            Assert.AreEqual(algo.Colors[args.Vertex], GraphColor.Black);
//        }

//        //[SetUp]
//        public void Init()
//        {

//            m_Parents = new VertexVertexDictionary();
//            m_Distances = new VertexIntDictionary();
//        }        

//        [PexMethod(MaxBranches = 200000)]
//        [PexBooleanAsZeroOrOne, PexAllowedException(typeof(ArgumentNullException)), PexAllowedException(typeof(Exception))]
//        /// Summary
//        /// Time: 8 min 17 sec        
//        /// Pattern: AAAA, Parameterized stub
//        /// Pex Limitations - Not able to generate any test due to the following issue:
//        /// <boundary> maxbranches - 40000 (maximum number of branches exceeded)
//        /// [execution] Please notice: A branch in the method System.Collections.Hashtable+HashtableEnumerator.MoveNext was executed 5777 times;
//        /// please check that the code is not stuck in an infinite loop.
//        /// [test] (run 1) GraphWithoutSelfEdgesPUT01, pathboundsexceeded (duplicate)
//        /// [execution] Please notice: A branch in the method System.Collections.Hashtable+HashtableEnumerator.MoveNext was executed 4344 times;
//        /// please check that the code is not stuck in an infinite loop.
//        /// [test] (run 2) GraphWithoutSelfEdgesPUT01, pathboundsexceeded (duplicate)        
//      /*  public void GraphWithSelfEdgesPUT(int loopBound)
//        {
//            Random rnd = new Random();

//            for (int i = 0; i < loopBound; ++i)
//                for (int j = 0; j < i * i; ++j)
//                {
//                    AdjacencyGraph g = new AdjacencyGraph(new QuickGraph.Providers.VertexAndEdgeProvider(), true);
//                    RandomGraph.Graph(g, i, j, rnd, true);

//                    BreadthFirstSearchAlgorithm bfs = new BreadthFirstSearchAlgorithm(g);
//                    bfs.InitializeVertex += new VertexHandler(this.InitializeVertex);
//                    bfs.DiscoverVertex += new VertexHandler(this.DiscoverVertex);
//                    bfs.ExamineEdge += new EdgeHandler(this.ExamineEdge);
//                    bfs.ExamineVertex += new VertexHandler(this.ExamineVertex);
//                    bfs.TreeEdge += new EdgeHandler(this.TreeEdge);
//                    bfs.NonTreeEdge += new EdgeHandler(this.NonTreeEdge);
//                    bfs.GrayTarget += new EdgeHandler(this.GrayTarget);
//                    bfs.BlackTarget += new EdgeHandler(this.BlackTarget);
//                    bfs.FinishVertex += new VertexHandler(this.FinishVertex);

//                    Parents.Clear();
//                    Distances.Clear();
//                    m_CurrentDistance = 0;
//                    m_SourceVertex = RandomGraph.Vertex(g, rnd);

//                    foreach (IVertex v in g.Vertices)
//                    {
//                        Distances[v] = int.MaxValue;
//                        Parents[v] = v;
//                    }
//                    Distances[SourceVertex] = 0;
//                    bfs.Compute(SourceVertex);

//                    CheckBfs(g, bfs);
//                }
//        }*/

//        /// <summary>
//        /// @Author:Madhuri
//        /// </summary>
//        /// <param name="g"></param>
//        /// <param name="bfs"></param>
//        public void GraphWithSelfEdgesPUT(int loopBound, int randomVertex,bool allowParallelEdges_b, int numberOfVertices, bool toNull, bool toGraphNull)
//        {
//            AdjacencyGraph g = null;
//            if(!toGraphNull)
//                g = AdjacencyGraphFactory.CreateAcyclicGraph1(allowParallelEdges_b, numberOfVertices, toNull);
//            Random rnd = new Random();
//          //  PexAssume.IsTrue(loopBound < 50);
//            if(g != null)
//                PexAssume.IsTrue(randomVertex > -1 && randomVertex < g.VerticesCount);
//            for (int i = 0; i < loopBound; ++i)
//                for (int j = 0; j < i * i; ++j)
//                {
//                    //AdjacencyGraph g = new AdjacencyGraph(new QuickGraph.Providers.VertexAndEdgeProvider(), true);
//                    RandomGraph.Graph(g, i, j, rnd, true);
//                    Init();
//                    BreadthFirstSearchAlgorithm bfs = new BreadthFirstSearchAlgorithm(g);
//                    bfs.InitializeVertex += new VertexHandler(this.InitializeVertex);
//                    bfs.DiscoverVertex += new VertexHandler(this.DiscoverVertex);
//                    bfs.ExamineEdge += new EdgeHandler(this.ExamineEdge);
//                    bfs.ExamineVertex += new VertexHandler(this.ExamineVertex);
//                    bfs.TreeEdge += new EdgeHandler(this.TreeEdge);
//                    bfs.NonTreeEdge += new EdgeHandler(this.NonTreeEdge);
//                    bfs.GrayTarget += new EdgeHandler(this.GrayTarget);
//                    bfs.BlackTarget += new EdgeHandler(this.BlackTarget);
//                    bfs.FinishVertex += new VertexHandler(this.FinishVertex);

//                    Parents.Clear();
//                    Distances.Clear();
//                    m_CurrentDistance = 0;

//                    int k = 0;
//                    foreach (IVertex v in g.Vertices)
//                    {                        
//                        if (k == randomVertex)
//                        {
//                            m_SourceVertex = v;
//                            break;
//                        }
//                        k++;
//                    }

//                    //IEnumerator vertices = g.Vertices.GetEnumerator();
//                    //for(int k =0; k < g.VerticesCount; k++)
//                    //{
//                    //    IVertex vertex = (IVertex)vertices.Current;
//                    //    if (k == randomVertex)
//                    //    {
//                    //        m_SourceVertex = vertex;
//                    //        break;
//                    //    }
//                    //    vertices.MoveNext();    
                            
//                    //}
//                     //   RandomGraph.Vertex(g, rnd);

//                    foreach (IVertex v in g.Vertices)
//                    {
//                        Distances[v] = int.MaxValue;
//                        Parents[v] = v;
//                    }
//                    Distances[SourceVertex] = 0;
//                    bfs.Compute(SourceVertex);

//                    CheckBfs(g, bfs);
//                }
//        }

//        [PexMethod, PexAllowedException(typeof(ArgumentNullException))]
//        public void GraphWithSelfEdgesPUT1(int v, int e, bool self)
//        {
//            AdjacencyGraph g = null;
//            RandomGraph.Graph(g, v, e, new Random(), self);
//            BreadthFirstSearchAlgorithm bfs = new BreadthFirstSearchAlgorithm(g);
//        }

//        [PexMethod, PexAllowedException(typeof(ArgumentNullException))]
//        public void GraphWithSelfEdgesPUT2()
//        {
//            AdjacencyGraph g = null;
//            BreadthFirstSearchAlgorithm bfs = new BreadthFirstSearchAlgorithm(g);
//        }

//        protected void CheckBfs(IVertexListGraph g, BreadthFirstSearchAlgorithm bfs)
//        {
//            // All white vertices should be unreachable from the source.
//            foreach(IVertex v in g.Vertices)
//            {
//                if (bfs.Colors[v]==GraphColor.White)
//                {
//                    //!IsReachable(start,u,g);
//                }
//            }

//            // The shortest path to a child should be one longer than
//            // shortest path to the parent.
//            foreach(IVertex v in g.Vertices)
//            {
//                if (Parents[v] != v) // *ui not the root of the bfs tree
//                    Assert.AreEqual(Distances[v], Distances[Parents[v]] + 1);
//            }
//        }
//    }
//}
