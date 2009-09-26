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
	using QuickGraph.Concepts.Providers;
	using QuickGraph.Concepts.Modifications;
	using QuickGraph.Concepts.Predicates;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Collections;
	using QuickGraph.Exceptions;
	using QuickGraph.Predicates;

	/// <summary>
	/// A mutable bidirectional graph implemetation
	/// </summary>
	/// <remarks>
	/// <seealso cref="AdjacencyGraph"/>
	/// <seealso cref="IBidirectionalGraph"/>
	/// <seealso cref="IMutableBidirectionalGraph"/>
	/// </remarks>
	public class BidirectionalGraph :
		AdjacencyGraph,
		IBidirectionalGraph,
		IMutableBidirectionalGraph
	{
		private VertexEdgesDictionary m_VertexInEdges;

		/// <summary>
		/// Builds a new empty graph
		/// </summary>
		public BidirectionalGraph(
			IVertexAndEdgeProvider provider,
			bool allowParallelEdges
			)
			: base(provider,allowParallelEdges)
		{
			m_VertexInEdges = new VertexEdgesDictionary();
		}
		/// <summary>
		/// Vertex Out edges dictionary
		/// </summary>
		protected VertexEdgesDictionary VertexInEdges
		{
			get
			{
				return m_VertexInEdges;
			}
		}

		/// <summary>
		/// Remove all of the edges and vertices from the graph.
		/// </summary>
		public override void Clear()
		{
			base.Clear();
			VertexInEdges.Clear();
		}

		/// <summary>
		/// Add a new vertex to the graph and returns it.
		/// 
		/// Complexity: 1 insertion.
		/// </summary>
		/// <returns>Create vertex</returns>
		public override IVertex AddVertex()
		{
			IVertex v = base.AddVertex();
			VertexInEdges.Add(v, new EdgeCollection());

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
		public override IEdge AddEdge(
			IVertex source,
			IVertex target
			)
		{
			// look for the vertex in the list
			if (!VertexInEdges.ContainsKey(source))
				throw new VertexNotFoundException("Could not find source vertex");
			if (!VertexInEdges.ContainsKey(target))
				throw new VertexNotFoundException("Could not find target vertex");

			// create edge
			IEdge e = base.AddEdge(source,target);
			VertexInEdges[target].Add(e);

			return e;
		}

		/// <summary>
		/// Returns the number of in-degree edges of v
		/// </summary>
		/// <param name="v"></param>
		/// <returns></returns>
		public int InDegree(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("v");
			return VertexInEdges[v].Count;
		}

		/// <summary>
		/// Returns an iterable collection over the in-edge connected to v
		/// </summary>
		/// <param name="v"></param>
		/// <returns></returns>
		public EdgeCollection InEdges(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("v");

			return VertexInEdges[v];
		}

		/// <summary>
		/// Incidence graph implementation
		/// </summary>
		IEnumerable IBidirectionalGraph.InEdges(IVertex v)
		{
			return this.InEdges(v);
		}

		/// <summary>
		/// Removes the vertex from the graph.
		/// </summary>
		/// <param name="v">vertex to remove</param>
		/// <exception cref="ArgumentNullException">v is null</exception>
		public override void RemoveVertex(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("vertex");

			base.RemoveVertex(v);
			// removing vertex
			VertexInEdges.Remove(v);
		}

		/// <summary>
		/// Remove all edges to and from vertex u from the graph.
		/// </summary>
		/// <param name="v"></param>
		public override void ClearVertex(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("vertex");

			base.ClearVertex(v);
			VertexInEdges[v].Clear();
		}

		/// <summary>
		/// Removes an edge from the graph.
		/// 
		/// Complexity: 2 edges removed from the vertex edge list + 1 edge
		/// removed from the edge list.
		/// </summary>
		/// <param name="e">edge to remove</param>
		/// <exception cref="ArgumentNullException">e is null</exception>
		public override void RemoveEdge(IEdge e)
		{
			if (e == null)
				throw new ArgumentNullException("edge");
			base.RemoveEdge(e);
			// removing edge from vertices (in - edges)
			VertexInEdges[e.Target].Remove(e);
		}

		/// <summary>
		/// Remove the edge (u,v) from the graph. 
		/// If the graph allows parallel edges this remove all occurrences of 
		/// (u,v).
		/// </summary>
		/// <param name="u">source vertex</param>
		/// <param name="v">target vertex</param>
		public override void RemoveEdge(IVertex u, IVertex v)
		{
			if (u == null)
				throw new ArgumentNullException("source vertex");
			if (v == null)
				throw new ArgumentNullException("targetvertex");

			EdgeCollection outEdges = VertexOutEdges[u];
			EdgeCollection inEdges = VertexInEdges[v];
			// marking edges to remove
			EdgeCollection removedEdges = new EdgeCollection();
			foreach(IEdge e in outEdges)
			{
				if (e.Target == v)
					removedEdges.Add(e);
			}
			foreach(IEdge e in inEdges)
			{
				if (e.Source == u)
					removedEdges.Add(e);
			}

			//removing edges
			foreach(IEdge e in removedEdges)
				RemoveEdge(e);
		}

		/// <summary>
		/// Remove all the out-edges of vertex u for which the predicate pred 
		/// returns true.
		/// </summary>
		/// <param name="u">vertex</param>
		/// <param name="pred">edge predicate</param>
		public void RemoveInEdgeIf(IVertex u, IEdgePredicate pred)
		{
			if (u==null)
				throw new ArgumentNullException("vertex u");
			if (pred == null)
				throw new ArgumentNullException("predicate");

			EdgeCollection edges = VertexInEdges[u];
			EdgeCollection removedEdges = new EdgeCollection();
			foreach(IEdge e in edges)
				if (pred.Test(e))
					removedEdges.Add(e);

			foreach(IEdge e in removedEdges)
				RemoveEdge(e);
		}

		/// <summary>
		/// Returns the number of in-edges plus out-edges.
		/// </summary>
		/// <param name="v"></param>
		/// <returns></returns>
		public int Degree(IVertex v)
		{
			if (v == null)
				throw new ArgumentNullException("v");

			return VertexInEdges[v].Count + VertexOutEdges[v].Count;
		}
	}
}
