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



namespace QuickGraph.Algorithms.ShortestPath
{
	using System;
	using System.Collections;
	using QuickGraph.Collections;
	using QuickGraph.Algorithms.Search;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Concepts.Visitors;
	using QuickGraph.Concepts.Algorithms;

	/// <summary>
	/// Dijkstra shortest path algorithm.
	/// </summary>
	/// <remarks>
	/// This algorithm solves the single-source shortest-paths problem 
	/// on a weighted, directed or undirected graph for the case where all 
	/// edge weights are nonnegative. It is strongly inspired from the
	/// Boost Graph Library implementation.
	/// 
	/// Use the Bellman-Ford algorithm for the case when some edge weights are 
	/// negative. 
	/// Use breadth-first search instead of Dijkstra's algorithm when all edge 
	/// weights are equal to one. 
	/// </remarks>
	public class DijkstraShortestPathAlgorithm : 
		IVertexColorizerAlgorithm
	{
		private IVertexListGraph m_VisitedGraph;
		private PriorithizedVertexBuffer m_VertexQueue;
		private VertexColorDictionary m_Colors;
		private VertexDoubleDictionary m_Distances;
		private EdgeDoubleDictionary m_Weights;
		private VertexVertexDictionary m_Predecessors;

		/// <summary>
		/// Builds a new Dijsktra searcher.
		/// </summary>
		/// <param name="g">The graph</param>
		/// <param name="weights">Edge weights</param>
		/// <exception cref="ArgumentNullException">Any argument is null</exception>
		/// <remarks>This algorithm uses the <seealso cref="BreadthFirstSearchAlgorithm"/>.</remarks>
		public DijkstraShortestPathAlgorithm(
			IVertexListGraph g, 
			EdgeDoubleDictionary weights
			)
		{
			if (g == null)
				throw new ArgumentNullException("g");
			if (weights == null)
				throw new ArgumentNullException("Weights");

			m_VisitedGraph = g;
			m_Colors = new VertexColorDictionary();
			m_Distances = new VertexDoubleDictionary();
			m_Predecessors = new VertexVertexDictionary();
			m_Weights = weights;
			m_VertexQueue = null;
		}


		/// <summary>
		/// Visited graph
		/// </summary>
		public IVertexListGraph VisitedGraph
		{
			get
			{
				return m_VisitedGraph;
			}
		}

		Object IAlgorithm.VisitedGraph
		{
			get
			{
				return this.VisitedGraph;
			}
		}

		/// <summary>
		/// Vertex color map
		/// </summary>
		public VertexColorDictionary Colors
		{
			get
			{
				return m_Colors;
			}
		}

		IDictionary IVertexColorizerAlgorithm.Colors
		{
			get
			{
				return this.Colors;
			}
		}

		#region Events
		/// <summary>
		/// Invoked on each vertex in the graph before the start of the 
		/// algorithm.
		/// </summary>
		public event VertexHandler InitializeVertex;

		/// <summary>
		/// Invoked on vertex v when the edge (u,v) is examined and v is WHITE. 
		/// Since a vertex is colored GRAY when it is discovered, each 
		/// reachable vertex is discovered exactly once. This is also when the 
		/// vertex is inserted into the priority queue. 
		/// </summary>
		public event VertexHandler DiscoverVertex;

		/// <summary>
		/// Invoked on a vertex as it is removed from the priority queue and 
		/// added to set S. At this point we know that (p[u],u) is a 
		/// shortest-paths tree edge so 
		/// d[u] = delta(s,u) = d[p[u]] + w(p[u],u). 
		/// Also, the distances of the examined vertices is monotonically 
		/// increasing d[u1] &lt;= d[u2] &lt;= d[un]. 
		/// </summary>
		public event VertexHandler ExamineVertex;

		/// <summary>
		/// Invoked on each out-edge of a vertex immediately after it has 
		/// been added to set S. 
		/// </summary>
		public event EdgeHandler ExamineEdge;

		/// <summary>
		/// invoked on edge (u,v) if d[u] + w(u,v) &lt; d[v]. The edge (u,v) 
		/// that participated in the last relaxation for vertex v is an edge 
		/// in the shortest paths tree. 
		/// </summary>
		public event EdgeHandler EdgeRelaxed;

		/// <summary>
		/// Invoked if the edge is not relaxed. <seealso cref="EdgeRelaxed"/>.
		/// </summary>
		public event EdgeHandler EdgeNotRelaxed;

		/// <summary>
		/// Invoked on a vertex after all of its out edges have been examined.
		/// </summary>
		public event VertexHandler FinishVertex;
		#endregion

		/// <summary>
		/// Checks for edge relation.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void TreeEdge(Object sender, EdgeEventArgs args)
		{
			bool decreased = Relax(args.Edge);
			if (decreased)
			{
				if (EdgeRelaxed != null)
					EdgeRelaxed(this, new EdgeEventArgs(args.Edge));
			}
			else
			{
				if (EdgeNotRelaxed != null)
					EdgeNotRelaxed(this, new EdgeEventArgs(args.Edge));
			}
		}

		/// <summary>
		/// Checks for edge relation.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void GrayTarget(Object sender, EdgeEventArgs args)
		{
			bool decreased = Relax(args.Edge);
			if (decreased)
			{
				m_VertexQueue.Update(args.Edge.Target);
				if (EdgeRelaxed != null)
					EdgeRelaxed(this, new EdgeEventArgs(args.Edge));
			}
			else
			{
				if (EdgeNotRelaxed != null)
					EdgeNotRelaxed(this, new EdgeEventArgs(args.Edge));
			}
		}

		/// <summary>
		/// Constructed distance map
		/// </summary>
		public VertexDoubleDictionary Distances
		{
			get
			{
				return m_Distances;
			}
		}

		/// <summary>
		/// Constructed predecessor map
		/// </summary>
		public VertexVertexDictionary Predecessors
		{
			get
			{
				return m_Predecessors;
			}
		}

		/// <summary>
		/// Vertex priorithized queue. Used internally.
		/// </summary>
		protected VertexBuffer VertexQueue
		{
			get
			{
				return m_VertexQueue;
			}
		}

		/// <summary>
		/// Computes all the shortest path from s to the oter vertices
		/// </summary>
		/// <param name="s">Start vertex</param>
		/// <exception cref="ArgumentNullException">s is null</exception>
		public void Compute(IVertex s)
		{
			if (s==null)
				throw new ArgumentNullException("Start vertex is null");

			// init color, distance
			foreach(IVertex u in VisitedGraph.Vertices)
			{
				Colors[u]=GraphColor.White;
				Predecessors[u]=u;
				Distances[u]=double.PositiveInfinity;
			}

			Colors[s]=GraphColor.Gray;
			Distances[s]=0;

			ComputeNoInit(s);
		}

		internal void ComputeNoInit(IVertex s)
		{
			m_VertexQueue = new PriorithizedVertexBuffer(m_Distances);
			BreadthFirstSearchAlgorithm bfs = new BreadthFirstSearchAlgorithm(
				VisitedGraph,
				m_VertexQueue,
				Colors
				);

			bfs.InitializeVertex += this.InitializeVertex;
			bfs.DiscoverVertex += this.DiscoverVertex;
			bfs.ExamineEdge += this.ExamineEdge;
			bfs.ExamineVertex += this.ExamineVertex;
			bfs.FinishVertex += this.FinishVertex;

			bfs.TreeEdge += new EdgeHandler(this.TreeEdge);
			bfs.GrayTarget += new EdgeHandler(this.GrayTarget);

			bfs.Visit(s);
		}

		internal bool Compare(double a, double b)
		{
			return a < b;
		}

		internal double Combine(double d, double w)
		{
			return d+w;
		}

		internal bool Relax(IEdge e)
		{
			double du = m_Distances[e.Source];
			double dv = m_Distances[e.Target];
			double we = m_Weights[e];

			if (Compare(Combine(du,we),dv))
			{
				m_Distances[e.Target]=Combine(du,we);
				m_Predecessors[e.Target]=e.Source;
				return true;
			}
			else
				return false;
		}

		
		/// <summary>
		/// 
		/// </summary>
		/// <param name="vis"></param>
		public void RegisterVertexColorizerHandlers(IVertexColorizerVisitor vis)
		{
			InitializeVertex += new VertexHandler(vis.InitializeVertex);
			DiscoverVertex += new VertexHandler(vis.DiscoverVertex);
			FinishVertex += new VertexHandler(vis.FinishVertex);
		}
	}
}
