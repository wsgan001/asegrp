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
	using System.Collections;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Collections;
	using QuickGraph.Algorithms.Search;

	/// <summary>
	/// Computes the graph strong components.
	/// </summary>
	/// <remarks>
	/// The strong_components() functions compute the strongly connected 
	/// components of a directed graph using Tarjan's algorithm based on DFS.
	/// </remarks>
	public class StrongComponentsAlgorithm
	{
		private IVertexListGraph m_VisitedGraph;
		private VertexIntDictionary m_Components;
		private VertexIntDictionary m_DiscoverTimes;
		private VertexVertexDictionary m_Roots;
		private Stack m_Stack;
		int m_Count;
		int m_DfsTime;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="g"></param>
		public StrongComponentsAlgorithm(IVertexListGraph g)
		{
			if (g==null)
				throw new ArgumentNullException("g");
			m_VisitedGraph = g;
			m_Components = new VertexIntDictionary();
			m_Roots = new VertexVertexDictionary();
			m_DiscoverTimes = new VertexIntDictionary();
			m_Stack = new Stack();
			m_Count = 0;
			m_DfsTime = 0;
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
		/// Root map
		/// </summary>
		public VertexVertexDictionary Roots
		{
			get
			{
				return m_Roots;
			}
		}

		/// <summary>
		/// Vertex discory times
		/// </summary>
		public VertexIntDictionary DiscoverTimes
		{
			get
			{
				return m_DiscoverTimes;
			}
		}

		/// <summary>
		/// Used internally
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void DiscoverVertex(Object sender, VertexEventArgs args)
		{
			IVertex v = args.Vertex;

			Roots[v]=v;
			Components[v]=int.MaxValue;
			DiscoverTimes[v]=m_DfsTime++;
			m_Stack.Push(v);
		}

		/// <summary>
		/// Used internally
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void FinishVertex(Object sender, VertexEventArgs args)
		{
			IVertex v = args.Vertex;
			foreach(IEdge e in VisitedGraph.OutEdges(v))
			{
				IVertex w = e.Target;

				if (Components[w] == int.MaxValue)
					Roots[v]=MinDiscoverTime(Roots[v],Roots[w]);
			}

			if (Roots[v] == v) 
			{
				IVertex w=null;
				do 
				{
					w = (IVertex)m_Stack.Peek(); 
					m_Stack.Pop();
					Components[w]=m_Count;
				} 
				while (w != v);
				++m_Count;
			}	
		}

		internal IVertex MinDiscoverTime(IVertex u, IVertex v)
		{
			if (DiscoverTimes[u]<DiscoverTimes[v])
				return u;
			else
				return v;
		}

		/// <summary>
		/// Executes the algorithm
		/// </summary>
		/// <remarks>
		/// The output of the algorithm is recorded in the component property 
		/// Components, which will contain numbers giving the component ID 
		/// assigned to each vertex. 
		/// </remarks>
		/// <returns>The number of components is the return value of the function.</returns>
		public int Compute()
		{
			m_Components.Clear();
			m_Roots.Clear();
			m_DiscoverTimes.Clear();
			m_Count = 0;
			m_DfsTime = 0;

			DepthFirstSearchAlgorithm dfs = new DepthFirstSearchAlgorithm(VisitedGraph);
			dfs.DiscoverVertex += new VertexHandler(this.DiscoverVertex);
			dfs.FinishVertex += new VertexHandler(this.FinishVertex);

			dfs.Compute();

			return ++m_Count;
		}
	}
}
