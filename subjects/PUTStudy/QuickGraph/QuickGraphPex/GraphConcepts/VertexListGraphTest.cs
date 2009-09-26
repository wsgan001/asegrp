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
    using Microsoft.Pex.Framework;
    using QuickGraph.Representations;
    
    [PexClass]
    public partial class GenericVertexListGraphTest
	{
		private IVertexListGraphGenerator m_Generator;
		
		public GenericVertexListGraphTest()
		{
			m_Generator = null;
		}

		public IVertexListGraphGenerator Generator
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

		public IVertexListGraph VertexList
		{
			get
			{
				return m_Generator.VertexList;
			}
		}
        
        /**
         * Combines both Count and Iteration methods
         **/
        [PexMethod(MaxBranches = 20000)]
        /// Summary
        /// Time: 9 min 34 sec
        /// Combines two unit tests Count and Iteration
        /// Created 2 factory methods with loops in them
        /// Pattern: RoundTrip Pattern, AAAA
        public void IterationPUT([PexAssumeUnderTest]AdjacencyGraph g)
        {            
            int n = g.VerticesCount;
            int i = 0;
            foreach (IVertex v in g.Vertices)
            {
                ++i;
            }
            PexAssert.AreEqual(n, i);
        }
	}

	[TestFixture]
    public partial class IncidenceVertexListGraphTest : GenericVertexListGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new AdjacencyGraphGenerator();
		}
	}

	[TestFixture]
    public partial class BidirectionalVertexListGraphTest : GenericVertexListGraphTest
	{
		[SetUp]
		public void Init()
		{
			Generator = new BidirectionalGraphGenerator();
		}
	}
}
