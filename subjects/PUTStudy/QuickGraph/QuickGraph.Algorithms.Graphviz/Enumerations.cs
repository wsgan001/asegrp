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

namespace QuickGraph.Algorithms.Graphviz
{
	/// <summary>
	/// Possigle vertex shape in the dot language
	/// </summary>
	public enum GraphvizVertexShape
	{
		/// <summary>
		/// Shape not specified
		/// </summary>
		Unspecified,
		/// <summary>
		/// Box shape
		/// </summary>
		Box,
		/// <summary>
		/// Polygon, used with Sides, Skew and distorsion
		/// </summary>
		Polygon,
		/// <summary>
		/// Elipse shpae
		/// </summary>
		Ellipse,
		/// <summary>
		/// Circle shape
		/// </summary>
		Circle,
		/// <summary>
		/// Point
		/// </summary>
		Point,
		/// <summary>
		/// Egg shpae
		/// </summary>
		Egg,
		/// <summary>
		/// Triangle shape
		/// </summary>
		Triangle,
		/// <summary>
		/// Plain text
		/// </summary>
		Plaintext,
		/// <summary>
		/// Diamond shape
		/// </summary>
		Diamond,
		/// <summary>
		/// Trapezion shape
		/// </summary>
		Trapezium,
		/// <summary>
		/// Parallelogram
		/// </summary>
		Parallelogram,
		/// <summary>
		/// House shape
		/// </summary>
		House,
		/// <summary>
		/// Pentagon
		/// </summary>
		Pentagon,
		/// <summary>
		/// Hexagon
		/// </summary>
		Hexagon,
		/// <summary>
		/// Septagon
		/// </summary>
		Septagon,
		/// <summary>
		/// Octagon
		/// </summary>
		Octagon,
		/// <summary>
		/// DoubleCircle
		/// </summary>
		DoubleCircle,
		/// <summary>
		/// DoubleOctagon
		/// </summary>
		DoubleOctagon,
		/// <summary>
		/// TripleOctagon
		/// </summary>
		TripleOctagon,
		/// <summary>
		/// InvTriangle
		/// </summary>
		InvTriangle,
		/// <summary>
		/// InvTrapezium
		/// </summary>
		InvTrapezium,
		/// <summary>
		/// InvHouse
		/// </summary>
		InvHouse,
		/// <summary>
		/// MiddleDiamond
		/// </summary>
		MDiamond,
		/// <summary>
		/// MiddleSquare
		/// </summary>
		MSquare,
		/// <summary>
		/// MiddleCircle
		/// </summary>
		MCircle,
		/// <summary>
		/// Rect
		/// </summary>
		Rect,
		/// <summary>
		/// Rectangle
		/// </summary>
		Rectangle,
		/// <summary>
		/// Record
		/// </summary>
		Record
	}

	/// <summary>
	/// Vertex styles in graphviz format
	/// </summary>
	public enum GraphvizVertexStyle
	{
		/// <summary>
		/// Not specified, ignored
		/// </summary>
		Unspecified,
		/// <summary>
		/// Filled shape. Uses the FillColor
		/// </summary>
		Filled,
		/// <summary>
		/// Adds diagonals. Show the TopLabel and BottomLabel.
		/// </summary>
		Diagonals,
		/// <summary>
		/// Rounded
		/// </summary>
		Rounded,
		/// <summary>
		/// Invisible shpae
		/// </summary>
		Invis,
		/// <summary>
		/// Dashed line
		/// </summary>
		Dashed,
		/// <summary>
		/// Dotted line
		/// </summary>
		Dotted,
		/// <summary>
		/// Bold line
		/// </summary>
		Bold,
		/// <summary>
		/// Solid edge
		/// </summary>
		Solid
	}

	/// <summary>
	/// Edge style
	/// </summary>
	public enum GraphvizEdgeStyle
	{
		/// <summary>
		/// Not specified, ignored
		/// </summary>
		Unspecified,
		/// <summary>
		/// Invisible shpae
		/// </summary>
		Invis,
		/// <summary>
		/// Dashed line
		/// </summary>
		Dashed,
		/// <summary>
		/// Dotted line
		/// </summary>
		Dotted,
		/// <summary>
		/// Bold line
		/// </summary>
		Bold,
		/// <summary>
		/// Solid edge
		/// </summary>
		Solid
	}

	/// <summary>
	/// Arrow shape enumeration
	/// </summary>
	public enum	GraphvizArrowShape
	{
		/// <summary>
		/// Box arrow
		/// </summary>
		Box,
		/// <summary>
		/// Crow arrow
		/// </summary>
		Crow,
		/// <summary>
		/// Diamond arrow
		/// </summary>
		Diamond,
		/// <summary>
		/// Dot arrow
		/// </summary>
		Dot,
		/// <summary>
		/// Inv
		/// </summary>
		Inv,
		/// <summary>
		/// No arrow
		/// </summary>
		None,   
		/// <summary>
		/// Normal arrow
		/// </summary>
		Normal,
		/// <summary>
		/// Tee
		/// </summary>
		Tee,
		/// <summary>
		/// Vee
		/// </summary>
		Vee   
	}


	/// <summary>
	/// Arrow clipping
	/// </summary>
	public enum GraphvizArrowClipping
	{
		/// <summary>
		/// No clipping
		/// </summary>
		None,
		/// <summary>
		/// Clip the shape, leaving only the part to the left of the edge. 
		/// </summary>
		Left,
		/// <summary>
		/// Clip the shape, leaving only the part to the right of the edge. 
		/// </summary>
		Right
	}

	/// <summary>
	/// Arrow fille
	/// </summary>
	public enum GraphvizArrowFilling
	{
		/// <summary>
		/// Close, filled shaped
		/// </summary>
		Close,
		/// <summary>
		/// Opened non-fille shape
		/// </summary>
		Open
	}

	/// <summary>
	/// Enumeration that specifies where the arrow head is drawed
	/// </summary>
	public enum GraphvizEdgeDirection
	{
		/// <summary>
		/// No arrow head
		/// </summary>
		None,
		/// <summary>
		/// forward position
		/// </summary>
		Forward,
		/// <summary>
		/// Back position
		/// </summary>
		Back,
		/// <summary>
		/// forward and back
		/// </summary>
		Both
	}

	/// <summary>
	/// Supported output image file types
	/// </summary>
	public enum GraphvizImageType
	{
		/// <summary>
		/// Fig
		/// </summary>
		Fig, 
		/// <summary>
		/// Gd format
		/// </summary>
		Gd,
		/// <summary>
		/// GD2 format
		/// </summary>
		Gd2,   
		/// <summary>
		/// Famous gif
		/// </summary>
		Gif,
		/// <summary>
		/// HP-GL/2
		/// </summary>
		Hpgl,
		/// <summary>
		/// Server-side imagemaps
		/// </summary>
		Imap, 
		/// <summary>
		/// Client-side imagemaps
		/// </summary>
		Cmap, 
		/// <summary>
		/// JPEG
		/// </summary>
		Jpeg,
		/// <summary>
		/// FrameMaker MIF format
		/// </summary>
		Mif,
		/// <summary>
		/// MetaPost
		/// </summary>
		Mp,
		/// <summary>
		/// PCL
		/// </summary>
		Pcl,
		/// <summary>
		/// PIC
		/// </summary>
		Pic,
		/// <summary>
		/// Simple text format
		/// </summary>
		PlainText,
		/// <summary>
		/// Portable Network Graphics format
		/// </summary>
		Png,
		/// <summary>
		/// PostScript
		/// </summary>
		Ps,  
		/// <summary>
		/// PostScript for PDF
		/// </summary>		
		Ps2,
		/// <summary>
		/// Scalable Vector Graphics
		/// </summary>
		Svg,
		/// <summary>
		/// Scalable Vector Graphics, gzipped
		/// </summary>
		Svgz,
		/// <summary>
		/// VRML
		/// </summary>
		Vrml,
		/// <summary>
		/// Visual Thought format
		/// </summary>
		Vtx,
		/// <summary>
		/// Wireless BitMap format
		/// </summary>
		Wbmp   
	}

	/// <summary>
	/// Mode used for handling clusters.
	/// If clusterrank is "local", 
	/// a subgraph whose name begins with "cluster" is given special treatment. 
	/// The subgraph is laid out separately, and then integrated as a unit into 
	/// its parent graph, with a bounding rectangle drawn about it. 
	/// 
	/// If the cluster has a label parameter, this label is displayed within 
	/// the rectangle. 
	/// Note also that there can be clusters within clusters. At present, 
	/// the modes "global" and "none" 
	/// appear to be identical, both turning off the special cluster processing. 
	/// color 
	/// </summary>
	public enum ClusterMode
	{
		/// <summary>
		/// 
		/// </summary>
		Local,
		/// <summary>
		/// 
		/// </summary>
		Global,
		/// <summary>
		/// 
		/// </summary>
		None
	}

	/// <summary>
	/// Justification for cluster labels.
	/// </summary>
	public enum LabelJustification
	{
		/// <summary>
		/// Left-justificed
		/// </summary>
		L,
		/// <summary>
		/// Right-justified
		/// </summary>
		R,
		/// <summary>
		/// Centered
		/// </summary>
		C
	}

	/// <summary>
	/// Top/bottom placement of graph and cluster labels. 
	/// </summary>
	public enum LabelLocation
	{
		/// <summary>
		/// Labels at top
		/// </summary>
		T,
		/// <summary>
		/// Bottom placement
		/// </summary>
		B
	}

	/// <summary>
	/// Specify the order in which nodes and edges are drawn in concrete output.
	/// </summary>
	public enum OutputMode
	{
		/// <summary>
		/// The default BreadthFirst is the simplest, but when the graph 
		/// layout does not avoid edge-node overlap, this mode will sometimes 
		/// have edges drawn over nodes and sometimes on top of nodes.
		/// </summary>
		BreadthFirst,
		/// <summary>
		/// If the mode NodesFirst is chosen, all nodes are drawn first, 
		/// followed by the edges. This guarantees an edge-node overlap will 
		/// not be mistaken for an edge ending at a node.
		/// </summary>
		NodesFirst,
		/// <summary>
		/// Usually for aesthetic reasons, it may be desirable that all edges 
		/// appear beneath nodes, even if the resulting drawing is ambiguous. 
		/// This can be achieved by choosing EdgesFirst. 
		/// </summary>
		EdgesFirst
	}

	/// <summary>
	/// These specify the 8 row or column major orders for traversing a 
	/// rectangular array, the first character corresponding to the major 
	/// order and the second to the minor order. Thus, for "BL", the major 
	/// order is from bottom to top, and the minor order is from left to right. 
	/// 
	/// This means the bottom row is traversed first, from left to right, 
	/// then the next row up, from left to right, and so on, 
	/// until the topmost row is traversed. 
	/// </summary>
	public enum PageDirection
	{
		/// <summary>
		/// 
		/// </summary>
		BL,
		/// <summary>
		/// 
		/// </summary>
		BR,
		/// <summary>
		/// 
		/// </summary>
		TL, 
		/// <summary>
		/// 
		/// </summary>
		TR, 
		/// <summary>
		/// 
		/// </summary>
		RB, 
		/// <summary>
		/// 
		/// </summary>
		RT, 
		/// <summary>
		/// 
		/// </summary>
		LB, 
		/// <summary>
		/// 
		/// </summary>
		LT
	}

	/// <summary>
	/// Aspect ratio (drawing height/drawing width) for the drawing.
	/// </summary>
	public enum RatioMode
	{
		/// <summary>
		/// Vertex positions are scaled, separately in both x and y, so that 
		/// the final drawing exactly fills the specified size. 
		/// </summary>
		Fill,
		/// <summary>
		/// Dot attempts to compress the initial layout to fit in the given 
		/// size. This achieves a tighter packing of nodes but reduces the 
		/// balance and symmetry. This feature only works in dot. 
		/// </summary>
		Compress,
		/// <summary>
		/// If ratio = "auto", the page attribute is set and the graph cannot 
		/// be drawn on a single 
		/// page, then size is set to an ``ideal'' value. In particular, the 
		/// size in a given dimension will be the smallest integral multiple 
		/// of the page size in that dimension which is at least half the
		/// current size. The two dimensions are then scaled independently to 
		/// the new size. This feature only works in dot. 
		/// </summary>
		Auto
	}
}