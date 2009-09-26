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

namespace QuickGraph.Providers
{
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Providers;
	/// <summary>
	/// Description résumée de VertexAndEdgeGenerator.
	/// </summary>
	public class VertexAndEdgeProvider :
		IVertexAndEdgeProvider
	{
		/// <summary>
		/// Creates a new vertex
		/// </summary>
		/// <returns></returns>
		public Vertex ProvideVertex()
		{
			return new Vertex();
		}

		IVertex IVertexProvider.ProvideVertex()
		{
			return this.ProvideVertex();
		}

		/// <summary>
		/// Creates a new edge
		/// </summary>
		/// <param name="u"></param>
		/// <param name="v"></param>
		/// <returns></returns>
		public Edge ProvideEdge(Vertex u, Vertex v)
		{
			return new Edge(u,v);
		}
		IEdge IEdgeProvider.ProvideEdge(IVertex u, IVertex v)
		{
			return this.ProvideEdge((Vertex)u, (Vertex)v);
		}
	}
}
