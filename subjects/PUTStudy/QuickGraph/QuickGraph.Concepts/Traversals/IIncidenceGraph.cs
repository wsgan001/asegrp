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

namespace QuickGraph.Concepts.Traversals
{
	using System.Collections;
	/// <summary>
	/// Access to each vertex out-edges.
	/// </summary>
	/// <remarks>
	/// <para>
	/// The IncidenceGraph concept provides an interface for efficient access 
	/// to the out-edges of each vertex in the graph. 
	/// </para>
	/// <seealso cref="IGraph"/>
	/// </remarks>
	public interface IIncidenceGraph : IGraph
	{
		/// <summary>
		/// Returns the number of out-degree edges of v
		/// </summary>
		/// <param name="v">vertex to test</param>
		/// <returns>out-degree</returns>
		int OutDegree(IVertex v);

		/// <summary>
		/// Returns an iterable collection of the out edges of v
		/// </summary>
		IEnumerable OutEdges(IVertex v);
	}
}
