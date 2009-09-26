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
	using System.Drawing;
	using System.Collections;

	/// <summary>
	/// Helper for customizing the apperance of an edge in GraphViz
	/// </summary>
	public class GraphvizEdge
	{
		private String m_Url;
		private GraphvizArrow m_HeadArrow;
		private GraphvizArrow m_TailArrow;
		private Color m_StrokeColor;
		private String m_Comment;
		private bool m_IsConstrained;
		private bool m_IsDecorated;
		private GraphvizEdgeDirection m_Dir;
		private Font m_Font;
		private Color m_FontColor;
		private GraphvizEdgeExtremity m_Head;
		private GraphvizEdgeExtremity m_Tail;
		private GraphvizEdgeLabel m_Label;
		private GraphvizLayer m_Layer;
		private int m_MinLength;
		private GraphvizEdgeStyle m_Style;
		private String m_ToolTip;
		private double m_Weight;

		/// <summary>
		/// Default constructor
		/// </summary>
		public GraphvizEdge()
		{
			m_Url=null;
			m_HeadArrow=null;
			m_TailArrow=null;
			m_StrokeColor=Color.Black;
			m_Comment=null;
			m_IsConstrained=true;
			m_IsDecorated=false;
			m_Dir=GraphvizEdgeDirection.Forward;
			m_Font=null;
			m_FontColor=Color.Black;
			m_Head=new GraphvizEdgeExtremity(true);
			m_Tail=new GraphvizEdgeExtremity(false);
			m_Label=new GraphvizEdgeLabel();
			m_Layer=null;
			m_MinLength=1;
			m_Style=GraphvizEdgeStyle.Unspecified;
			m_ToolTip=null;
			m_Weight=1;
		}

		#region Properties

		/// <summary>
		/// See <see cref="GraphvizVertex.Url"/>.
		/// </summary>
		public String Url
		{
			get
			{
				return m_Url;
			}
			set
			{
				m_Url=value;
			}
		}

		/// <summary>
		/// Style of arrowhead on the head node of an edge.
		/// </summary>
		public GraphvizArrow HeadArrow
		{
			get
			{
				return m_HeadArrow;
			}
			set
			{
				m_HeadArrow=value;
			}
		}

		/// <summary>
		/// Style of arrowhead on the tail node of an edge.
		/// </summary>
		public GraphvizArrow TailArrow
		{
			get
			{
				return m_TailArrow;
			}
			set
			{
				m_TailArrow=value;
			}
		}

		/// <summary>
		/// Basic drawing color for graphics. 
		/// </summary>
		public Color StrokeColor
		{
			get
			{
				return m_StrokeColor;
			}
			set
			{
				m_StrokeColor=value;
			}
		}

		/// <summary>
		/// Comments are inserted into output. Device-dependent 
		/// </summary>
		public String Comment
		{
			get
			{
				return m_Comment;
			}
			set
			{
				m_Comment=value;
			}
		}

		/// <summary>
		/// See Graphviz doc.
		/// </summary>
		public bool IsConstrained
		{
			get
			{
				return m_IsConstrained;
			}
			set
			{
				m_IsConstrained=value;
			}
		}

		/// <summary>
		/// If true, attach edge label to edge by a 2-segment polyline, 
		/// underlining the label, then going to the closest point of spline. 
		/// </summary>
		public bool IsDecorated
		{
			get
			{
				return m_IsDecorated;
			}
			set
			{
				m_IsDecorated=value;
			}
		}

		/// <summary>
		/// Arrow direction
		/// </summary>
		public GraphvizEdgeDirection Dir
		{
			get
			{
				return m_Dir;
			}
			set
			{
				m_Dir=value;
			}
		}

		/// <summary>
		/// General font
		/// </summary>
		public Font Font
		{
			get
			{
				return m_Font;
			}
			set
			{
				m_Font=value;
			}
		}

		/// <summary>
		/// Text color
		/// </summary>
		public Color FontColor
		{
			get
			{
				return m_FontColor;
			}
			set
			{
				m_FontColor=value;
			}
		}

		/// <summary>
		/// Head description
		/// </summary>
		public GraphvizEdgeExtremity Head
		{
			get
			{
				return m_Head;
			}
			set
			{
				m_Head=value;
			}
		}

		/// <summary>
		/// Tail description
		/// </summary>
		public GraphvizEdgeExtremity Tail
		{
			get
			{
				return m_Tail;
			}
			set
			{
				m_Tail=value;
			}
		}

		/// <summary>
		/// Label description
		/// </summary>
		public GraphvizEdgeLabel Label
		{
			get
			{
				return m_Label;
			}
			set
			{
				m_Label=value;
			}
		}

		/// <summary>
		/// Specifies layers in which the edge is present. 
		/// </summary>
		public GraphvizLayer Layer
		{
			get
			{
				return m_Layer;
			}
			set
			{
				m_Layer=value;
			}
		}

		/// <summary>
		/// Minimum edge length (rank difference between head and tail). 
		/// </summary>
		public int MinLength
		{
			get
			{
				return m_MinLength;
			}
			set
			{
				m_MinLength=value;
			}
		}

		/// <summary>
		/// Set style foredge. For cluster subgraph, if "filled", 
		/// the cluster box's background is filled. 
		/// </summary>
		public GraphvizEdgeStyle Style
		{
			get
			{
				return m_Style;
			}
			set
			{
				m_Style=value;
			}
		}

		/// <summary>
		/// Tooltip annotation attached to the node or edge. 
		/// This is used only if the object has a URL attribute.
		/// </summary>
		public String ToolTip
		{
			get
			{
				return m_ToolTip;
			}
			set
			{
				m_ToolTip=value;
			}
		}

		/// <summary>
		/// Weight of edge. In dot, the heavier the weight, the shorter, 
		/// straighter and more vertical the edge is. 
		/// </summary>
		public double Weight
		{
			get
			{
				return m_Weight;
			}
			set
			{
				m_Weight=value;
			}
		}
		#endregion


		/// <summary>
		/// Returns dot code
		/// </summary>
		/// <returns></returns>
		public String ToDot()
		{
			Hashtable pairs = new Hashtable();

			if (this.Comment != null)
				pairs["comment"] = Comment;
			if (this.Dir != GraphvizEdgeDirection.Forward)
				pairs["dir"] = Dir.ToString().ToLower();
			if (this.Font != null)
			{
				pairs["fontname"]=Font.Name;
				pairs["fontsize"]=Font.SizeInPoints;
			}
			if (this.FontColor != Color.Black)
				pairs["fontcolor"]=FontColor;

			this.Head.AddParameters(pairs);
			if (HeadArrow!= null)
				pairs["arrowhead"]=HeadArrow.ToDot();
			if (!this.IsConstrained)
				pairs["constraint"]=IsConstrained;
			if (this.IsDecorated)
				pairs["decorate"]=IsDecorated;
			this.Label.AddParameters(pairs);
			if (this.Layer!=null)
				pairs["layer"]=Layer.Name;
			if (this.MinLength != 1)
				pairs["minlen"]=MinLength;
			if (this.StrokeColor != Color.Black)
				pairs["colors"]=StrokeColor;
			if (this.Style != GraphvizEdgeStyle.Unspecified)
				pairs["style"]=Style.ToString().ToLower();
			Tail.AddParameters(pairs);
			if (TailArrow != null)
				pairs["arrowtail"]=TailArrow.ToDot();
			if (this.ToolTip != null)
				pairs["tooltip"]=ToolTip;
			if (this.Url != null)
				pairs["URL"]=Url;
			if (this.Weight!=1)
				pairs["weight"]=Weight;

			return GenerateDot(pairs);
		}

		/// <summary>
		/// Returns to string
		/// </summary>
		/// <returns></returns>
		public override String ToString()
		{
			return ToDot();
		}	
		
		/// <summary>
		/// Converts a dictionary of parameters name-value to dot code.
		/// </summary>
		/// <param name="pairs">name-value pair collection</param>
		/// <returns>dot code</returns>
		internal String GenerateDot(Hashtable pairs)
		{
			bool needComa = false;

			StringWriter sw = new StringWriter();
			foreach(DictionaryEntry de in pairs)
			{
				// handling coma
				if (needComa)
					sw.Write(", ");
				else
					needComa=true;

				// handling string
				if (de.Value is String)
				{
					sw.Write("{0}=\"{1}\"",
						de.Key.ToString(),
						de.Value.ToString()
						);
				}
				else if (de.Value is GraphvizEdgeDirection)
				{
					sw.Write("{0}={1}",
						de.Key.ToString(),
						((GraphvizEdgeDirection)de.Value).ToString().ToLower()
						);
				}
				else if (de.Value is GraphvizEdgeStyle)
				{
					sw.Write("{0}={1}",
						de.Key.ToString(),
						((GraphvizEdgeStyle)de.Value).ToString().ToLower()
						);
				}
				else if (de.Value is Color)
				{
					Color c =(Color)de.Value;
					sw.Write("{0}=\"#{1}{2}{3}{4}\"",
						de.Key.ToString(),
						c.R.ToString("x2"),
						c.G.ToString("x2"),
						c.B.ToString("x2"),
						c.A.ToString("x2")
						);
				}
				else
				{
					sw.Write(" {0}={1}",
						de.Key.ToString(),
						de.Value.ToString().ToLower()
						);				
				}
			}

			return sw.ToString();
		}
					
	}
}
