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



namespace QuickGraph.Algorithms.Graphviz
{
	using System;
	using System.IO;
	using System.Text;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;

	/// <summary>
	/// A graphviz writer.
	/// </summary>
	/// <remarks>
	/// <para>
	/// This algorithms "renders" the graph to the dot format. dot is an open
	/// source application for drawing graphs available at  
	/// http://www.research.att.com/sw/tools/graphviz/.
	/// </para>
	/// <para>
	/// Further output customization can be achieved by using visitors and
	/// events. The events are
	/// <list>
	/// <para>The GraphViz .Net wrapping was found written by Leppied and can be found 
	/// at http://www.codeproject.com/dotnet/dfamachine.asp</para>
	/// <listheader>
	///		<term>Event</term>
	///		<description>Description</description>
	/// </listheader>
	/// <item>
	///		<term>WriteGraph</term>
	///		<description>Add graph and global customization code here.</description>
	/// </item>
	/// <item>
	///		<term>WriteVertex</term>
	///		<description>Add vertex appearance customization code here.</description>
	/// </item>
	/// <item>
	///		<term>WriteEdge</term>
	///		<description>Add edge appearance customization code here.</description>
	/// </item>
	/// </list>
	/// </para>
	/// </remarks>
	/// <example>
	/// This basic samples outputs the graph to a file:
	/// <code>				
	/// FileStream file = new FileStream(...,FileMode.Create, FileAccess.Write);
	/// GraphvizWriterAlgorithm gw = new GraphvizWriterAlgorithm(g,file);
	/// gw.Write();
	/// </code>
	/// </example>
	public class GraphvizAlgorithm
	{
		private IVertexAndEdgeListGraph m_VisitedGraph;
		private StringWriter m_StringWriter;
		private DotRenderer m_Dot;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="g"></param>
		public GraphvizAlgorithm(IVertexAndEdgeListGraph g)
		{
			if (g == null)
				throw new ArgumentNullException("g");
			m_VisitedGraph = g;
			m_StringWriter = null;
			m_Dot = new DotRenderer();
		}

		/// <summary>
		/// Builds a new Graphviz writer of the graph g using the Stream s.
		/// </summary>
		/// <param name="g">Graph to visit.</param>
		/// <param name="prefix"></param>
		/// <param name="path">Path where files are to be created</param>
		/// <param name="imageType">image output type</param>
		public GraphvizAlgorithm(
			IVertexAndEdgeListGraph g, 
			String prefix,
			String path,
			GraphvizImageType imageType
			)
		{
			if (g == null)
				throw new ArgumentNullException("g");
			m_VisitedGraph = g;
			m_StringWriter = null;
			m_Dot = new DotRenderer(prefix,path,imageType);
		}


		/// <summary>
		/// Visited graph
		/// </summary>
		public IVertexAndEdgeListGraph VisitedGraph
		{
			get
			{
				return m_VisitedGraph;
			}
			set
			{
				if (value==null)
					throw new ArgumentNullException("graph");
				m_VisitedGraph = value;
			}
		}

		/// <summary>
		/// Renderer
		/// </summary>
		public DotRenderer Renderer
		{
			get
			{
				return m_Dot;
			}
		}

		/// <summary>
		/// Dot output stream.
		/// </summary>
		public StringWriter Output
		{
			get
			{
				return m_StringWriter;
			}
		}

		#region Events
		/// <summary>
		/// Output the graph properties
		/// </summary>
		public event EventHandler WriteGraph;

		/// <summary>
		/// Outputs the vertex property
		/// </summary>
		public event VertexHandler WriteVertex;

		/// <summary>
		/// 
		/// </summary>
		public event EdgeHandler WriteEdge;
		#endregion Events

		/// <summary>
		/// Generates the dot code to be rendered with GraphViz
		/// </summary>
		/// <param name="imageType"></param>
		/// <returns>outputed file name</returns>
		public String Write(GraphvizImageType imageType)
		{
			m_Dot.ImageType = imageType;
			return Write();
		}

		/// <summary>
		/// Generates the dot code to be rendered with GraphViz
		/// </summary>
		/// <returns>outputed file name.</returns>
		public String Write()
		{
			m_StringWriter = new StringWriter();

			Output.WriteLine("digraph G");
			Output.WriteLine("{");

			if (WriteGraph != null)
				WriteGraph(this, new EventArgs());

			if (WriteVertex != null)
			{
				foreach(IVertex v in VisitedGraph.Vertices)
				{
					Output.Write("{0} [",v.GetHashCode());
					WriteVertex(this, new VertexEventArgs(v));
					Output.WriteLine("];");
				}
			}

			foreach(IEdge e in VisitedGraph.Edges)
			{
				Output.Write("{0} -> {1}",
						e.Source.GetHashCode(),
						e.Target.GetHashCode()
						);
				if (WriteEdge != null)
				{
					Output.Write(" [");
					WriteEdge(this, new EdgeEventArgs(e));
					Output.Write("]");
				}
				Output.WriteLine(";");
			}

			Output.WriteLine("}");
			return m_Dot.Render(Output.ToString());
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="vis"></param>
		public void RegisterVisitor(IGraphvizVisitor vis)
		{
			WriteGraph += new EventHandler(vis.WriteGraph);
			WriteVertex += new VertexHandler(vis.WriteVertex);
			WriteEdge += new EdgeHandler(vis.WriteEdge);
		}
	}
}
