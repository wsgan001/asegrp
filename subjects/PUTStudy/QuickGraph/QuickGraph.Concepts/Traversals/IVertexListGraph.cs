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
using System.Collections;

namespace QuickGraph.Concepts.Traversals
{
	/// <summary>
	/// The VertexListGraph concept refines the Graph concept, 
	/// and adds the requirement for efficient traversal of all the vertices 
	/// in the graph. 
	/// </summary>
	/// <remarks>
	/// <para>
	/// One issue in the design of this concept is whether to include the 
	/// refinement from the IncidenceGraph
	/// concepts. 
	/// </para>
	/// <para>
	/// The ability to traverse the vertices of a graph is orthogonal to 
	/// traversing out-edges, so it would make sense to have a VertexListGraph 
	/// concept that only includes vertex traversal. 
	/// </para>
	/// <para>
	/// However, such a concept would no longer really be a graph, 
	/// but would just be a set, and the STL already has concepts for dealing 
	/// with such things. However, there are many BGL algorithms that need to 
	/// traverse the vertices and out-edges of a graph, so for convenience a 
	/// concept is needed that groups these requirements together, 
	/// hence the VertexListGraph concept. 
	/// </para>
	/// <seealso cref="IGraph"/>
	/// <seealso cref="IIncidenceGraph"/>
	/// </remarks>
	public interface IVertexListGraph : IIncidenceGraph
	{
		/// <summary>
		/// Returns the number of vertices in the graph.
		/// </summary>
		int VerticesCount {get;}
		/// <summary>
		/// Returns an iterator-range providing access to all the vertices in 
		/// the graph.
		/// </summary>
		IEnumerable Vertices {get;}
	}
}
