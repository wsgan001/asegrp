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



namespace QuickGraph.Algorithms.Search
{
	using System;
	using System.Collections;
	using System.Collections.Specialized;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Algorithms;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Concepts.Visitors;

	using QuickGraph.Collections;

	/// <summary>
	/// Performs a breadth-first traversal 
	/// of a directed or undirected graph. 
	/// </summary>
	/// <remarks>
	/// <para>
	/// A breadth-first-search (BFS) traversal visits vertices that are closer to the 
	/// source before visiting vertices that are further away. 
	/// In this context ``distance'' is defined as the number of edges in the 
	/// shortest path from the source vertex. 
	/// </para>
	/// <para>
	/// The BFS can be used to compute the shortest 
	/// path from the source to all reachable vertices and the resulting 
	/// shortest-path distances.
	/// </para>
	/// <para>
	/// BFS uses two data structures to to implement the traversal: 
	/// a color marker for each vertex and a queue. 
	/// White vertices are undiscovered while gray vertices are discovered 
	/// but have undiscovered adjacent vertices. Black vertices are discovered 
	/// and are adjacent to only other black or gray vertices. 
	/// </para>
	/// <para>
	/// The algorithm proceeds by removing a vertex u from the queue and 
	/// examining each out-edge (u,v). If an adjacent vertex v is not already 
	/// discovered, it is colored gray and placed in the queue. After all of 
	/// the out-edges are examined, vertex u is colored black and the process 
	/// is repeated. Pseudo-code for the BFS algorithm is a listed below. 
	/// </para>
	/// <code>
	/// Graph G;
	/// BFS(Vertex s)
	/// {
	///		// initialize vertices
	///     foreach(Vertex u in G.Vertices)
	///     {
	///			Colors[u] = White; 
	///			InitializeVertex(u);						// event
	///		}
	///		
	///		Visit(s);
	///	}
	///	
	///	Visit(Vertex s)
	///	{
	///		Colors[s]=GraphColor.Gray;
	///		DiscoverVertex(s);								//event
	///		
	///		m_Q.Push(s);
	///		while (m_Q.Count != 0)
	///		{
	///			Vertex u = m_Q.Peek(); 
	///			m_Q.Pop();
	///			ExamineVertex(u);							// event
	/// 
	///			foreach(Edge e in u.OutEdges)
	///			{
	///				ExamineEdge(e);							// event
	///
	///				GraphColor vColor = Colors[e.Target];
	///				if (vColor == GraphColor.White)
	///				{
	///					TreeEdge(e);						// event
	///					DiscoverVertex(v);					// event
	///					Colors[v]=GraphColor.Gray;
	///					m_Q.Push(v);
	///				}
	///				else
	///				{
	///					NonTreeEdge(e);
	///					if (vColor == GraphColor.Gray)
	///					{
	///						GrayTarget(e);					// event
	///					}
	///					else
	///					{
	///						BlackTarget(e);					//event
	///					}
	///				}
	///			}
	///			Colors[u]=GraphColor.Black;
	///			FinishVertex(this, uArgs);
	///		}
	///	}
	///</code>
	/// <para>This algorithm is directly inspired from the
	/// BoostGraphLibrary implementation.
	/// </para> 
	/// </remarks>
	public class BreadthFirstSearchAlgorithm :
		IAlgorithm,
		IPredecessorRecorderAlgorithm,
		IDistanceRecorderAlgorithm,
		IVertexColorizerAlgorithm,
		ITreeEdgeBuilderAlgorithm
	{
		private IVertexListGraph m_VisitedGraph;
		private VertexColorDictionary m_Colors;
		private VertexBuffer m_Q;

		/// <summary>
		/// BreadthFirstSearch searcher constructor
		/// </summary>
		/// <param name="g">Graph to visit</param>
		public BreadthFirstSearchAlgorithm(IVertexListGraph g)
		{
			if (g == null)
				throw new ArgumentNullException("g");
			m_VisitedGraph = g;
			m_Colors = new VertexColorDictionary();
			m_Q = new VertexBuffer();
		}

		/// <summary>
		/// BreadthFirstSearch searcher contructor
		/// </summary>
		/// <param name="g">Graph to visit</param>
		/// <param name="Q">Vertex buffer</param>
		/// <param name="colors">Vertex color map</param>
		public BreadthFirstSearchAlgorithm(
			IVertexListGraph g, 
			VertexBuffer Q,
			VertexColorDictionary colors
			)
		{
			if (g == null)
				throw new ArgumentNullException("g");
			if (Q == null)
				throw new ArgumentNullException("Stack Q is null");
			if (colors == null)
				throw new ArgumentNullException("Colors");

			m_VisitedGraph = g;
			m_Colors = colors;
			m_Q = Q;
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
		/// Invoked on every vertex before the start of the search
		/// </summary>
		public event VertexHandler InitializeVertex;

		/// <summary>
		/// Invoked in each vertex as it is removed from the queue
		/// </summary>
		public event VertexHandler ExamineVertex;

		/// <summary>
		/// Invoked the first time the algorithm encounters vertex u. 
		/// All vertices closer to the source vertex have been discovered, 
		/// and vertices further from the source have not yet been discovered.
		/// </summary>
		public event VertexHandler DiscoverVertex;

		/// <summary>
		/// Invoked on every out-edge of each vertex immediately after the vertex is removed from the queue.
		/// </summary>
		public event EdgeHandler ExamineEdge;

		/// <summary>
		/// Invoked (in addition to ExamineEdge()) if the edge is a tree edge. 
		/// The target vertex of edge e is discovered at this time.
		/// </summary>
		public event EdgeHandler TreeEdge;

		/// <summary>
		/// Invoked (in addition to examine_edge()) if the edge is not a tree 
		/// edge.
		/// </summary>
		public event EdgeHandler NonTreeEdge;

		/// <summary>
		/// Invoked (in addition to non_tree_edge()) if the target vertex is 
		/// colored gray at the time of examination. The color gray indicates 
		/// that the vertex is currently in the queue.
		/// </summary>
		public event EdgeHandler GrayTarget;

		/// <summary>
		/// Invoked (in addition to NonTreeEdge()) if the target vertex is 
		/// colored black at the time of examination. The color black indicates 
		/// that the vertex is no longer in the queue.
		/// </summary>
		public event EdgeHandler BlackTarget;

		/// <summary>
		/// Invoked after all of the out edges of u have been examined 
		/// and all of the adjacent vertices have been discovered. 
		/// </summary>
		public event VertexHandler FinishVertex;

		#endregion

		/// <summary>
		/// Computes the bfs starting at s
		/// </summary>
		/// <param name="s">starting vertex</param>
		/// <exception cref="ArgumentNullException">s is null</exception>
		/// <remarks>
		/// This method initializes the color map before appliying the visit.
		/// </remarks>
		public void Compute(IVertex s)
		{
			if (s == null)
				throw new ArgumentNullException("Start vertex is null");

			// initialize vertex u
			foreach(IVertex v in VisitedGraph.Vertices)
			{
				Colors[v]=GraphColor.White;
				if (InitializeVertex != null)
					InitializeVertex(this, new VertexEventArgs(v));
			}

			Visit(s);
		}

		/// <summary>
		/// Computes the bfs starting at s without initalization.
		/// </summary>
		/// <param name="s">starting vertex</param>
		/// <exception cref="ArgumentNullException">s is null</exception>
		public void Visit(IVertex s)
		{
			if (s == null)
				throw new ArgumentNullException("Start vertex is null");

			Colors[s]=GraphColor.Gray;

			if (DiscoverVertex != null)
				DiscoverVertex(this,new VertexEventArgs(s));

			m_Q.Push(s);
			while (m_Q.Count != 0)
			{
				IVertex u = (IVertex)m_Q.Peek(); 
				m_Q.Pop();

				VertexEventArgs uArgs = new VertexEventArgs(u);
				if (ExamineVertex != null)
					ExamineVertex(this,uArgs);

				foreach(IEdge e in VisitedGraph.OutEdges(u))
				{
					EdgeEventArgs eArgs=new EdgeEventArgs(e);
					IVertex v = e.Target;
					if (ExamineEdge != null)
						ExamineEdge(this, eArgs);

					GraphColor vColor = Colors[v];
					if (vColor == GraphColor.White)
					{
						if (TreeEdge != null)
							TreeEdge(this,eArgs);
						Colors[v]=GraphColor.Gray;
						if (DiscoverVertex != null)
							DiscoverVertex(this, new VertexEventArgs(v));
						m_Q.Push(v);
					}
					else
					{
						if (NonTreeEdge != null)
							NonTreeEdge(this, eArgs);
						if (vColor == GraphColor.Gray)
						{
							if (GrayTarget != null)
								GrayTarget(this, eArgs);
						}
						else
						{
							if (BlackTarget != null)
								BlackTarget(this, eArgs);
						}

					}
				}
				Colors[u]=GraphColor.Black;

				if (FinishVertex != null)
					FinishVertex(this, uArgs);
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="vis"></param>
		public void RegisterVertexColorizerHandlers(IVertexColorizerVisitor vis)
		{
			if (vis == null)
				throw new ArgumentNullException("visitor");

			InitializeVertex += new VertexHandler(vis.InitializeVertex);
			DiscoverVertex += new VertexHandler(vis.DiscoverVertex);
			FinishVertex += new VertexHandler(vis.FinishVertex);
		}


		
		/// <summary>
		/// 
		/// </summary>
		/// <param name="vis"></param>
		public void RegisterDistanceRecorderHandlers(IDistanceRecorderVisitor vis)		
		{
			if (vis == null)
				throw new ArgumentNullException("visitor");

			InitializeVertex += new VertexHandler(vis.InitializeVertex);
			DiscoverVertex += new VertexHandler(vis.DiscoverVertex);
			TreeEdge += new EdgeHandler(vis.TreeEdge);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="vis"></param>
		public void RegisterTreeEdgeBuilderHandlers(ITreeEdgeBuilderVisitor vis)
		{
			if (vis == null)
				throw new ArgumentNullException("visitor");

			TreeEdge += new EdgeHandler(vis.TreeEdge);
		}
		/// <summary>
		/// Registers the predecessors handler
		/// </summary>
		/// <param name="vis"></param>
		public void RegisterPredecessorRecorderHandlers(IPredecessorRecorderVisitor vis)
		{
			if (vis == null)
				throw new ArgumentNullException("visitor");
			InitializeVertex += new VertexHandler(vis.InitializeVertex);
			TreeEdge += new EdgeHandler(vis.TreeEdge);
		}
	
	}
}
