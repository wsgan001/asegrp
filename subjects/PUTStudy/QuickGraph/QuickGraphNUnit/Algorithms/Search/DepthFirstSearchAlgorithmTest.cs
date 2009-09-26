/// QuickGraph Library 
/// 
/// Copyright (c) 2003 Jonathan de Halleux
///
/// This software is provided 'as-is', without any express or implied warranty. 
/// 
/// In no event will the authors be held liable for any damages arising from 
/// the use of this software.
/// Permission is granted to anyone to use this software for any purpose, 
/// including commercial applications, and to alter it and redistribute it 
/// freely, subject to the following restrictions:
///
///		1. The origin of this software must not be misrepresented; 
///		you must not claim that you wrote the original software. 
///		If you use this software in a product, an acknowledgment in the product 
///		documentation would be appreciated but is not required.
///
///		2. Altered source versions must be plainly marked as such, and must 
///		not be misrepresented as being the original software.
///
///		3. This notice may not be removed or altered from any source 
///		distribution.
///		
///		QuickGraph Library HomePage: http://www.dotnetwiki.org
///		Author: Jonathan de Halleux


using System;

namespace QuickGraphNUnit.Algorithms.Search
{
	using NUnit.Framework;

	using QuickGraphNUnit.Generators;

	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Concepts.Modifications;
	using QuickGraph.Algorithms;
	using QuickGraph.Algorithms.Search;
	using QuickGraph.Collections;
	using QuickGraph.Representations;
	
	[TestFixture]
	public class DepthFirstAlgorithmSearchTest
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

		[SetUp]
		public void Init()
		{

			m_Parents = new VertexVertexDictionary();
			m_DiscoverTimes = new VertexIntDictionary();
			m_FinishTimes = new VertexIntDictionary();
			m_Time = 0;
		}

		[Test]
		public void GraphWithSelfEdges()
		{
			AdjacencyGraph g = new AdjacencyGraph(
				new QuickGraph.Providers.VertexAndEdgeProvider(),
				true
				);
			RandomGraph.Graph(g,20,100,new Random(),true);

			DepthFirstSearchAlgorithm dfs = new DepthFirstSearchAlgorithm(g);
			dfs.StartVertex += new VertexHandler( this.StartVertex);
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

			foreach(IVertex v in g.Vertices)
				Parents[v] = v;

			// compute
			dfs.Compute();

			CheckDfs(g,dfs);
		}

		[Test]
		public void GraphWithoutSelfEdges()
		{
			AdjacencyGraph g = new AdjacencyGraph(
				new QuickGraph.Providers.VertexAndEdgeProvider(),
				true);
			RandomGraph.Graph(g,20,100,new Random(),false);

			DepthFirstSearchAlgorithm dfs = new DepthFirstSearchAlgorithm(g);
			dfs.StartVertex += new VertexHandler( this.StartVertex);
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

			foreach(IVertex v in g.Vertices)
				Parents[v] = v;

			// compute
			dfs.Compute();

			CheckDfs(g,dfs);
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
