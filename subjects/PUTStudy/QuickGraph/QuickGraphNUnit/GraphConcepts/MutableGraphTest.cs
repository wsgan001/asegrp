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

namespace QuickGraphNUnit.GraphConcepts
{
	using NUnit.Framework;

	using QuickGraphNUnit.Generators;
	using QuickGraph.Concepts;
	using QuickGraph.Concepts.Predicates;
	using QuickGraph.Concepts.Modifications;
	using QuickGraph.Exceptions;

	public class DummyEdgeEqualPredicate : IEdgePredicate
	{
		IEdge re;
		bool th;

		public DummyEdgeEqualPredicate(IEdge e, bool throwIfTrue)
		{
			re = e;
			th = throwIfTrue;
		}
		public bool Test(IEdge e)
		{
			if (e == null)
				throw new ArgumentNullException("e");

			if (th && e==re)
				throw new Exception("e == re");
			return e==re;
		}
	}

	public class GenericMutableGraphTest
	{
		private IMutableGraphGenerator m_Generator;
		
		public GenericMutableGraphTest()
		{
			m_Generator = null;
		}

		public IMutableGraphGenerator Generator
		{
			get
			{
				return m_Generator;
			}
			set
			{
				m_Generator = value;
			}
		}
		public IEdgeMutableGraph Graph
		{
			get
			{
				return m_Generator.MutableGraph;
			}
		}


		[Test]
		public void AddRemoveVertex()
		{
			IEdgeMutableGraph g = Graph;
			IVertex v = g.AddVertex();
			g.RemoveVertex(v);
		}

		[Test]
		[ExpectedException(typeof(VertexNotFoundException))]
		public void AddRemoveVertexNotFound()
		{
			IEdgeMutableGraph g = Graph;
			IEdgeMutableGraph g2 = Graph;
			IVertex v = g2.AddVertex();
			g.RemoveVertex(v);
		}

		[Test]
		public void AddEdge()
		{
			IEdgeMutableGraph g = Graph;
			IVertex v = g.AddVertex();
			IVertex u  = g.AddVertex();
			IEdge e = g.AddEdge(u,v);

			Assert.AreEqual(e.Source,u);
			Assert.AreEqual(e.Target,v);
		}

		[Test]
		[ExpectedException(typeof(VertexNotFoundException))]
		public void AddEdgeSourceNotFound()
		{
			IEdgeMutableGraph g = Graph;
			IEdgeMutableGraph g2 = Graph;
			IVertex v = g.AddVertex();
			IVertex u  = g2.AddVertex();
			IEdge e = g.AddEdge(u,v);
		}


		[Test]
		[ExpectedException(typeof(VertexNotFoundException))]
		public void AddEdgeTargetNotFound()
		{
			IEdgeMutableGraph g = Graph;
			IEdgeMutableGraph g2 = Graph;
			IVertex v = g.AddVertex();
			IVertex u  = g2.AddVertex();
			IEdge e = g.AddEdge(v,u);
		}

		[Test]
		public void RemoveEdge()
		{
			IEdgeMutableGraph g = Graph;
			IVertex v = g.AddVertex();
			IVertex u  = g.AddVertex();
			IEdge e = g.AddEdge(u,v);
			g.RemoveEdge(e);
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void RemoveEdgeNotFound()
		{
			IEdgeMutableGraph g = Graph;
			IVertex v = g.AddVertex();
			IVertex u  = g.AddVertex();
			IEdge e = g.AddEdge(u,v);
			g.RemoveEdge(e);
			g.RemoveEdge(e);
		}

		[Test]
		public void ClearVertexSourceTarget()
		{
			IEdgeMutableGraph g = Graph;
			IVertex v = g.AddVertex();
			IVertex u  = g.AddVertex();
			g.AddEdge(u,v);
			g.ClearVertex(u);
			g.ClearVertex(v);
		}

		[Test]
		public void ClearVertexTargetSource()
		{
			IEdgeMutableGraph g = Graph;
			IVertex u  = g.AddVertex();
			IVertex v = g.AddVertex();
			g.AddEdge(u,v);
			g.ClearVertex(v);
			g.ClearVertex(u);
		}
	}

	[TestFixture]
	public class IncidenceMutableGraphTest : GenericMutableGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new AdjacencyGraphGenerator();
		}
	}

	[TestFixture]
	public class BidirectionaleMutableGraphTest : GenericMutableGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new BidirectionalGraphGenerator();
		}
	}
}
