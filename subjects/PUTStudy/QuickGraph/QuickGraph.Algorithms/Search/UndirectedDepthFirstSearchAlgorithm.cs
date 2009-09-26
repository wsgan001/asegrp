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
	using QuickGraph.Collections;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Algorithms;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Concepts.Visitors;

	/// <summary>
	/// Summary description for UndirectedDepthFirstSearch.
	/// </summary>
	/// <remarks>
	/// <para>This algorithm is directly inspired from the
	/// BoostGraphLibrary implementation.
	/// </para> 
	/// </remarks>
	public class UndirectedDepthFirstSearchAlgorithm :
		IAlgorithm,
		IVertexColorizerAlgorithm,
		IEdgeColorizerAlgorithm,
		IPredecessorRecorderAlgorithm, 
		ITimeStamperAlgorithm
	{
		private IVertexAndEdgeListGraph m_VisitedGraph;
		private VertexColorDictionary m_Colors;
		private EdgeColorDictionary m_EdgeColors;

		/// <summary>
		/// Create a undirected dfs algorithm
		/// </summary>
		/// <param name="g">Graph to search on.</param>
		public UndirectedDepthFirstSearchAlgorithm(IVertexAndEdgeListGraph g)
		{
			if(g == null)
				throw new ArgumentNullException("g");
			m_VisitedGraph = g;
			m_EdgeColors=new EdgeColorDictionary();
			m_Colors = new VertexColorDictionary();
		}

		/// <summary>
		/// Visited graph
		/// </summary>
		public IVertexAndEdgeListGraph VisitedGraph
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

		/// <summary>
		/// Edge color map
		/// </summary>
		public EdgeColorDictionary EdgeColors
		{
			get
			{
				return m_EdgeColors;
			}	
		}

		IDictionary IEdgeColorizerAlgorithm.EdgeColors
		{
			get
			{
				return this.EdgeColors;
			}
		}

		#region Events
		/// <summary>
		/// Invoked on every vertex of the graph before the start of the graph 
		/// search.
		/// </summary>
		public event VertexHandler InitializeVertex;

		/// <summary>
		/// Invoked on the source vertex once before the start of the search. 
		/// </summary>
		public event VertexHandler StartVertex;

		/// <summary>
		/// Invoked when a vertex is encountered for the first time. 
		/// </summary>
		public event VertexHandler DiscoverVertex;

		/// <summary>
		/// Invoked on every out-edge of each vertex after it is discovered. 
		/// </summary>
		public event EdgeHandler ExamineEdge;

		/// <summary>
		/// Invoked on each edge as it becomes a member of the edges that form 
		/// the search tree. If you wish to record predecessors, do so at this 
		/// event point. 
		/// </summary>
		public event EdgeHandler TreeEdge;

		/// <summary>
		/// Invoked on the back edges in the graph. 
		/// </summary>
		public event EdgeHandler BackEdge;

		/// <summary>
		/// Invoked on a vertex after all of its out edges have been added to 
		/// the search tree and all of the adjacent vertices have been 
		/// discovered (but before their out-edges have been examined). 
		/// </summary>
		public event VertexHandler FinishVertex;
		#endregion

		/// <summary>
		/// Computes the dfs
		/// </summary>
		public void Compute()
		{
			Compute(null);
		}

		/// <summary>
		/// Computes the dfs starting at s
		/// </summary>
		/// <param name="s">start vertex</param>
		public void Compute(IVertex s)
		{
			// init vertices
			foreach(IVertex u in VisitedGraph.Vertices)
			{
				Colors[u]=GraphColor.White;
				if (InitializeVertex != null)
					InitializeVertex(this, new VertexEventArgs(u));
			}
			//init edges
			foreach(IEdge e in VisitedGraph.Edges)
			{
				EdgeColors[e]=GraphColor.White;
			}

			// use start vertex
			if (s != null)
			{
				if (StartVertex != null)
					StartVertex(this,new VertexEventArgs(s));
				Visit(s);
			}
			// visit vertices
			foreach(IVertex v in VisitedGraph.Vertices)
			{
				if (Colors[v] == GraphColor.White)
				{
					if (StartVertex != null)
						StartVertex(this,new VertexEventArgs(v));
					Visit(v);
				}
			}
		}

		/// <summary>
		/// Visits vertex s
		/// </summary>
		/// <param name="u">vertex to visit</param>
		public void Visit(IVertex u)
		{
			VertexEventArgs uArgs = new VertexEventArgs(u);

			Colors[u]=GraphColor.Gray;
			if (DiscoverVertex != null)
				DiscoverVertex(this,uArgs);

			foreach(IEdge e in VisitedGraph.OutEdges(u))
			{
				EdgeEventArgs eArgs = new EdgeEventArgs(e);
				if (ExamineEdge != null)
					ExamineEdge(this, eArgs);

				GraphColor vc = Colors[e.Target];
				GraphColor ec = EdgeColors[e];

				EdgeColors[e]=GraphColor.Black;
				if (vc == GraphColor.White) // tree edge
				{
					if (TreeEdge != null)
						TreeEdge(this,eArgs);
					Visit(e.Target);
				}
				else if(vc == GraphColor.Gray && ec == GraphColor.White)
				{
					if (BackEdge != null)
						BackEdge(this,eArgs);
				}

			}

			Colors[u]=GraphColor.Black;
			if (FinishVertex != null)
				FinishVertex(this,uArgs);
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

		/// <summary>
		/// 
		/// </summary>
		/// <param name="vis"></param>
		public void RegisterTimeStamperHandlers(ITimeStamperVisitor vis)
		{
			if (vis == null)
				throw new ArgumentNullException("visitor");

			DiscoverVertex += new VertexHandler(vis.DiscoverVertex);
			FinishVertex += new VertexHandler(vis.FinishVertex);
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
	}
}
