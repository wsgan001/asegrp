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

namespace QuickGraphTest
{
	using QuickGraph.Concepts;
	using QuickGraph.Providers;
	using QuickGraph.Representations;
	using QuickGraph.Collections;
	using QuickGraph.Algorithms.Graphviz;

	public class MainClass
	{
		[STAThread]
		static void Main(string[] args)
		{
			FileDependencyTest fd =new FileDependencyTest();

			fd.Test();
		}
	}

	/// <summary>
	/// Description résumée de FileDependencyTest.
	/// </summary>
	public class FileDependencyTest
	{
		private GraphvizVertex m_Vertex;
		private VertexStringDictionary m_Names;

		public FileDependencyTest()
		{
			m_Vertex = new GraphvizVertex();			
			m_Names = new VertexStringDictionary();
		}

		#region Properties
		public GraphvizVertex Vertex
		{
			get
			{
				return m_Vertex;
			}
		}

		public VertexStringDictionary Names
		{
			get
			{
				return m_Names;
			}
		}
		#endregion

		public void WriteVertex(Object sender, VertexEventArgs args)
		{
			// setting the vertex name
			Vertex.Label = Names[args.Vertex];

			// outputting to dot
			// sender is the graphviz writer algorithm
			((GraphvizAlgorithm)sender).Output.Write(Vertex.ToDot());
		}

		public void Test()
		{
			// create a new adjacency graph
			AdjacencyGraph g = new AdjacencyGraph(new VertexAndEdgeProvider(), false);

			// adding files and storing names
			IVertex zig_cpp = g.AddVertex();	Names[zig_cpp]="zip.cpp";
			IVertex boz_h = g.AddVertex();		Names[boz_h]="boz.h";
			IVertex zag_cpp = g.AddVertex();	Names[zag_cpp]="zag.cpp";
			IVertex yow_h = g.AddVertex();		Names[yow_h]="yow.h";
			IVertex dax_h = g.AddVertex();		Names[dax_h]="dax.h";
			IVertex bar_cpp = g.AddVertex();	Names[bar_cpp]="bar.cpp";
			IVertex zow_h = g.AddVertex();		Names[zow_h]="zow.h";
			IVertex foo_cpp = g.AddVertex();	Names[foo_cpp]="foo.cpp";

			IVertex zig_o = g.AddVertex();		Names[zig_o]="zig.o";
			IVertex zag_o = g.AddVertex();		Names[zag_o]="zago";
			IVertex bar_o = g.AddVertex();		Names[bar_o]="bar.o";
			IVertex foo_o = g.AddVertex();		Names[foo_o]="foo.o";
			IVertex libzigzag_a = g.AddVertex();Names[libzigzag_a]="libzigzig.a";
			IVertex libfoobar_a = g.AddVertex();Names[libfoobar_a]="libfoobar.a";

			IVertex killerapp = g.AddVertex();	Names[killerapp]="killerapp";

			// adding dependencies
			g.AddEdge(dax_h, foo_cpp); 
			g.AddEdge(dax_h, bar_cpp); 
			g.AddEdge(dax_h, yow_h);
			g.AddEdge(yow_h, bar_cpp); 
			g.AddEdge(yow_h, zag_cpp);
			g.AddEdge(boz_h, bar_cpp); 
			g.AddEdge(boz_h, zig_cpp); 
			g.AddEdge(boz_h, zag_cpp);
			g.AddEdge(zow_h, foo_cpp); 
			g.AddEdge(foo_cpp, foo_o);
			g.AddEdge(foo_o, libfoobar_a);
			g.AddEdge(bar_cpp, bar_o);
			g.AddEdge(bar_o, libfoobar_a);
			g.AddEdge(libfoobar_a, libzigzag_a);
			g.AddEdge(zig_cpp, zig_o);
			g.AddEdge(zig_o, libzigzag_a);
			g.AddEdge(zag_cpp, zag_o);
			g.AddEdge(zag_o, libzigzag_a);
			g.AddEdge(libzigzag_a, killerapp);


			// outputing graph to png
			GraphvizAlgorithm gw = new GraphvizAlgorithm(
				g,                      // graph to draw
				"filedependency",         // output file prefix
				".",                    // output file path
				GraphvizImageType.Png   // output file type
				);
			// outputing to graph.
			gw.Write();

			// attaching the file name outputer...
			gw.WriteVertex += new VertexHandler(this.WriteVertex);
			gw.Write();
		}
	}
}
