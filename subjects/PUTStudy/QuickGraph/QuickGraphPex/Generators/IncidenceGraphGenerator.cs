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

namespace QuickGraphNUnit.Generators
{
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Modifications;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Representations;
	using QuickGraph.Providers;

	/// <summary>
	/// Description r�sum�e de AdjacencyGraphGenerator.
	/// </summary>
	public class AdjacencyGraphGenerator : 
		IGraphGenerator,
		IMutableGraphGenerator,
		IMutableIncidenceGraphGenerator,
		IIncidenceGraphGenerator,
		IVertexListGraphGenerator,
		IMutableEdgeListGraphGenerator
	{
		public AdjacencyGraph G
		{
			get
			{
				return new AdjacencyGraph(new VertexAndEdgeProvider(),true);
			}
		}

		public AdjacencyGraph ACyclicGraph
		{
			get
			{
				AdjacencyGraph g =  new AdjacencyGraph(new VertexAndEdgeProvider(),true);
				IVertex u = g.AddVertex();
				IVertex v = g.AddVertex();
				IVertex w = g.AddVertex();
				g.AddEdge(u,v);
				g.AddEdge(v,w);
				g.AddEdge(u,w);

				return g;
			}
		}

		public AdjacencyGraph CyclicGraph
		{
			get
			{
				AdjacencyGraph g =  new AdjacencyGraph(new VertexAndEdgeProvider(),true);
				IVertex u = g.AddVertex();
				IVertex v = g.AddVertex();
				IVertex w = g.AddVertex();
				g.AddEdge(u,v);
				g.AddEdge(v,w);
				g.AddEdge(w,u);

				return g;
			}
		}

		public IGraph Graph(bool allowParallelEdges)
		{
			return new AdjacencyGraph(new VertexAndEdgeProvider(),allowParallelEdges);
		}

		public IEdgeMutableGraph MutableGraph
		{
			get
			{
				return this.G;
			}
		}
		public IMutableIncidenceGraph MutableIncidenceGraph
		{
			get
			{
				
				return this.G;
			}
		}
		public IMutableEdgeListGraph MutableEdgeListGraph
		{
			get
			{
				return this.ACyclicGraph;
			}
		}
		public IIncidenceGraph IncidenceGraph
		{
			get
			{
				return this.ACyclicGraph;
			}
		}
		public IVertexListGraph VertexList
		{
			get
			{
				return this.ACyclicGraph;
			}
		}
		public int VertexListCount
		{
			get
			{
				return 3;
			}
		}
	}
}
