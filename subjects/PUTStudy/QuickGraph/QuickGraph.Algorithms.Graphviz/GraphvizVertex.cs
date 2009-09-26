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
	using System.Collections;
	using System.Drawing;

	/// <summary>
	/// A helper class for customizing Graphviz vertex appearance.
	/// </summary>
	public class GraphvizVertex
	{
		private Font m_Font;
		private GraphvizVertexShape m_Shape;
		private bool m_FixedSize;
		private SizeF m_Size;
		private String m_Label;
		private GraphvizVertexStyle m_Style;
		private bool m_Regular;
		private String m_Url;
		private String m_ToolTip;
		private Color m_StrokeColor;
		private Color m_FillColor;
		private Color m_FontColor;
		private String m_Comment;
		private String m_Group;
		private GraphvizLayer m_Layer;
		private double m_Orientation;
		private int m_Peripheries;
		private double m_Z;

		private int m_Sides;
		private double m_Skew;
		private double m_Distorsion;

		private String m_TopLabel;
		private String m_BottomLabel;

		/// <summary>
		/// Default constructor
		/// </summary>
		public GraphvizVertex()
		{
			m_Font = null;
			m_Shape = GraphvizVertexShape.Unspecified;
			m_FixedSize=false;
			m_Size=new SizeF(0,0);
			m_Label = null;
			m_Style = GraphvizVertexStyle.Unspecified;
			m_Regular = false;
			m_Url = null;
			m_ToolTip = null;
			m_StrokeColor = Color.Black;
			m_FillColor = Color.White;
			m_FontColor = Color.Black;
			m_Comment = null;
			m_Group = null;
			m_Layer = null;
			m_Orientation = 0;
			m_Peripheries = -1;
			m_Z = -1;

			m_Sides = 4;
			m_Skew = 0.0;
			m_Distorsion = 0.0;

			m_TopLabel = null;
			m_BottomLabel = null;
		}


		#region Properties

		/// <summary>
		/// Vertex font
		/// </summary>
		public Font Font
		{
			get
			{ 
				return m_Font;
			}
			set
			{ 
				m_Font = value;
			}
		}

		/// <summary>
		/// Vertex shape.
		/// </summary>
		public GraphvizVertexShape Shape
		{
			get
			{ 
				return m_Shape;
			}
			set
			{ 
				m_Shape = value;
			}
		}

		/// <summary>
		/// If set to true, the vertex size is fixed to Size.
		/// </summary>
		public bool FixedSize		
		{
			get
			{ 
				return m_FixedSize;
			}
			set
			{ 
				m_FixedSize = value;
			}
		}

		/// <summary>
		/// Vertex size (enabled if FixedSize is true).
		/// </summary>
		public SizeF Size		
		{
			get
			{ 
				return m_Size;
			}
			set
			{ 
				m_Size = value;
			}
		}

		/// <summary>
		/// Vertex Label
		/// </summary>
		public String Label		
		{
			get
			{ 
				return m_Label;
			}
			set
			{ 
				m_Label = value;
			}
		}

		/// <summary>
		/// Vertex label
		/// </summary>
		public GraphvizVertexStyle Style		
		{
			get
			{ 
				return m_Style;
			}
			set
			{ 
				m_Style = value;
			}
		}

		/// <summary>
		/// If true, force polygon to be regular. 
		/// </summary>
		public bool Regular		
		{
			get
			{ 
				return m_Regular;
			}
			set
			{ 
				m_Regular = value;
			}
		}

		/// <summary>
		/// <para>
		/// Hyperlinks incorporated into device-dependent output. 
		/// At present, used in ps2, cmap, i*map and svg formats. 
		/// For all these formats, URLs can be attached to nodes, edges and 
		/// clusters. 
		/// </para>
		/// <para>
		/// URL attributes can also be attached to the root graph in 
		/// ps2, cmap and i*map formats. This serves as the base URL for 
		/// relative URLs in the former, and as the default image map file in 
		/// the latter.
		/// </para>
		/// <para> 
		/// The active area for a node or cluster is its bounding box. For 
		/// edges, the active areas are small circles where the edge contacts 
		/// its head and tail nodes. These areas may overlap the related node, 
		/// and the edge URL dominates. If the edge has a label, this will also 
		/// be active. 
		/// </para>
		/// <para>
		/// Finally, if the edge has a head or tail label, this will also be 
		/// active. Note, however, that if the edge has a headURL attribute, 
		/// it is this value that is used near the head node and on the head 
		/// label, if defined. The similar restriction holds when tailURL is 
		/// defined. 
		/// </para>
		/// </summary>
		public String Url		
		{
			get
			{ 
				return m_Url;
			}
			set
			{ 
				m_Url = value;
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
				m_ToolTip = value;
			}
		}

		/// <summary>
		/// Basic drawing color for graphics. 
		/// Ignored if color = Color.Transparent
		/// </summary>
		public Color StrokeColor		
		{
			get
			{ 
				return m_StrokeColor;
			}
			set
			{ 
				m_StrokeColor = value;
			}
		}

		/// <summary>
		/// <para>
		/// Color used to fill the background of a node or cluster. 
		/// </para>
		/// <para>
		/// If fillcolor is not defined, color is used. (For clusters, if color 
		/// is not defined, bgcolor is used.) If this is not defined, 
		/// the default is used, except for shape=point or when the output 
		/// format is MIF, which use black by default. 
		/// Note that a cluster inherits the root graph's attributes if 
		/// defined. Thus, if the root graph has defined a fillcolor, this 
		/// will override a color or bgcolor attribute set for the cluster.
		/// </para>
		/// <para>
		/// Ignored if color = Color.Transparent
		/// </para>
		/// </summary>
		public Color FillColor		
		{
			get
			{ 
				return m_FillColor;
			}
			set
			{ 
				m_FillColor = value;
			}
		}

		/// <summary>
		/// <para>
		/// Color used for text. 
		/// </para>
		/// <para>
		/// Ignored if color = Color.Transparent
		/// </para>
		/// </summary>
		public Color FontColor		
		{
			get
			{ 
				return m_FontColor;
			}
			set
			{ 
				m_FontColor = value;
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
				m_Comment = value;
			}
		}

		/// <summary>
		/// If the end points of an edge belong to the same group, i.e., 
		/// have the same group attribute, parameters are set to avoid 
		/// crossings and keep the edges straight. 
		/// </summary>
		public String Group		
		{
			get
			{ 
				return m_Group;
			}
			set
			{ 
				m_Group = value;
			}
		}

		/// <summary>
		/// Specifies layers in which the node or edge is present.
		/// </summary>
		public GraphvizLayer Layer		
		{
			get
			{ 
				return m_Layer;
			}
			set
			{ 
				m_Layer = value;
			}
		}

		/// <summary>
		/// Angle, in degrees, used to rotate node shapes. 
		/// </summary>
		public double Orientation		
		{
			get
			{ 
				return m_Orientation;
			}
			set
			{ 
				m_Orientation = value;
			}
		}

		/// <summary>
		/// <para>
		/// Set number of peripheries used in polygonal shapes and cluster 
		/// boundaries.
		/// </para>
		/// <para> 
		/// Default peripheries value is 1 and the user-defined shape will be 
		/// drawn in a bounding rectangle. 
		/// Setting peripheries=0 will turn this off. 
		/// </para>
		/// <para>
		/// Also, 1 is the maximum peripheries value for clusters.
		/// </para>
		/// </summary>
		public int Peripheries		
		{
			get
			{ 
				return m_Peripheries;
			}
			set
			{ 
				m_Peripheries = value;
			}
		}

		/// <summary>
		/// Provides z coordinate for the node when output format is VRML. 
		/// </summary>
		public double Z		
		{
			get
			{ 
				return m_Z;
			}
			set
			{ 
				m_Z = value;
			}
		}

		/// <summary>
		/// Number of sides if shape=polygon. 
		/// </summary>
		public int Sides		
		{
			get
			{ 
				return m_Sides;
			}
			set
			{ 
				m_Sides = value;
			}
		}

		/// <summary>
		/// Skew factor for shape=polygon. 
		/// Positive values skew top of polygon to right; negative to left.
		/// </summary>
		public double Skew		
		{
			get
			{ 
				return m_Skew;
			}
			set
			{ 
				m_Skew = value;
			}
		}

		/// <summary>
		/// Distortion factor for shape=polygon. 
		/// Positive values cause top part to be larger than bottom; 
		/// negative values do the opposite. 
		/// </summary>
		public double Distorsion		
		{
			get
			{ 
				return m_Distorsion;
			}
			set
			{ 
				m_Distorsion = value;
			}
		}

		/// <summary>
		/// Additional label near top of nodes of shape M*. 
		/// </summary>
		public String TopLabel		
		{
			get
			{ 
				return m_TopLabel;
			}
			set
			{ 
				m_TopLabel = value;
			}
		}

		/// <summary>
		/// Additional label near bottom of nodes of shape M*.
		/// </summary>
		public String BottomLabel		
		{
			get
			{ 
				return m_BottomLabel;
			}
			set
			{ 
				m_BottomLabel = value;
			}
		}

		#endregion 

		/// <summary>
		/// Generates dot code that represents the vertex appearance settings.
		/// </summary>
		/// <returns>dot code</returns>
		public String ToDot()
		{
			Hashtable pairs = new Hashtable();


			// font
			if (Font != null)
			{
				pairs["fontname"]=Font.Name;
				pairs["fontsize"]=Font.SizeInPoints;
			}
			if (FontColor != Color.Black)
			pairs["fontcolor"]=FontColor;

			// shape
			if (Shape != GraphvizVertexShape.Unspecified)
				pairs["shape"]=Shape;
			if (Style != GraphvizVertexStyle.Unspecified)
				pairs["style"]=Style;
			
			// fixed size
			if (FixedSize)
			{
				pairs["fixedsize"]=true;

				// size
				if (Size.Height > 0)
					pairs["height"]=Size.Height;
				if (Size.Width > 0)
					pairs["width"]=Size.Width;
			}

			// colors
			pairs["color"]=StrokeColor;
			pairs["fillcolor"]=FillColor;

			// label
			if (Label != null)
				pairs["label"]=Label;

			// regular
			if (Regular)
				pairs["regular"]=Regular;

			// url, tooltip
			if (Url != null)
				pairs["URL"]=Url;
			if (ToolTip != null)
				pairs["tooltip"]=ToolTip;

			if (Comment != null)
				pairs["comment"]=Comment;
			if (Group != null)
				pairs["group"]=Group;
			if (Layer != null)
				pairs["layer"]=Layer.Name;
			if (Orientation > 0)
				pairs["orientation"]=Orientation;
			if (Peripheries >= 0)
				pairs["peripheries"]=Peripheries;
			if (Z > 0)
				pairs["z"]=Z;

			if (Style == GraphvizVertexStyle.Diagonals
				|| Shape == GraphvizVertexShape.MCircle
				|| Shape == GraphvizVertexShape.MDiamond
				|| Shape == GraphvizVertexShape.MSquare
				)
			{
				if (TopLabel != null)
					pairs["toplabel"]=TopLabel;
				if (BottomLabel != null)
					pairs["bottomlable"]=BottomLabel;
			}

			if (Shape == GraphvizVertexShape.Polygon)
			{
				if (Sides != 0)
					pairs["sides"]=Sides;
				if (Skew != 0)
					pairs["skew"]=Skew;
				if (Distorsion != 0)
					pairs["distorsion"]=Distorsion;
			}

			return GenerateDot(pairs);
		}

		/// <summary>
		/// Returns ToDot()
		/// </summary>
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
				else if (de.Value is GraphvizVertexShape)
				{
					sw.Write("{0}={1}",
						de.Key.ToString(),
						((GraphvizVertexShape)de.Value).ToString().ToLower()
						);
				}
				else if (de.Value is GraphvizVertexStyle)
				{
					sw.Write("{0}={1}",
						de.Key.ToString(),
						((GraphvizVertexStyle)de.Value).ToString().ToLower()
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
