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

namespace QuickGraph.Concepts
{
	/// <summary>
	/// The Graph concept contains a few requirements that are common to all 
	/// the graph concepts. 
	/// </summary>
	public interface IGraph
	{
		/// <summary>
		/// Directed or undirected graph
		/// </summary>
		/// <value>
		/// True if directed graph
		/// </value>
		bool IsDirected {get;}

		/// <summary>
		/// Parallel edge handling
		/// </summary>
		/// <remarks>
		/// This describes whether the graph class allows the insertion of 
		/// parallel edges (edges with the same source and target). 
		/// </remarks>
		bool AllowParallelEdges{get;}
	}
}
