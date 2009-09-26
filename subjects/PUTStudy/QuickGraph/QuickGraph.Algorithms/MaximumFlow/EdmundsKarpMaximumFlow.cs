
namespace QuickGraph.Algorithms.MaximumFlow
{
	using System;
	using QuickGraph.Collections;
	using QuickGraph.Predicates;
	using QuickGraph.Algorithms.Visitors;
	using QuickGraph.Algorithms.Search;

	/// <summary>
	/// Description résumée de EdmundsKarpMaximumFlow.
	/// </summary>
	public class EdmundsKarpMaximumFlow : Algorithm
	{
		private EdgeDoubleDictionary m_ResidualEdgeCapacities;
		private EdgeDoubleDictionary m_EdgeCapacities;
		private VertexEdgeDictionary m_Predecessors;

		public EdmundsKarpMaximumFlow(
			Graph g,
			EdgeDoubleDictionary edgeCapacities
			) : base(g)
		{
			if (edgeCapacities == null)
				throw new ArgumentNullException("Edge capacities");

			m_EdgeCapacities = edgeCapacities;
			m_ResidualEdgeCapacities = null;
			m_Predecessors = new VertexEdgeDictionary();
		}

		public EdgeDoubleDictionary ResidualEdgeCapacities
		{
			get
			{
				return m_ResidualEdgeCapacities;
			}
		}
		public EdgeDoubleDictionary EdgeCapacities
		{
			get
			{
				return m_EdgeCapacities;
			}
		}

		public VertexEdgeDictionary Predecessors
		{
			get
			{
				return m_Predecessors;
			}
		}

		public double Compute(Vertex src, Vertex sink)
		{
			m_ResidualEdgeCapacities = new EdgeDoubleDictionary();

			// initializing
			foreach(Vertex u in VisitedGraph.Vertices)
				foreach(Edge e in u.OutEdges)
					ResidualCapacities[e] = Capacities[e];

			Colors[sink] = GraphColor.Gray;
			while (Colors[sink] != GraphColor.White) 
			{
				VertexBuffer Q = new VertexBuffer();
				ResidualEdgePredicate ep = new ResidualEdgePredicate(ResidualCapacities);
				
				BreadthFirstSearchAlgorithm bfs = new BreadthFirstSearchAlgorithm(resg,Q,Colors);
				PredecessorVisitor pred = new PredecessorVisitor(Predecessors);
				pred.RegisterHandlers(bfs);
				bfs.Compute(src);

				if (Colors[sink] != GraphColor.White)
					Augment(src, sink, pred.Predecessors);
			} // while

			double flow = 0;
			foreach(Edge e in src.OutEdges)
				flow += (EdgeCapacities[e] - ResidualEdgeCapacities[e]);
    
			return flow;
		}

		internal void Augment(Vertex src, Vertex sink, VertexEdgeDictionary predecessors)
		{
			// find minimum residual capacity along the augmenting path
			double delta = double.MaxValue;
			Edge e = predecessors[sink];
			Vertex u = null;
			do 
			{
				delta = Math.Min(delta, ResidualEdgeCapacities[e]);
				u = e.Source;
				e = predecessors[u];
		     } while (u != src);

			// push delta units of flow along the augmenting path
			e = predecessors[sink];
			do 
			{
				ResidualEdgeCapacities[e] -= delta;
				ResidualEdgeCapacities[ReverseEdges[e]] += delta;
				u = e.Source;
				e = predecessors[u];
			} while (u != src);
		}
	}
}
