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
	using QuickGraph.Concepts.Traversals;

	public class GenericAdjacencyGraphTest
	{
		private IIncidenceGraphGenerator m_Generator;
		
		public GenericAdjacencyGraphTest()
		{
			m_Generator = null;
		}

		public IIncidenceGraphGenerator Generator
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
		public IIncidenceGraph Graph
		{
			get
			{
				return m_Generator.IncidenceGraph;
			}
		}


		[Test]
		public void TestOutDegree()
		{
			IIncidenceGraph g = Generator.IncidenceGraph;
			
		}
	}

	[TestFixture]
	public class IncidenceAdjacencyGraphTest : GenericAdjacencyGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new AdjacencyGraphGenerator();
		}
	}

	[TestFixture]
	public class BidirectionaleAdjacencyGraphTest : GenericAdjacencyGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new BidirectionalGraphGenerator();
		}
	}
}
