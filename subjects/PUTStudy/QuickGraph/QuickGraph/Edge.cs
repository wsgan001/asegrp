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


namespace QuickGraph
{
	using System;
	using QuickGraph.Concepts;

	/// <summary>
	/// A graph edge
	/// </summary>
	/// <remarks>
	/// This class represents a directed edge. It links
	/// a source <seealso cref="Vertex"/> to a target <seealso cref="Vertex"/>.
	/// 
	/// The source and target vertices can be accessed as properties.
	/// </remarks>
	/// <example>
	/// This sample shows a basic usage of an edge:
	/// <code>
	/// Vertex v;   // vertex
	/// foreach(Edge e in v.InEdges)
	/// {
	///     Console.WriteLine("{0} -> {1}",
	///			e.Source.GetHashCode(),
	///			e.Target.GetHashCode()
	///			);
	/// }
	/// </code>
	/// </example>
	public class Edge : IEdge
	{
		private Vertex m_Source;
		private Vertex m_Target;

		/// <summary>
		/// Builds an edge from source to target
		/// </summary>
		/// <param name="source">Source vertex</param>
		/// <param name="target">Target vertex</param>
		/// <exception cref="ArgumentNullException">Source or Target is null</exception>
		public Edge(Vertex source, Vertex target)
		{
			if (source == null)
				throw new ArgumentNullException("Source");
			if (target == null)
				throw new ArgumentNullException("Target");
			m_Source = source;
			m_Target = target;
		}

		/// <summary>
		/// Compares two edges
		/// </summary>
		/// <param name="obj">Edge to compare</param>
		/// <returns></returns>
		/// <exception cref="ArgumentException">obj is not of type Edge.</exception>
		public int CompareTo(Object obj)
		{
			if (!(obj is Edge))
				throw new ArgumentException("Cannot compare two objects");
			return GetHashCode().CompareTo(obj.GetHashCode());
		}

		/// <summary>
		/// Source vertex
		/// </summary>
		public Vertex Source
		{
			get
			{
				return m_Source;
			}
		}

		IVertex IEdge.Source
		{
			get
			{
				return (IVertex)this.Source;
			}
		}

		/// <summary>
		/// Target Vertex
		/// </summary>
		public Vertex Target
		{
			get
			{
				return m_Target;
			}
		}

		IVertex IEdge.Target
		{
			get
			{
				return (IVertex)this.Target;
			}
		}
	}
}
