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


namespace QuickGraph.Representations
{
	using System;
	using System.Collections;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Concepts.Modifications;
	using QuickGraph.Concepts.Predicates;
	using QuickGraph.Concepts.Providers;
	using QuickGraph.Collections;
	using QuickGraph.Exceptions;
	using QuickGraph.Predicates;

	/// <summary>
	/// A mutable incidence graph implemetation
	/// </summary>
	/// <remarks>
	/// <seealso cref="IVertexMutableGraph"/>
	/// <seealso cref="IMutableIncidenceGraph"/>
	/// </remarks>
	public class AdjacencyGraph :
		IVertexAndEdgeListGraph,
		IMutableEdgeListGraph,
		IEdgeMutableGraph,
		IMutableIncidenceGraph
	{
		private IVertexAndEdgeProvider m_Provider;
		private VertexEdgesDictionary m_VertexOutEdges;
		private EdgeCollection m_Edges;
		private bool m_AllowParallelEdges;

		/// <summary>
		/// Builds a new empty directed graph
		/// </summary>
		public AdjacencyGraph(
			IVertexAndEdgeProvider provider,
			bool allowParallelEdges
			)
		{
			if (provider == null)
				throw new ArgumentNullException("provider");

			m_Provider = provider;
			m_AllowParallelEdges = allowParallelEdges;
			m_VertexOutEdges = new VertexEdgesDictionary();
			m_Edges = new EdgeCollection();
		}

		/// <summary>
		/// 
		/// </summary>
		/// <remarks>
		/// <seealso cref="IGraph"/>
		/// </remarks>
		public bool IsDirected
		{
			get
			{
				return true;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <remarks>
		/// <seealso cref="IGraph"/>
		/// </remarks>
		public bool AllowParallelEdges
		{
			get
			{
				return IsDirected && m_AllowParallelEdges;
			}
		}

		/// <summary>
		/// Vertex Out edges dictionary
		/// </summary>
		protected VertexEdgesDictionary VertexOutEdges
		{
			get
			{
				return m_VertexOutEdges;
			}
		}

		/// <summary>
		/// Vertex and edge provider
		/// </summary>
		public IVertexAndEdgeProvider Provider
		{
			get
			{
				return m_Provider;
			}
		}

		/// <summary>
		/// Remove all of the edges and vertices from the graph.
		/// </summary>
		public virtual void Clear()
		{
			VertexOutEdges.Clear();
			m_Edges.Clear();
		}

		/// <summary>
		/// Add a new vertex to the graph and returns it.
		/// 
		/// Complexity: 1 insertion.
		/// </summary>
		/// <returns>Create vertex</returns>
		public virtual IVertex AddVertex()
		{
			IVertex v = Provider.ProvideVertex();
			VertexOutEdges.Add(v, new EdgeCollection());

			return v;
		}

		/// <summary>
		/// Add a new vertex from source to target
		///  
		/// Complexity: 2 search + 1 insertion
		/// </summary>
		/// <param name="source">Source vertex</param>
		/// <param name="target">Target vertex</param>
		/// <returns>Created Edge</returns>
		/// <exception cref="ArgumentNullException">source or target is null</exception>
		/// <exception cref="Exception">source or target are not part of the graph</exception>
		public virtual IEdge AddEdge(
			IVertex source,
			IVertex target
			)
		{
			// look for the vertex in the list
			if (!VertexOutEdges.ContainsKey(source))
				throw new VertexNotFoundException("Could not find source vertex");
			if (!VertexOutEdges.ContainsKey(target))
				throw new VertexNotFoundException("Could not find target vertex");

			// if parralel edges are not allowed check if already in the graph
			if (!this.AllowParallelEdges)
			{
				if (ContainsEdge(source,target))
					return null;
			}

			// create edge
			IEdge e = Provider.ProvideEdge(source,target);
			VertexOutEdges[source].Add(e);
			m_Edges.Add(e);

			return e;
		}

		/// <summary>
		/// Returns the number of out-degree edges of v
		/// </summary>
		/// <param name="v"></param>
		/// <returns></returns>
		public int OutDegree(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("v");
			return VertexOutEdges[v].Count;
		}

		/// <summary>
		/// Returns an iterable collection over the edge connected to v
		/// </summary>
		/// <param name="v"></param>
		/// <returns></returns>
		public EdgeCollection OutEdges(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("v");

			return VertexOutEdges[v];
		}

		/// <summary>
		/// Incidence graph implementation
		/// </summary>
		IEnumerable IIncidenceGraph.OutEdges(IVertex v)
		{
			return this.OutEdges(v);
		}

		/// <summary>
		/// Removes the vertex from the graph.
		/// </summary>
		/// <param name="v">vertex to remove</param>
		/// <exception cref="ArgumentNullException">v is null</exception>
		public virtual void RemoveVertex(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("vertex");
			if (!ContainsVertex(v))
				throw new VertexNotFoundException("v");

			ClearVertex(v);

			// removing vertex
			VertexOutEdges.Remove(v);
		}

		/// <summary>
		/// Remove all edges to and from vertex u from the graph.
		/// </summary>
		/// <param name="v"></param>
		public virtual void ClearVertex(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("vertex");

			// removing edges touching v
			RemoveEdgeIf(new IsAdjacentEdgePredicate(v));

			// removing edges
			VertexOutEdges[v].Clear();
		}

		/// <summary>
		/// Removes an edge from the graph.
		/// 
		/// Complexity: 2 edges removed from the vertex edge list + 1 edge
		/// removed from the edge list.
		/// </summary>
		/// <param name="e">edge to remove</param>
		/// <exception cref="ArgumentNullException">e is null</exception>
		public virtual void RemoveEdge(IEdge e)
		{
			if (e == null)
				throw new ArgumentNullException("edge");
			// removing edge from vertices
			VertexOutEdges[e.Source].Remove(e);
			m_Edges.Remove(e);
		}

		/// <summary>
		/// Remove the edge (u,v) from the graph. 
		/// If the graph allows parallel edges this remove all occurrences of 
		/// (u,v).
		/// </summary>
		/// <param name="u">source vertex</param>
		/// <param name="v">target vertex</param>
		public virtual void RemoveEdge(IVertex u, IVertex v)
		{
			if (u == null)
				throw new ArgumentNullException("source vertex");
			if (v == null)
				throw new ArgumentNullException("targetvertex");

			EdgeCollection edges = VertexOutEdges[u];
			// marking edges to remove
			EdgeCollection removedEdges = new EdgeCollection();
			foreach(IEdge e in edges)
			{
				if (e.Target == v)
					removedEdges.Add(e);
			}
			//removing edges
			foreach(IEdge e in removedEdges)
			{
				edges.Remove(e);
				m_Edges.Remove(e);
			}
		}
		

		/// <summary>
		/// Remove all the edges from graph g for which the predicate pred
		/// returns true.
		/// </summary>
		/// <param name="pred">edge predicate</param>
		public void RemoveEdgeIf(IEdgePredicate pred)
		{
			if (pred == null)
				throw new ArgumentNullException("predicate");

			// marking edge for removal
			EdgeCollection removedEdges = new EdgeCollection();
			foreach(IEdge e in Edges)
			{
				if (pred.Test(e))
					removedEdges.Add(e);
			}

			// removing edges
			foreach(IEdge e in removedEdges)
				RemoveEdge(e);
		}

		/// <summary>
		/// Remove all the out-edges of vertex u for which the predicate pred 
		/// returns true.
		/// </summary>
		/// <param name="u">vertex</param>
		/// <param name="pred">edge predicate</param>
		public void RemoveOutEdgeIf(IVertex u, IEdgePredicate pred)
		{
			if (u==null)
				throw new ArgumentNullException("vertex u");
			if (pred == null)
				throw new ArgumentNullException("predicate");

			EdgeCollection edges = VertexOutEdges[u];
			EdgeCollection removedEdges = new EdgeCollection();
			foreach(IEdge e in edges)
				if (pred.Test(e))
					removedEdges.Add(e);

			foreach(IEdge e in removedEdges)
				RemoveEdge(e);
		}

		/// <summary>
		/// The number of vertices
		/// </summary>
		public int VerticesCount
		{
			get
			{
				return VertexOutEdges.Count;
			}
		}

		/// <summary>
		/// Enumerable collection of vertices.
		/// </summary>
		public IEnumerable Vertices
		{
			get
			{
				return VertexOutEdges.Keys;
			}
		}

		/// <summary>
		/// Tests if a vertex is part of the graph
		/// </summary>
		/// <param name="v">Vertex to test</param>
		/// <returns>true if is part of the graph, false otherwize</returns>
		public bool ContainsVertex(IVertex v)
		{
			return VertexOutEdges.Contains(v);
		}

		/// <summary>
		/// Edge count
		/// </summary>
		public int EdgesCount
		{
			get
			{
				return m_Edges.Count;
			}
		}

		/// <summary>
		/// Enumerable collection of edges.
		/// </summary>
		public EdgeCollection Edges
		{
			get
			{
				return m_Edges;
			}
		}

		/// <summary>
		/// IEdgeListGraph implementation
		/// </summary>
		IEnumerable IEdgeListGraph.Edges
		{
			get
			{
				return this.Edges;
			}
		}

		/// <summary>
		/// Tests if a edge is part of the graph
		/// </summary>
		/// <param name="v">Edge to test</param>
		/// <returns>true if is part of the graph, false otherwize</returns>
		public bool ContainsEdge(IEdge v)
		{
			return m_Edges.Contains(v);
		}

		/// <summary>
		/// Test is an edge (u,v) is part of the graph
		/// </summary>
		/// <param name="u">source vertex</param>
		/// <param name="v">target vertex</param>
		/// <returns>true if part of the graph</returns>
		public bool ContainsEdge(IVertex u, IVertex v)
		{
			// try to find the edge
			foreach(IEdge e in this.OutEdges(u))
			{
				if (e.Target == v)
					return true;
			}
			return false;
		}
	}
}
