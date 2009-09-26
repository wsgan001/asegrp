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
	using QuickGraph.Concepts.Traversals;

	/// <summary>
	/// An edge-list representation of a graph is simply a sequence of edges,
	/// where each edge is represented as a pair of vertex ID's.
	/// </summary>
	/// <remarks>
	/// <para>
	/// The EdgeList class is an adaptor that turns an edge collection
	/// into a class that models IEdgeListGraph. 
	/// The value type of the edge collection must be be inherited form IEdge.
	/// </para>
	/// <para>
	/// An edge-list representation of a graph is simply a sequence of edges, 
	/// where each edge is represented as a pair of vertex ID's. 
	/// The memory required is only O(E). Edge insertion is typically O(1), 
	/// though accessing a particular edge is O(E) (not efficient). 
	/// </para>
	/// <seealso cref="IEdgeListGraph"/>
	/// </remarks>
	public class EdgeList : IEdgeListGraph
	{
		private ICollection m_Edges;
		private bool m_IsDirected;
		private bool m_AllowParallelEdges;

		/// <summary>
		/// Builds an EdgeListGraph out of a edges collection
		/// </summary>
		/// <param name="edges"></param>
		/// <param name="isDirected"></param>
		/// <param name="allowParallelEdges"></param>
		public EdgeList(
			ICollection edges,
			bool isDirected, 
			bool allowParallelEdges
			)
		{
			if (edges == null)
				throw new ArgumentNullException("edge collection");

			m_Edges = edges;
			m_IsDirected = isDirected;
			m_AllowParallelEdges = allowParallelEdges;
		}

		/// <summary>
		/// 
		/// </summary>
		public bool IsDirected
		{
			get
			{
				return m_IsDirected;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		public bool AllowParallelEdges
		{
			get
			{
				return m_AllowParallelEdges;
			}
		}

		/// <summary>
		/// Returns the number of edges in the graph.
		/// </summary>
		public int EdgesCount
		{
			get
			{
				return m_Edges.Count;
			}
		}

		/// <summary>
		/// Returns an enumerator providing access to all the edges in the graph.
		/// </summary>
		public ICollection Edges
		{
			get
			{
				return m_Edges;
			}
		}

		/// <summary>
		/// IEdgeListGraph implemetentation.
		/// </summary>
		IEnumerable IEdgeListGraph.Edges
		{
			get
			{
				return this.Edges;
			}
		}
	}
}
