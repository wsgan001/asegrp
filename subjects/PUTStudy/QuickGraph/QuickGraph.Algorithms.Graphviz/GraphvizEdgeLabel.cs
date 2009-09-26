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

	/// <summary>
	/// An edge label
	/// </summary>
	public class GraphvizEdgeLabel
	{
		private double m_Angle;
		private double m_Distance;
		private bool m_Float;
		private Color m_FontColor;
		private Font m_Font;
		private String m_Value;

		/// <summary>
		/// Default constructor
		/// </summary>
		public GraphvizEdgeLabel()
		{
			m_Angle = -25.0;
			m_Distance = 1.0;
			m_Float = true;
			m_FontColor = Color.Black;
			m_Font = null;
			m_Value = null;
		}

		/// <summary>
		/// Adds parameters to the list
		/// </summary>
		/// <param name="dic">name-value map</param>
		public void AddParameters(IDictionary dic)
		{
			if (Value == null)
				return;
			dic["label"]=Value;

			if (Angle != -25)
				dic["labelangle"] = Angle;
			if (Distance != 1)
				dic["labeldistance"]=Distance;
			if (!Float)
				dic["labelfloat"]=Float;
			if (Font != null)
			{
				dic["labelfontname"]=Font.Name;
				dic["labelfontsize"]=Font.SizeInPoints;
			}
		}

		#region Properties

		/// <summary>
		/// Label angle
		/// </summary>
		public double Angle
		{
			get
			{
				return m_Angle;
			}
			set
			{
				m_Angle = value;
			}
		}

		/// <summary>
		/// Lable distance
		/// </summary>
		public double Distance
		{
			get
			{
				return m_Distance;
			}
			set
			{
				m_Distance = value;
			}
		}

		/// <summary>
		/// If true, label is floating
		/// </summary>
		public bool Float
		{
			get
			{
				return m_Float;
			}
			set
			{
				m_Float = value;
			}
		}

		/// <summary>
		/// Label font color
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
		/// Label font
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
		/// Label text
		/// </summary>
		public String Value
		{
			get
			{
				return m_Value;
			}
			set
			{
				m_Value = value;
			}
		}

		#endregion

	}
}
