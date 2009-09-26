
namespace QuickGraphTest
{
	using System;
	using System.IO;
	using System.Text;
	using System.Collections;
	using System.Collections.Specialized;

	using QuickGraph;
	using QuickGraph.Algorithms;
	using QuickGraph.Collections;
	using QuickGraph.Algorithms.Search;
	using QuickGraph.Algorithms.Graphviz;
	using QuickGraph.Algorithms.Visitors;


	/// <summary>
	/// Description résumée de Class1.
	/// </summary>
	class Class1
	{
		/// <summary>
		/// Point d'entrée principal de l'application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			try
			{
/*
				VertexStringDictionary verticesNames = new VertexStringDictionary();
				EdgeStringDictionary edgesNames = new EdgeStringDictionary();
				EdgeDoubleDictionary edgesWeights = new EdgeDoubleDictionary();
				IncidenceGraph g = new IncidenceGraph(true);
			
				// adding vertex
				Vertex u = g.AddVertex();
				verticesNames[u]="u";
				Vertex v = g.AddVertex();
				verticesNames[v]="v";
				Vertex w = g.AddVertex();
				verticesNames[w]="w";
				Vertex x = g.AddVertex();
				verticesNames[x]="x";
				Vertex y = g.AddVertex();
				verticesNames[y]="y";
				Vertex z = g.AddVertex();
				verticesNames[z]="z";

				// adding edges
				Edge uv = g.AddEdge(u,v);
				edgesNames[uv]="uv";
				edgesWeights[uv]=1;
				Edge ux = g.AddEdge(u,x);
				edgesNames[ux]="ux";
				edgesWeights[ux]=0.8;
				Edge wu = g.AddEdge(w,u);
				g.AddEdge(w,u);
				edgesNames[wu]="wu";
				edgesWeights[wu]=0.2;
				Edge xv = g.AddEdge(x,v);
				edgesNames[xv]="xv";
				edgesWeights[xv]=1.1;
				Edge vy = g.AddEdge(v,y);
				edgesNames[vy]="vy";
				edgesWeights[vy]=2.0;
				Edge wy = g.AddEdge(w,y);
				edgesNames[wy]="wy";
				edgesWeights[wy]=1.5;
				Edge yw = g.AddEdge(y,w);
				edgesNames[yw]="yw";
				edgesWeights[yw]=0.2;
				Edge wz = g.AddEdge(w,z);
				edgesNames[wz]="wz";
				edgesWeights[wz]=0.1;

				RandomGraph.Graph(g, 20,50,new Random(),true);

/*
				// do a dfs serach
				Console.WriteLine("---- DepthFirstSearch");
				DepthFirstSearchAlgorithm dfs = new DepthFirstSearchAlgorithm(g);
				//TestDepthFirstSearchVisitor dfsVis = 
				//	new TestDepthFirstSearchVisitor(dfs,verticesNames);
				AlgorithmTracerVisitor dfstracer = new
					AlgorithmTracerVisitor(g,"dfs",".",GraphvizImageType.Png);
				dfstracer.VertexLabels = verticesNames;
				dfstracer.RegisterVertexHandlers(dfs);
				dfstracer.RegisterEdgeHandlers(dfs);

				dfs.Compute();
*/

				Vertex source = u;
				source = RandomGraph.Vertex(g,new Random());
				Console.WriteLine("source: {0}",source.GetHashCode());
				Console.WriteLine("---- BreathFirstSearch");
				BreadthFirstSearchAlgorithm bfs = new BreadthFirstSearchAlgorithm(g);
				TestBreadthFirstSearchVisitor bfsVis 
					= new TestBreadthFirstSearchVisitor(bfs,verticesNames,source);

				AlgorithmTracerVisitor bfstracer = new
					AlgorithmTracerVisitor(g,"bfs",".",GraphvizImageType.Png);
//				bfstracer.VertexLabels = verticesNames;
				bfs.RegisterTreeEdgeBuilderHandlers(bfsTracer);
				bfs.RegisterVertexColorizeHandlers(bfsTracer);

				bfs.Compute(source);
/*
				Console.WriteLine("---- Dijkstra");
				DijkstraShortestPathAlgorithm dij = new DijkstraShortestPathAlgorithm(
					g,
					edgesWeights
					);
				TestDijkstraShortestPathVisitor dijVis = new TestDijkstraShortestPathVisitor(dij,verticesNames);
				AlgorithmTracerVisitor dijtracer = new
					AlgorithmTracerVisitor(g,"dij",".",GraphvizImageType.Png);
				dijtracer.VertexLabels = verticesNames;
				dijtracer.EdgeLabels = edgesWeights;
				dijtracer.RegisterVertexHandlers(dij);
//				dijtracer.RegisterEdgeHandlers(dij);
				dij.Compute(g.Vertex(0));

				Console.WriteLine("Distance from {0}", verticesNames[g.Vertex(0)]);
				foreach(DictionaryEntry de in dij.Distances)
				{
		
					Console.WriteLine("\t-> {0}, {1}",
						verticesNames[(Vertex)de.Key],
						de.Value.ToString()
						);
				}

				Console.WriteLine("---- Topological sort");
				VertexCollection vs = new VertexCollection();
				TopologicalSortAlgorithm tps= new TopologicalSortAlgorithm(g);
				tps.Compute(vs);
				foreach(Vertex ve in vs)
				{
					Console.WriteLine("v - {0}",verticesNames[ve]);
				}

				Console.WriteLine("--- graphviz output");
				GraphvizWriterAlgorithm gw = new GraphvizWriterAlgorithm(
					g,"dotgenerator",".",GraphvizImageType.Png);
				TestGraphvizVertex gv = new TestGraphvizVertex(verticesNames);
				gw.WriteVertex += new VertexHandler( gv.WriteVertex );
				gw.WriteEdge+= new EdgeHandler( gv.WriteEdge );
				gw.Write(GraphvizImageType.Png);
				gw.Write(GraphvizImageType.Svg);
				gw.Write(GraphvizImageType.Svgz);
				gw.Write(GraphvizImageType.Gif);
				gw.Write(GraphvizImageType.Jpeg);
*/				
				Console.WriteLine("Test finished");
				String s2=Console.ReadLine();
			}
			catch(Exception ex)
			{				
				Console.WriteLine(ex.ToString());
				String s=Console.ReadLine();
			}

		}
	}
}
