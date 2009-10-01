///// QuickGraph Library 
///// 
///// Copyright (c) 2003 Jonathan de Halleux
/////
///// This software is provided 'as-is', without any express or implied warranty. 
///// 
///// In no event will the authors be held liable for any damages arising from 
///// the use of this software.
///// Permission is granted to anyone to use this software for any purpose, 
///// including commercial applications, and to alter it and redistribute it 
///// freely, subject to the following restrictions:
/////
/////		1. The origin of this software must not be misrepresented; 
/////		you must not claim that you wrote the original software. 
/////		If you use this software in a product, an acknowledgment in the product 
/////		documentation would be appreciated but is not required.
/////
/////		2. Altered source versions must be plainly marked as such, and must 
/////		not be misrepresented as being the original software.
/////
/////		3. This notice may not be removed or altered from any source 
/////		distribution.
/////		
/////		QuickGraph Library HomePage: http://www.dotnetwiki.org
/////		Author: Jonathan de Halleux


//using System;

//namespace QuickGraphNUnit.GraphConcepts
//{
//    //using NUnit.Framework;

//    using QuickGraphNUnit.Generators;
//    using QuickGraph.Concepts;
//    using QuickGraph.Concepts.Predicates;
//    using QuickGraph.Concepts.Modifications;
//    using QuickGraph.Exceptions;
//    using Microsoft.Pex.Framework;
//    using QuickGraph.Representations;
//    using Microsoft.Pex.Framework.Validation;
//    using QuickGraph;
//    using Microsoft.VisualStudio.TestTools.UnitTesting;

//    public class DummyEdgeEqualPredicate : IEdgePredicate
//    {
//        IEdge re;
//        bool th;

//        public DummyEdgeEqualPredicate(IEdge e, bool throwIfTrue)
//        {
//            re = e;
//            th = throwIfTrue;
//        }
//        public bool Test(IEdge e)
//        {
//            if (e == null)
//                throw new ArgumentNullException("e");

//            if (th && e==re)
//                throw new Exception("e == re");
//            return e==re;
//        }
//    }

//   /* [PexClass]
//    [TestClass]
//    public partial class GenericMutableGraphTest
//    {
//        private IMutableGraphGenerator m_Generator;
		
//        public GenericMutableGraphTest()
//        {
//            m_Generator = null;
//        }

//        public IMutableGraphGenerator Generator
//        {
//            get
//            {
//                return m_Generator;
//            }
//            set
//            {
//                m_Generator = value;
//            }
//        }
//        public IEdgeMutableGraph Graph
//        {
//            get
//            {
//                return m_Generator.MutableGraph;
//            }
//        }


//        [PexMethod(MaxRunsWithoutNewTests = 200)]
//        /// Summary
//        /// Time: 3 min 52 sec
//        /// Pattern: AAAA, State relation        
//        public void AddRemoveVertex([PexAssumeUnderTest]AdjacencyGraph g)
//        {			
//            IVertex v = g.AddVertex();
//            g.RemoveVertex(v);
//            PexAssert.IsFalse(g.ContainsVertex(v));
//        }

//        [PexMethod]
//        [PexAllowedException(typeof(VertexNotFoundException))]
//        /// Summary
//        /// Time: 1 min 59 sec
//        /// Pattern: AAAA, AllowedException        
//        public void AddRemoveVertexNotFound([PexAssumeUnderTest]AdjacencyGraph g1, [PexAssumeUnderTest]AdjacencyGraph g2)
//        {			
//            IVertex v = g2.AddVertex();
//            g1.RemoveVertex(v);
//        }

//        [PexMethod(MaxRunsWithoutNewTests = 200)]        
//        /// Summary
//        /// Time: 4 min 29 sec
//        /// Pattern: AAAA, State Relation
//        public void AddEdge([PexAssumeUnderTest]AdjacencyGraph g)
//        {
//            IVertex u = g.AddVertex();
//            IVertex v = g.AddVertex();

//            IEdge e = g.AddEdge(u,v);

//            PexAssert.AreEqual(e.Source,u);
//            PexAssert.AreEqual(e.Target, v);
//        }

//        [PexMethod]
//        [PexAllowedException(typeof(VertexNotFoundException))]
//        /// Summary
//        /// Time: 1 min 3 sec
//        /// Pattern: AAAA, Allowed Exception
//        public void AddEdgeSourceNotFound([PexAssumeUnderTest]AdjacencyGraph g, [PexAssumeUnderTest]AdjacencyGraph g2)
//        {			
//            IVertex v = g.AddVertex();
//            IVertex u  = g2.AddVertex();
//            IEdge e = g.AddEdge(u,v);
//            PexAssert.IsTrue(false);    //Should not be reached
//        }


//        [PexMethod]
//        [PexAllowedException(typeof(VertexNotFoundException))]
//        /// Summary
//        /// Time: 1 min 8 sec
//        /// Pattern: AAAA, Allowed Exception
//        public void AddEdgeTargetNotFound([PexAssumeUnderTest]AdjacencyGraph g, [PexAssumeUnderTest]AdjacencyGraph g2)
//        {			
//            IVertex v = g.AddVertex();
//            IVertex u  = g2.AddVertex();
//            IEdge e = g.AddEdge(v,u);
//            PexAssert.IsTrue(false);    //Should not be reached
//        }

//        [PexMethod]
//        /// Summary
//        /// Time: 1 min 21 sec
//        /// Pattern: AAAA, State Relation
//        public void RemoveEdge([PexAssumeUnderTest]AdjacencyGraph g)
//        {			
//            IVertex v = g.AddVertex();
//            IVertex u  = g.AddVertex();
//            IEdge e = g.AddEdge(u,v);
//            g.RemoveEdge(e);

//            PexAssert.IsFalse(g.ContainsEdge(e));
//        }

//        [PexMethod(Timeout = 240)]
//        [PexAllowedException(typeof(ArgumentException))]
//        /// Summary
//        /// Time: 3 min 25 sec
//        /// Pattern: AAAA, Allowed Exception
//        public void RemoveEdgeNotFound([PexAssumeUnderTest]AdjacencyGraph g)
//        {			
//            IVertex v = g.AddVertex();
//            IVertex u  = g.AddVertex();
//            IEdge e = g.AddEdge(u,v);
//            g.RemoveEdge(e);
//            g.RemoveEdge(e);
//            PexAssert.IsTrue(false);    //Should not be reached
//        }

//        [PexMethod]
//        /// Summary
//        /// Time: 3 min 25 sec
//        /// Pattern: AAAA, Parameterized stub
//        public void ClearVertexSourceTarget([PexAssumeUnderTest]AdjacencyGraph g)
//        {			
//            IVertex v = g.AddVertex();
//            IVertex u  = g.AddVertex();
//            g.AddEdge(u,v);
//            g.ClearVertex(u);
//            g.ClearVertex(v);
//        }

//        [PexMethod]
//        /// Summary
//        /// Time: 1 min 25 sec
//        /// Pattern: AAAA, Parameterized stub
//        public void ClearVertexTargetSource([PexAssumeUnderTest]AdjacencyGraph g)
//        {			
//            IVertex u  = g.AddVertex();
//            IVertex v = g.AddVertex();
//            g.AddEdge(u,v);
//            g.ClearVertex(v);
//            g.ClearVertex(u);
//        }
//    }*/

//    [TestClass]
//    public partial class IncidenceMutableGraphTest : GenericMutableGraphTest
//    {
//        [TestInitialize]
//        public void Init()
//        {
//            Generator = new AdjacencyGraphGenerator();
//        }
//    }

//    [TestClass]
//    public partial class BidirectionaleMutableGraphTest : GenericMutableGraphTest
//    {
//        [TestInitialize]
//        public void Init()
//        {
//            Generator = new BidirectionalGraphGenerator();
//        }
//    }
//}
