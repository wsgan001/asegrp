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



namespace QuickGraph.Algorithms.Visitors
{
	using System;
	using QuickGraph.Collections;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Visitors;

	/// <summary>
	/// Visitor that computes the vertices predecessors.
	/// </summary>
	/// <remarks>
	///  The visitor applies to any algorithm that implements the 
	///  <seealso cref="QuickGraph.Concepts.Algorithms.IPredecessorRecorderAlgorithm"/> model.
	///  </remarks>
	///  <example>
	///  This sample shows how to use the find the predecessor map using a
	///  <seealso cref="Search.DepthFirstSearchAlgorithm"/>:
	///  <code>
	///  Graph g = ...;
	///  
	///  // creating dfs algorithm
	///  DepthFirstSearchAlgorithm dfs = new DepthFirstSearchAlgorithm(g);
	///  
	///  // creating predecessor visitor
	///  PredecessorVisitor pred = new PredecessorVisitor();
	///  
	///  // registering event handlers
	///  pred.RegisterHandlers(dfs);
	///  
	///  //executing...
	///  dfs.Compute();
	///  
	///  // pred.Predecessors now contains the map of predecessors.
	///  </code>
	///  </example>
	public class PredecessorRecorderVisitor :
		IPredecessorRecorderVisitor
	{
		private VertexEdgeDictionary m_Predecessors;

		/// <summary>
		/// Default constructor
		/// </summary>
		public PredecessorRecorderVisitor()
		{
			m_Predecessors = new VertexEdgeDictionary();
		}

		/// <summary>
		/// Constructor, uses the given predecessor map.
		/// </summary>
		/// <param name="predecessors">Predecessor map</param>
		/// <exception cref="ArgumentNullException">predecessors is null</exception>
		public PredecessorRecorderVisitor(VertexEdgeDictionary predecessors)
		{
			if (predecessors == null)
				throw new ArgumentNullException("predecessors");
			m_Predecessors = predecessors;
		}

		/// <summary>
		/// Vertex Edge predecessor map.
		/// </summary>
		public VertexEdgeDictionary Predecessors
		{
			get
			{
				return m_Predecessors;
			}
		}

		/// <summary>
		/// Initializes each vertex predecessor
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void InitializeVertex(Object sender, VertexEventArgs args)
		{
			Predecessors[args.Vertex]=null;
		}

		/// <summary>
		/// Let e = (u,v), p[v]=u
		/// </summary>
		public void TreeEdge(Object sender, EdgeEventArgs args)
		{
			Predecessors[args.Edge.Target]=args.Edge;
		}
	}
}
