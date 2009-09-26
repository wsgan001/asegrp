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

	/// <summary>
	/// A arrow shape
	/// </summary>
	public class GraphvizArrow
	{
		private GraphvizArrowShape m_Shape;
		private GraphvizArrowClipping m_Clipping;
		private GraphvizArrowFilling m_Filling;

		/// <summary>
		/// Builds a arrow.
		/// </summary>
		/// <param name="shape">Arrow shape</param>
		public GraphvizArrow(GraphvizArrowShape shape)
		{
			m_Shape = shape;
			m_Clipping = GraphvizArrowClipping.None;
			m_Filling = GraphvizArrowFilling.Close;
		}

		/// <summary>
		/// Builds an arrow
		/// </summary>
		/// <param name="shape">Arrow shape</param>
		/// <param name="clip">Clip option</param>
		/// <param name="fill">Fill option</param>
		public GraphvizArrow(
			GraphvizArrowShape shape,
			GraphvizArrowClipping clip,
			GraphvizArrowFilling fill
			)
		{
			m_Shape = shape;
			m_Clipping = clip;
			m_Filling = fill;
		}

		/// <summary>
		/// Arrow shape
		/// </summary>
		public GraphvizArrowShape Shape
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
		/// Clip option
		/// </summary>
		public GraphvizArrowClipping Clipping
		{
			get
			{
				return m_Clipping;
			}
			set
			{
				m_Clipping = value;
			}
		}

		/// <summary>
		/// Filling
		/// </summary>
		public GraphvizArrowFilling Filling
		{
			get
			{
				return m_Filling;
			}
			set
			{
				m_Filling = value;
			}
		}

		/// <summary>
		/// Converts arrow to dot code
		/// </summary>
		/// <returns>dot code</returns>
		public String ToDot()
		{
			StringWriter sw = new StringWriter();
			if (m_Filling == GraphvizArrowFilling.Open)
				sw.Write('o');
			switch(m_Clipping)
			{
				case  GraphvizArrowClipping.Left:
					sw.Write('l'); break;
				case  GraphvizArrowClipping.Right:
					sw.Write('r'); break;
			}				
			sw.Write(m_Shape.ToString().ToLower());
			return sw.ToString();
		}

		/// <summary>
		/// Converts arrow to string. Uses ToDot
		/// </summary>
		/// <returns>string representation of the arrow</returns>
		public override String ToString()
		{
			return ToDot();
		}
	}
}
