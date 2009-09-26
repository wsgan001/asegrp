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
	using System.Drawing;
	using System.Collections;

	using QuickGraph.Collections;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Traversals;
	using QuickGraph.Concepts.Algorithms;
	using QuickGraph.Concepts.Visitors;

	/// <summary>
	/// A visitor that output the graph color state at each change
	/// </summary>
	public class AlgorithmTracerVisitor :
		IVertexColorizerVisitor,
		ITreeEdgeBuilderVisitor,
		IGraphvizVisitor
	{
		private GraphvizVertex m_Vertex;
		private GraphvizEdge m_Edge;
		private GraphvizAlgorithm m_Algo;
		private IDictionary m_Colors;
		private IDictionary m_EdgeColors;
		private IDictionary m_VertexLabels;
		private IDictionary m_EdgeLabels;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="g"></param>
		/// <param name="prefix"></param>
		/// <param name="path"></param>
		/// <param name="imageType"></param>
		public AlgorithmTracerVisitor(
			IVertexAndEdgeListGraph g,
			String prefix,
			String path, 
			GraphvizImageType imageType
			)
		{
			m_Vertex = new GraphvizVertex();
			m_Vertex.Shape = GraphvizVertexShape.Circle;
			m_Vertex.Style = GraphvizVertexStyle.Filled;
			m_VertexLabels =null;

			m_Edge = new GraphvizEdge();
			m_EdgeLabels = null;

			m_Colors = null;
			m_EdgeColors = new EdgeColorDictionary();

			m_Algo = new GraphvizAlgorithm(g,prefix,path,imageType);
			m_Algo.RegisterVisitor(this);
		}


		/// <summary>
		/// Vertex name map
		/// </summary>
		public IDictionary VertexLabels
		{
			get
			{
				return m_VertexLabels;
			}
			set
			{
				m_VertexLabels = value;
			}
		}

		/// <summary>
		/// Edge name map
		/// </summary>
		public IDictionary EdgeLabels
		{
			get
			{
				return m_EdgeLabels;
			}
			set
			{
				m_EdgeLabels = value;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void WriteGraph(Object sender, EventArgs args)
		{

		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void WriteVertex(Object sender, VertexEventArgs args)
		{
			if (m_Colors.Contains(args.Vertex))
			{
				GraphColor c = (GraphColor)m_Colors[args.Vertex];
				m_Vertex.FillColor = GraphColorConverter.Convert(c);
				if (c == GraphColor.Black)
					m_Vertex.FontColor = Color.White;
				else
					m_Vertex.FontColor = Color.Black;
			}
			else
				m_Vertex.StrokeColor = Color.White;

			if (m_VertexLabels != null)
				m_Vertex.Label = m_VertexLabels[args.Vertex].ToString();

			((GraphvizAlgorithm)sender).Output.Write(m_Vertex.ToDot());
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void WriteEdge(Object sender, EdgeEventArgs args)
		{
			if (m_EdgeColors.Contains(args.Edge))
				m_Edge.Style = GraphvizEdgeStyle.Bold;
			else
				m_Edge.Style = GraphvizEdgeStyle.Unspecified;

			if (EdgeLabels != null)
				m_Edge.Label.Value = EdgeLabels[args.Edge].ToString();

			((GraphvizAlgorithm)sender).Output.Write(m_Edge.ToDot());
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void InitializeVertex(Object sender, VertexEventArgs args)
		{}
		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void DiscoverVertex(Object sender, VertexEventArgs args)
		{
			OutputGraph(sender,args);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void FinishVertex(Object sender, VertexEventArgs args)
		{
			OutputGraph(sender,args);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		protected void OutputGraph(Object sender, VertexEventArgs args)
		{
			IVertexColorizerAlgorithm algo = (IVertexColorizerAlgorithm)sender;
			m_Colors = algo.Colors;
			m_Algo.Write();
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="args"></param>
		public void TreeEdge(Object sender, EdgeEventArgs args)
		{
			IVertexColorizerAlgorithm algo = (IVertexColorizerAlgorithm)sender;
			m_Colors = algo.Colors;
			m_EdgeColors[args.Edge] = GraphColor.Black;
			m_Algo.Write();
		}
	}
}
