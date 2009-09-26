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
	using System.Collections;

	/// <summary>
	/// Description résumée de GraphvizEdgeExtremity.
	/// </summary>
	public class GraphvizEdgeExtremity
	{
		private String m_Url;
		private bool m_IsClipped;
		private String m_Label;
		private String m_ToolTip;
		private String m_Logical;
		private String m_Same;
		private bool m_IsHead;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="isHead"></param>
		public GraphvizEdgeExtremity(bool isHead)
		{
			m_IsHead = isHead;

			m_Url=null;
			m_IsClipped = true;
			m_Label=null;
			m_ToolTip=null;
			m_Logical=null;
			m_Same=null;
		}

		/// <summary>
		/// True if is head
		/// </summary>
		public bool IsHead
		{
			get
			{
				return m_IsHead;
			}
		}

		/// <summary>
		/// For the output format imap or cmap, if Url is defined, 
		/// it is output as part of the extremity label of the edge.
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
		/// If true, the extremity of an edge is clipped to the boundary of the extremity 
		/// node; otherwise, the end of the edge goes to the center of the 
		/// node, or the center of a port, if applicable.
		/// </summary>
		public bool IsClipped
		{
			get
			{
				return m_IsClipped;
			}
			set
			{
				m_IsClipped=value;
			}
		}

		/// <summary>
		/// Text label to be placed near extremity of edge. 
		/// </summary>
		public String Label
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
		/// Tooltip annotation attached to the extremity of an edge. 
		/// This is used only if the edge has a Url attribute. 
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
		/// Logical extremity of an edge. When compound is true, if Logical is 
		/// defined and is the name of a cluster containing the real extremity, 
		/// the edge is clipped to the boundary of the cluster.
		/// </summary>
		public String Logical
		{
			get
			{
				return m_Logical;
			}
			set
			{
				m_Logical=value;
			}
		}

		/// <summary>
		/// Edges with the same extremity and the same samehead value are 
		/// aimed at the same point on the head. 
		/// </summary>
		public String Same
		{
			get
			{
				return m_Same;
			}
			set
			{
				m_Same=value;
			}
		}



		/// <summary>
		/// Adds the parameters relative to the extremity
		/// </summary>
		/// <param name="dic"></param>
		public void AddParameters(IDictionary dic)
		{
			if (dic == null)
				throw new ArgumentNullException("dic");

			String suf = null;
			if (IsHead)
				suf = "head";
			else
				suf = "tail";

			if (Url != null)
				dic.Add(suf+"URL",Url);
			if (!IsClipped)
				dic.Add(suf+"clip",IsClipped);
			if (Label != null)
				dic.Add(suf+"label",Label);
			if (ToolTip != null)
				dic.Add(suf+"tooltip",ToolTip);
			if (Logical != null)
				dic.Add("l" + suf , Logical);
			if (Same != null)
				dic.Add("same" + suf, Same);
		}
	}
}
