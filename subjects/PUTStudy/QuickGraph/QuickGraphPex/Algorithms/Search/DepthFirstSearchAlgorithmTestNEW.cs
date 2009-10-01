using System;
using Microsoft.Pex.Framework.Validation;

namespace QuickGraphNUnit.Algorithms.Search
{
	//using NUnit.Framework;

	using QuickGraphNUnit.Generators;

	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Concepts.Modifications;
	using QuickGraph.Algorithms;
	using QuickGraph.Algorithms.Search;
	using QuickGraph.Collections;
	using QuickGraph.Representations;
    using Microsoft.Pex.Framework;
    using QuickGraph.Providers;
    using Microsoft.Pex.Framework.Validation;
   // using Microsoft.VisualStudio.TestTools.UnitTesting;
using NUnit.Framework;
	
	[TestFixture]
    [PexClass]
    public partial class DepthFirstAlgorithmSearchTestNEW
	{
		private VertexVertexDictionary m_Parents;
		private VertexIntDictionary m_DiscoverTimes;
		private VertexIntDictionary m_FinishTimes;
		private int m_Time;

		public VertexVertexDictionary Parents
		{
			get
			{
				return m_Parents;
			}
		}

		public VertexIntDictionary DiscoverTimes
		{
			get
			{
				return m_DiscoverTimes;
			}
		}

		public VertexIntDictionary FinishTimes
		{
			get
			{
				return m_FinishTimes;
			}
		}

		public void StartVertex(Object sender, VertexEventArgs args)
		{
			Assert.IsTrue( sender is DepthFirstSearchAlgorithm );
			DepthFirstSearchAlgorithm algo = (DepthFirstSearchAlgorithm)sender;

			Assert.AreEqual(algo.Colors[args.Vertex], GraphColor.White);
		}

		public void DiscoverVertex(Object sender, VertexEventArgs args)
		{
			Assert.IsTrue( sender is DepthFirstSearchAlgorithm );
			DepthFirstSearchAlgorithm algo = (DepthFirstSearchAlgorithm)sender;

			Assert.AreEqual(algo.Colors[args.Vertex], GraphColor.Gray);
			Assert.AreEqual(algo.Colors[Parents[args.Vertex]], GraphColor.Gray);

			DiscoverTimes[args.Vertex]=m_Time++;
		}

		public void ExamineEdge(Object sender, EdgeEventArgs args)
		{
			Assert.IsTrue( sender is DepthFirstSearchAlgorithm );
			DepthFirstSearchAlgorithm algo = (DepthFirstSearchAlgorithm)sender;

			Assert.AreEqual(algo.Colors[args.Edge.Source], GraphColor.Gray);
		}

		public void TreeEdge(Object sender, EdgeEventArgs args)
		{
			Assert.IsTrue( sender is DepthFirstSearchAlgorithm );
			DepthFirstSearchAlgorithm algo = (DepthFirstSearchAlgorithm)sender;

			Assert.AreEqual(algo.Colors[args.Edge.Target], GraphColor.White);
			Parents[args.Edge.Target] = args.Edge.Source;
		}

		public void BackEdge(Object sender, EdgeEventArgs args)
		{
			Assert.IsTrue( sender is DepthFirstSearchAlgorithm );
			DepthFirstSearchAlgorithm algo = (DepthFirstSearchAlgorithm)sender;

			Assert.AreEqual(algo.Colors[args.Edge.Target], GraphColor.Gray);
		}

		public void FowardOrCrossEdge(Object sender, EdgeEventArgs args)
		{
			Assert.IsTrue( sender is DepthFirstSearchAlgorithm );
			DepthFirstSearchAlgorithm algo = (DepthFirstSearchAlgorithm)sender;

			Assert.AreEqual(algo.Colors[args.Edge.Target], GraphColor.Black);
		}

		public void FinishVertex(Object sender, VertexEventArgs args)
		{
			Assert.IsTrue( sender is DepthFirstSearchAlgorithm );
			DepthFirstSearchAlgorithm algo = (DepthFirstSearchAlgorithm)sender;

			Assert.AreEqual(algo.Colors[args.Vertex], GraphColor.Black);
			FinishTimes[args.Vertex]=m_Time++;
		}

		public bool IsDescendant(IVertex u, IVertex v)
		{
			IVertex t=null;
			IVertex p=u;
			do
			{
				t=p;
				p=Parents[t];
				if (p==v)
					return true;
			}
			while (t!=p);

			return false;
		}

		//[SetUp]
		public void Init()
		{

			m_Parents = new VertexVertexDictionary();
			m_DiscoverTimes = new VertexIntDictionary();
			m_FinishTimes = new VertexIntDictionary();
			m_Time = 0;
		}

        [PexMethod(MaxBranches = 160000), PexAllowedException(typeof(ArgumentNullException)), PexAllowedException(typeof(Exception))]
        public void GraphWithSelfEdgesPUT(AdjacencyGraph g, int loopBound, bool self)
        {
            Random rnd; //new Random();
            var choose1 = PexChoose.FromCall(this);
            rnd = choose1.ChooseValue<Random>("Random object");
            Init();
            for (int i = 0; i < loopBound; ++i)
            {
                for (int j = 0; j < i * i; ++j)
                {
                    RandomGraph.Graph(g, i, j, rnd, true);
                    Init();
                    DepthFirstSearchAlgorithm dfs = new DepthFirstSearchAlgorithm(g);
                    dfs.StartVertex += new VertexHandler(this.StartVertex);
                    dfs.DiscoverVertex += new VertexHandler(this.DiscoverVertex);
                    dfs.ExamineEdge += new EdgeHandler(this.ExamineEdge);
                    dfs.TreeEdge += new EdgeHandler(this.TreeEdge);
                    dfs.BackEdge += new EdgeHandler(this.BackEdge);
                    dfs.ForwardOrCrossEdge += new EdgeHandler(this.FowardOrCrossEdge);
                    dfs.FinishVertex += new VertexHandler(this.FinishVertex);

                    Parents.Clear();
                    DiscoverTimes.Clear();
                    FinishTimes.Clear();
                    m_Time = 0;

                    foreach (IVertex v in g.Vertices)
                        Parents[v] = v;

                    var choose = PexChoose.FromCall(this);
                    if (choose.ChooseValue<bool>("to add a self ede"))
                    {
                        IVertex selfEdge = RandomGraph.Vertex(g, rnd);
                        g.AddEdge(selfEdge, selfEdge);
                    }
                    // compute
                    dfs.Compute();

                    CheckDfs(g, dfs);
                }
            }
        }

        [PexMethod, PexAllowedException(typeof(ArgumentNullException))]
        public void GraphWithSelfEdgesPUT1(int v, int e, bool self)
        {
            AdjacencyGraph g = null;
            RandomGraph.Graph(g, v, e, new Random(), self);
            DepthFirstSearchAlgorithm bfs = new DepthFirstSearchAlgorithm(g);
        }

        [PexMethod, PexAllowedException(typeof(ArgumentNullException))]
        public void GraphWithSelfEdgesPUT2()
        {
            AdjacencyGraph g = null;
            DepthFirstSearchAlgorithm bfs = new DepthFirstSearchAlgorithm(g);
        }

		protected void CheckDfs(IVertexListGraph g, DepthFirstSearchAlgorithm dfs)
		{
			// check
			// all vertices should be black
			foreach(IVertex v in g.Vertices)
			{
				Assert.IsTrue( dfs.Colors.Contains(v));
				Assert.AreEqual(dfs.Colors[v],GraphColor.Black);
			}

			// check parenthesis structure of discover/finish times
			// See CLR p.480
			foreach(IVertex u in g.Vertices)
			{
				foreach(IVertex v in g.Vertices) 
				{
					if (u != v) 
					{
						Assert.IsTrue( 
							FinishTimes[u] < DiscoverTimes[v]
							|| FinishTimes[v] < DiscoverTimes[u]
							|| (
							DiscoverTimes[v] < DiscoverTimes[u]
							&& FinishTimes[u] < FinishTimes[v]
							&& IsDescendant(u, v)
							)
							|| (
							DiscoverTimes[u] < DiscoverTimes[v]
							&& FinishTimes[v] < FinishTimes[u]
							&& IsDescendant(v, u)
							)
							);
					}
				}
			}
		}
	}
}
