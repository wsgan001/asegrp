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

namespace QuickGraph.Concepts.Modifications
{
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;

	/// <summary>
	/// Description résumée de RandomGraph.
	/// </summary>
	public class RandomGraph
	{
		/// <summary>
		/// Picks a vertex randomly in the vertex list
		/// </summary>
		/// <param name="g">vertex list</param>
		/// <param name="rnd">random generator</param>
		/// <returns>randomaly chosen vertex</returns>
		public static IVertex Vertex(IVertexListGraph g, Random rnd)
		{
			if (g == null)
				throw new ArgumentNullException("g");
			if (rnd == null)
				throw new ArgumentNullException("random generator");
			if (g.VerticesCount == 0)
				throw new ArgumentException("g is empty");

			int i = rnd.Next(g.VerticesCount);
			foreach(IVertex v in g.Vertices)
			{
				if (i==0)
					return v;
				else
					--i;
			}
			
			// failed
			throw new Exception();
		}

		/// <summary>
		/// Picks an edge randomly in the edge list
		/// </summary>
		/// <param name="g">edge list</param>
		/// <param name="rnd">random generator</param>
		/// <returns>randomaly chosen edge</returns>
		public static IEdge Edge(IEdgeListGraph g, Random rnd)
		{
			if (g == null)
				throw new ArgumentNullException("g");
			if (rnd == null)
				throw new ArgumentNullException("random generator");
			if (g.EdgesCount == 0)
				throw new ArgumentException("g is empty");

			int i = rnd.Next(g.EdgesCount);
			foreach(IEdge e in g.Edges)
			{
				if (i==0)
					return e;
				else
					--i;
			}
			
			// failed
			throw new Exception();
		}

		/// <summary>
		/// Generates a random graph
		/// </summary>
		/// <param name="g">Graph to fill</param>
		/// <param name="V">number of vertices</param>
		/// <param name="E">number of edges</param>
		/// <param name="rnd">random generator</param>
		/// <param name="selfEdges">self edges allowed</param>
		public static void Graph(
			IEdgeMutableGraph g, 
			int V, 
			int E, 
			Random rnd, 
			bool selfEdges
			)
		{
			if (g == null)
				throw new ArgumentNullException("g");
			if (rnd == null)
				throw new ArgumentNullException("random generator");

			IVertex a=null;
			IVertex b=null;
			// When parallel edges are not allowed, we create a new graph which
			// does not allow parallel edges, construct it and copy back.
			// This is not efficient if 'g' already disallow parallel edges,
			// but that's task for later.
			if (!g.AllowParallelEdges) 
			{
				throw new Exception("not implemented");				
			} 
			else 
			{
				for (int i = 0; i < V; ++i)
					g.AddVertex();
      
				for (int j = 0; j < E; ++j) 
				{
					a = Vertex((IVertexListGraph)g, rnd);
					do 
					{
						b = Vertex((IVertexListGraph)g, rnd);
					} 
					while (selfEdges == false && a == b);

					g.AddEdge(a, b);
				}
			}
		}
	}
}
