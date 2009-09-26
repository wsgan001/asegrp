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
    using Microsoft.Pex.Framework;
    using Microsoft.Pex.Framework.Domains;

    [PexClass]
    public partial class GenericGraphTest
	{
		private IGraphGenerator m_Generator;
		
		public GenericGraphTest()
		{
            m_Generator = new AdjacencyGraphGenerator();
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
        
		[PexMethod]
        [PexBooleanAsZeroOrOne]
        /// Summary
        /// Time: 9 min 32 sec
        /// Pattern: AAA, Constructor test, Roundtrip
        /// Combines two unit tests into one        
		public void IsDirected(bool isDirected)
		{
			IGraph g = Graph(isDirected);

            if (isDirected)
            {
                PexAssert.AreEqual(isDirected, g.IsDirected);
                PexAssert.AreEqual(isDirected, g.AllowParallelEdges);
            }
            else
            {
                PexAssert.AreEqual(isDirected, g.AllowParallelEdges);
            }
		}
	}

	[TestFixture]
    public partial class AdjacencyGraphTest : GenericGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new AdjacencyGraphGenerator();
		}
	}

	[TestFixture]
    public partial class BidirectionalGraphTest : GenericGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new BidirectionalGraphGenerator();
		}
	}
}
