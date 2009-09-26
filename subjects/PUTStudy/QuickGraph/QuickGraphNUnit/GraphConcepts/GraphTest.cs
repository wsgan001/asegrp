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
	using QuickGraph.Concepts;
	using QuickGraphNUnit.Generators;

	public class GenericGraphTest
	{
		private IGraphGenerator m_Generator;
		
		public GenericGraphTest()
		{
			m_Generator = null;
		}

		public IGraphGenerator Generator
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
		public IGraph Graph(bool pe)
		{
			return m_Generator.Graph(pe);
		}

		[Test]
		public void IsDirected()
		{
			IGraph g = Graph(true);
			Assert.AreEqual(g.IsDirected,true);
		}

		[Test]
		public void AllowParallelEdges()
		{
			IGraph g = Graph(true);
			Assert.AreEqual(g.AllowParallelEdges,true);
			g = Generator.Graph(false);
			Assert.AreEqual(g.AllowParallelEdges,false);
		}
	}

	[TestFixture]
	public class AdjacencyGraphTest : GenericGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new AdjacencyGraphGenerator();
		}
	}

	[TestFixture]
	public class BidirectionalGraphTest : GenericGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new BidirectionalGraphGenerator();
		}
	}
}
