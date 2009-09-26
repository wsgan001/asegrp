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

namespace QuickGraph.Algorithms
{
	using QuickGraph.Algorithms.Search;
	using QuickGraph.Collections;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;

	/// <summary>
	/// Connected component computation
	/// </summary>
	/// <remarks>
	/// <para>
	/// The ConnectedComponentsAlgorithm functions compute the connected 
	/// components of an undirected graph using a DFS-based approach. 
	/// </para>
	/// <para>
	/// A connected component of an undirected graph is a set of vertices that 
	/// are all reachable from each other. 
	/// </para>
	/// <para>
	/// If the connected components need to be maintained while a graph is 
	/// growing the disjoint-set based approach of function 
	/// IncrementalComponentsAlgorithm is faster. 
	/// For ``static'' graphs this DFS-based approach is faster. 
	/// </para>
	/// <para>
	/// The output of the algorithm is recorded in the component 
	/// property Components, which will contain numbers giving the 
	/// component number assigned to each vertex. 
	/// </para>
	/// </remarks>
	public class ConnectedComponentsAlgorithm
	{
		private int m_Count;
		private VertexIntDictionary m_Components;
		private IVertexListGraph m_VisitedGraph;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="g"></param>
		public ConnectedComponentsAlgorithm(IVertexListGraph g)
		{
			if (g==null)
				throw new ArgumentNullException("g");
			m_VisitedGraph = g;
			m_Components = new VertexIntDictionary();
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

		/// <summary>
		/// Component map
		/// </summary>
		public VertexIntDictionary Components
		{
			get
			{
				return m_Components;
			}
		}

		/// <summary>
		/// Connected components count
		/// </summary>
		public int Count
		{
			get
			{
				return m_Count;
			}
		}

		/// <summary>
		/// Used internally
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void StartVertex(Object sender, VertexEventArgs args)
		{
			if (m_Count == int.MaxValue)
				m_Count = 0; // start counting components at zero
			else
				++m_Count;
		}

		/// <summary>
		/// Used internally
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void DiscoverVertex(Object sender, VertexEventArgs args)
		{
			Components[args.Vertex]=m_Count++;
		}

		/// <summary>		
		/// Executes the algorithm
		/// </summary>
		/// <returns>The total number of components is the return value of the function</returns>
		public int Compute()
		{
			m_Count = int.MaxValue;
			m_Components.Clear();

			DepthFirstSearchAlgorithm dfs = new DepthFirstSearchAlgorithm(VisitedGraph);
			dfs.StartVertex += new VertexHandler(this.StartVertex);
			dfs.DiscoverVertex += new VertexHandler(this.DiscoverVertex);

			return m_Count;
		}
	}
}
