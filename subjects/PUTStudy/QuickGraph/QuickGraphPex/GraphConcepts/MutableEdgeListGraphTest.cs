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
    using Microsoft.Pex.Framework;
    using QuickGraph.Representations;

    [PexClass]
    public partial class GenericMutableEdgeListGraphTest
	{
		private IMutableEdgeListGraphGenerator m_Generator;
		private Random m_Rnd;
		
		public GenericMutableEdgeListGraphTest()
		{
			m_Generator = null;
			m_Rnd = new Random();
		}

		public IMutableEdgeListGraphGenerator Generator
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
		public IMutableEdgeListGraph Graph
		{
			get
			{
				return m_Generator.MutableEdgeListGraph;
			}
		}
		public Random Rnd
		{
			get
			{
				return m_Rnd;
			}
		}

		[PexMethod]
        /// Summary
        /// Time: 4 min 52 sec
        /// Pattern: Parameterized stub
		public void RemoveEdgeIf([PexAssumeUnderTest]BidirectionalGraph g)
		{			
			IEdge e = RandomGraph.Edge(g, Rnd);

			g.RemoveEdgeIf(new DummyEdgeEqualPredicate(e,false));
			g.RemoveEdgeIf(new DummyEdgeEqualPredicate(e,true));
		}
	}

	[TestFixture]
    public partial class IncidenceMutableEdgeListGraphTest : GenericMutableEdgeListGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new AdjacencyGraphGenerator();
		}
	}

	[TestFixture]
    public partial class BidirectionaleMutableEdgeListGraphTest : GenericMutableEdgeListGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new BidirectionalGraphGenerator();
		}
	}
}
