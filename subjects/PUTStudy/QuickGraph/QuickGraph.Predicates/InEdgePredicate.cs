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

namespace QuickGraph.Predicates
{
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Predicates;
	/// <summary>
	/// In edge predicate
	/// </summary>
	public class InEdgePredicate : IEdgePredicate
	{
		private IEdgePredicate m_EdgePredicate;
		private IVertexPredicate m_VertexPredicate;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="ep"></param>
		/// <param name="vp"></param>
		public InEdgePredicate(IEdgePredicate ep, IVertexPredicate vp)
		{
			if (ep == null)
				throw new ArgumentNullException("Edge predicate");
			if (vp == null)
				throw new ArgumentNullException("Vertex predicate");

			m_EdgePredicate = ep;
			m_VertexPredicate = vp;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		/// <returns></returns>
		public bool Test(IEdge e)
		{
			if (e == null)
				throw new ArgumentNullException("e");

			return m_EdgePredicate.Test(e) && m_VertexPredicate.Test(e.Source);
		}
	}
}
