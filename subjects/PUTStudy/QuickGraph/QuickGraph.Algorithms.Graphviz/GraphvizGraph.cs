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
	using QuickGraph.Algorithms.Graphviz.Collections;
	using System.Collections;

	/// <summary>
	/// Description résumée de GraphvizGraph.
	/// </summary>
	public class GraphvizGraph
	{
		private String m_Url;
		private Color m_BackgroundColor;
		private bool m_IsCentered;
		private ClusterMode m_ClusterRank;
		private String m_Comment;
		private bool m_IsCompounded;
		private bool m_IsConcentrated;
		private Font m_Font;
		private Color m_FontColor;
		private String m_Label;
		private LabelJustification m_LabelJustification;
		private LabelLocation m_LabelLocation;
		private GraphvizLayerCollection m_Layers;
		private double m_McLimit;
		private double m_NodeSeparation;
		private bool m_IsNormalized;
		private int m_NsLimit;
		private int m_NsLimit1;
		private OutputMode m_OutputOrder;
		private Size m_PageSize;
		private PageDirection m_PageDirection;
		private double m_Quantum;
		private double m_RankSeparation;
		private RatioMode m_Ratio;
		private bool m_IsReMinCross;
		private double m_Resolution;
		private int m_Rotate;
		private int m_SamplePoints;
		private int m_SearchSize;
		private Size m_Size;
		private String m_StyleSheet;
	

		/// <summary>
		/// Default contructor
		/// </summary>
		public GraphvizGraph()
		{
			m_Url=null;
			m_BackgroundColor=Color.White;
			m_IsCentered=false;
			m_ClusterRank=ClusterMode.Local;
			m_Comment=null;
			m_IsCompounded=false;
			m_IsConcentrated=false;
			m_Font=null;
			m_FontColor=Color.Black;
			m_Label=null;
			m_LabelJustification=LabelJustification.C;
			m_LabelLocation=LabelLocation.B;
			m_Layers=new GraphvizLayerCollection();
			m_McLimit=1.0;
			m_NodeSeparation=0.25;
			m_IsNormalized=false;
			m_NsLimit=-1;
			m_NsLimit1=-1;
			m_OutputOrder=OutputMode.BreadthFirst;
			m_PageSize=new Size(0,0);
			m_PageDirection=PageDirection.BL;
			m_Quantum=0;
			m_RankSeparation=0.5;
			m_Ratio = RatioMode.Auto;
			m_IsReMinCross=false;
			m_Resolution=0.96;
			m_Rotate=0;
			m_SamplePoints=8;
			m_SearchSize=30;
			m_Size=new Size(0,0);
			m_StyleSheet=null;
		}		

		/// <summary>
		/// Converts to dot code
		/// </summary>
		/// <returns>dot code</returns>
		public String ToDot()
		{
			Hashtable pairs = new Hashtable();

			if (Url!=null)
				pairs["URL"]=Url;
			if (BackgroundColor!=Color.White)
				pairs["bgcolor"]=BackgroundColor;
			if (IsCentered)
				pairs["center"]=true;
			if (ClusterRank!=ClusterMode.Local)
				pairs["clusterrank"]=ClusterRank.ToString().ToLower();
			if (Comment!=null)
				pairs["comment"]=Comment;
			if (IsCompounded)
				pairs["compound"]=IsCompounded;
			if (IsConcentrated)
				pairs["concentrated"]=IsConcentrated;
			if (Font != null)
			{
				pairs["fontname"]=Font.Name;
				pairs["fontsize"]=Font.SizeInPoints;
			}
			if (FontColor != Color.Black)
				pairs["fontcolor"]=FontColor;
			if (Label!=null)
				pairs["label"]=Label;

			if (LabelJustification!=LabelJustification.C)
				pairs["labeljust"]=LabelJustification.ToString().ToLower();

			if (LabelLocation!=LabelLocation.B)
				pairs["labelloc"]=LabelLocation.ToString().ToLower();

			if (Layers.Count != 0)
				pairs["layers"]=Layers.ToDot();

			if (McLimit!=1.0)
				pairs["mclimit"]=McLimit;

			if (NodeSeparation!=0.25)
				pairs["nodesep"]=NodeSeparation;
			if (IsNormalized)
				pairs["normalize"]=IsNormalized;
			if (NsLimit>0)
				pairs["nslimit"]=NsLimit;
			if (NsLimit1>0)
				pairs["nslimit1"]=NsLimit1;

			if (OutputOrder!=OutputMode.BreadthFirst)
				pairs["outputorder"]=OutputOrder.ToString().ToLower();

			if (!PageSize.IsEmpty)
				pairs["page"]=String.Format("({0},{1})",PageSize.Width,PageSize.Height);
			if (PageDirection!=PageDirection.BL)
				pairs["pagedir"]=PageDirection.ToString().ToLower();
			if (Quantum>0)
				pairs["quantum"]=Quantum;
			if (RankSeparation!=0.5)
				pairs["ranksep"]=RankSeparation;
			if(Ratio != RatioMode.Auto)
				pairs["ratio"]=Ratio.ToString().ToLower();
			if(IsReMinCross)
				pairs["remincross"]=IsReMinCross;
			if(Resolution!=0.96)
				pairs["resolution"]=Resolution;
			if(Rotate!=0)
				pairs["rotate"]=Rotate;
			if(SamplePoints!=8)
				pairs["samplepoints"]=SamplePoints;
			if (SearchSize!=30)
				pairs["searchsize"]=SearchSize;
			if (!Size.IsEmpty)
				pairs["size"]=String.Format("({0},{1})",Size.Width,Size.Height);
			if (StyleSheet != null)
				pairs["stylesheet"]=StyleSheet;

			return GenerateDot(pairs);
		}


		/// <summary>
		/// Converts graph option to string. Uses ToDot
		/// </summary>
		/// <returns>string representation</returns>
		public override String ToString()
		{
			return ToDot();
		}

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


		#region Properties

		/// <summary>
		/// Hyperlinks incorporated into device-dependent output. 
		/// 
		/// At present, used in ps2, cmap, i*map and svg formats. 
		/// For all these formats, URLs can be attached to nodes, edges and 
		/// clusters. URL attributes can also be attached to the root graph in 
		/// ps2, cmap and i*map formats. This serves as the base URL for 
		/// relative URLs in the former, and as the default image map 
		/// file in the latter. 
		/// The active area for a node or cluster is its bounding box. 
		/// For edges, the active areas are small circles where the edge
		/// contacts its head and tail nodes. These areas may overlap the 
		/// related node, and the edge URL dominates. If the edge has a label, 
		/// this will also be active. Finally, if the edge has a head or tail 
		/// label, this will also be active. Note, however, that if the edge 
		/// has a headURL attribute, it is this value that is used near the 
		/// head node and on the head label, if defined. The similar 
		/// restriction holds when tailURL is defined. 
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
		/// This color is used as the background for entire canvas. 
		/// </summary>
		public Color BackgroundColor
		{
			get
			{
				return m_BackgroundColor;
			}
			set
			{
				m_BackgroundColor = value;
			}
		}

		/// <summary>
		/// If true, the drawing is centered in the output canvas.
		/// </summary>
		public bool IsCentered
		{
			get
			{
				return m_IsCentered;
			}
			set
			{
				m_IsCentered = value;
			}
		}

		/// <summary>
		/// Mode used for handling clusters. If clusterrank is "local", 
		/// a subgraph whose name begins with "cluster" is given special 
		/// treatment. The subgraph is laid out separately, and then 
		/// integrated as a unit into its parent graph, with a bounding 
		/// rectangle drawn about it. If the cluster has a label parameter, 
		/// this label is displayed within the rectangle. Note also that there 
		/// can be clusters within clusters. At present, the modes "global" 
		/// and "none" appear to be identical, both turning off the special 
		/// cluster processing. 
		/// </summary>
		public ClusterMode ClusterRank
		{
			get
			{
				return m_ClusterRank;
			}
			set
			{
				m_ClusterRank = value;
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
		/// If true, allow edges between clusters. See lhead and ltail 
		/// </summary>
		public bool IsCompounded
		{
			get
			{
				return m_IsCompounded;
			}
			set
			{
				m_IsCompounded = value;
			}
		}

		/// <summary>
		/// If true, use edge concentrators. 
		/// </summary>
		public bool IsConcentrated
		{
			get
			{
				return m_IsConcentrated;
			}
			set
			{
				m_IsConcentrated = value;
			}
		}

		/// <summary>
		/// Text font
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
				m_FontColor = value;
			}
		}


		/// <summary>
		/// Text label attached to objects.
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
		/// Justification for cluster labels. If "r", the label is
		/// right-justified within bounding rectangle;
		/// if "l", left-justified; else the label is centered. 
		/// Note that a subgraph inherits attributes from its parent. 
		/// 
		/// Thus, if the root graph sets labeljust to "l", the subgraph 
		/// inherits this value. 
		/// </summary>
		public LabelJustification LabelJustification
		{
			get
			{
				return m_LabelJustification;
			}
			set
			{
				m_LabelJustification = value;
			}
		}

		/// <summary>
		/// Top/bottom placement of graph and cluster labels. 
		/// 
		/// If the attribute is "t", place label at the top; if the attribute 
		/// is "b", place label at the bottom. By default, root graph labels 
		/// go on the bottom and cluster labels go on the top. 
		/// 
		/// Note that a subgraph inherits attributes from its parent.
		///  Thus, if the root graph sets labelloc to "b", the subgraph 
		///  inherits this value.
		/// </summary>
		public LabelLocation LabelLocation
		{
			get
			{
				return m_LabelLocation;
			}
			set
			{
				m_LabelLocation = value;
			}
		}

		/// <summary>
		/// Layer collection
		/// </summary>
		public GraphvizLayerCollection Layers
		{
			get
			{
				return m_Layers;
			}
			set
			{
				m_Layers = value;
			}
		}

		/// <summary>
		/// Multiplicative scale factor used to alter the MinQuit (default = 8) 
		/// and MaxIter (default = 24) parameters used during crossing 
		/// minimization. 
		/// 
		/// These correspond to the number of tries without improvement 
		/// before quitting and the maximum number of iterations in each pass. 
		/// </summary>
		public double McLimit
		{
			get
			{
				return m_McLimit;
			}
			set
			{
				m_McLimit = value;
			}
		}

		/// <summary>
		/// Minimum space between two adjacent nodes in the same rank, 
		/// in inches. 
		/// </summary>
		public double NodeSeparation
		{
			get
			{
				return m_NodeSeparation;
			}
			set
			{
				m_NodeSeparation = value;
			}
		}

		/// <summary>
		/// If set, normalize coordinates of final layout so that the first 
		/// point is at the origin, and then rotate the layout so that 
		/// the first edge is horizontal.
		/// </summary>
		public bool IsNormalized
		{
			get
			{
				return m_IsNormalized;
			}
			set
			{
				m_IsNormalized = value;
			}
		}

		/// <summary>
		/// Used to set number of iterations in network simplex applications. 
		/// nslimit is used in computing node x coordinates, nslimit1 for 
		/// ranking nodes. 
		/// If defined, # iterations = nslimit(1) * # nodes; 
		/// otherwise, # iterations = MAXINT. 
		/// </summary>
		public int NsLimit
		{
			get
			{
				return m_NsLimit;
			}
			set
			{
				m_NsLimit = value;
			}
		}

		/// <summary>
		/// Used to set number of iterations in network simplex applications. 
		/// nslimit is used in computing node x coordinates, nslimit1 for 
		/// ranking nodes. 
		/// If defined, # iterations = nslimit(1) * # nodes; 
		/// otherwise, # iterations = MAXINT. 
		/// </summary>
		public int NsLimit1
		{
			get
			{
				return m_NsLimit1;
			}
			set
			{
				m_NsLimit1 = value;
			}
		}

		/// <summary>
		/// Specify order in which nodes and edges are drawn. 
		/// </summary>
		public OutputMode OutputOrder
		{
			get
			{
				return m_OutputOrder;
			}
			set
			{
				m_OutputOrder = value;
			}
		}

		/// <summary>
		/// Width and height of output pages, in inches. 
		/// If this is set and is smaller than the size of the layout, 
		/// a rectangular array of pages of the specified page size is 
		/// overlaid on the layout, with origins aligned in the lower-left 
		/// corner, thereby partitioning the layout into pages. 
		/// 
		/// The pages are then produced one at a time, in pagedir order. 
		/// </summary>
		public Size PageSize
		{
			get
			{
				return m_PageSize;
			}
			set
			{
				m_PageSize = value;
			}
		}

		/// <summary>
		/// If the page attribute is set and applicable, 
		/// this attribute specifies the order in which the pages are emitted. 
		/// This is limited to one of the 8 row or column major orders. 
		/// </summary>
		public PageDirection PageDirection
		{
			get
			{
				return m_PageDirection;
			}
			set
			{
				m_PageDirection = value;
			}
		}

		/// <summary>
		/// If quantum > 0.0, node label dimensions will be rounded to 
		/// integral multiples of the quantum. 
		/// </summary>
		public double Quantum
		{
			get
			{
				return m_Quantum;
			}
			set
			{
				m_Quantum = value;
			}
		}

		/// <summary>
		/// In dot, this the gives desired rank separation, in inches. 
		/// 
		/// This is the minimum vertical distance between the bottom of the 
		/// nodes in one rank and the tops of nodes in the next. If the value 
		/// contains "equally", the centers of all ranks are spaced equally 
		/// apart. 
		/// 
		/// Note that both settings are possible, e.g., 
		/// ranksep = "1.2 equally". 
		/// </summary>
		public double RankSeparation
		{
			get
			{
				return m_RankSeparation;
			}
			set
			{
				m_RankSeparation = value;
			}
		}

		/// <summary>
		/// Sets the ratio mode
		/// </summary>
		public RatioMode Ratio
		{
			get
			{
				return m_Ratio;
			}
			set
			{
				m_Ratio = value;
			}
		}

		/// <summary>
		/// If true and there are multiple clusters, run cross minimization 
		/// a second time.
		/// </summary>
		public bool IsReMinCross
		{
			get
			{
				return m_IsReMinCross;
			}
			set
			{
				m_IsReMinCross = value;
			}
		}

		/// <summary>
		/// This specifies the expected number of pixels per inch on a display. 
		/// 
		/// It is used to guarantee that dimensions in the output correspond 
		/// to the correct number of points or inches
		/// </summary>
		public double Resolution
		{
			get
			{
				return m_Resolution;
			}
			set
			{
				m_Resolution = value;
			}
		}

		/// <summary>
		/// Canvas roation in degrees. If 90, set drawing orientation to landscape. 
		/// </summary>
		public int Rotate
		{
			get
			{
				return m_Rotate;
			}
			set
			{
				m_Rotate = value;
			}
		}

		/// <summary>
		/// Tthis give the number of points used to represent circles and 
		/// ellipses. 
		/// </summary>
		public int SamplePoints
		{
			get
			{
				return m_SamplePoints;
			}
			set
			{
				m_SamplePoints = value;
			}
		}

		/// <summary>
		/// During network simplex, maximum number of edges with negative 
		/// cut values to search when looking for one with minimum cut value. 
		/// </summary>
		public int SearchSize
		{
			get
			{
				return m_SearchSize;
			}
			set
			{
				m_SearchSize = value;
			}
		}

		/// <summary>
		/// Maximum width and height of drawing, inches. 
		/// If defined and the drawing is too large, the drawing is uniformly 
		/// scaled down so that it fits within the given size. 
		/// Note that there is some interaction between the size and ratio 
		/// attributes. 
		/// </summary>
		public Size Size
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
		/// A URL or pathname specifying an XML style sheet, used in SVG output. 
		/// </summary>
		public String StyleSheet
		{
			get
			{
				return m_StyleSheet;
			}
			set
			{
				m_StyleSheet = value;
			}
		}

		#endregion 
	}
}
