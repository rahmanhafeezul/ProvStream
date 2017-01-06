/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provenancestream;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
 import java.util.List;
 
 import org.graphstream.algorithm.Dijkstra;
/**
 *
 * @author HafeezulRahman
 */
public class NonInterference{
    private static String filePath="D:\\Courses\\sem 9\\MTP\\rahman-project\\ProvenanceStream\\src\\provenancestream\\sample_prov.dgs";;
    private static Graph g = new DefaultGraph("ProvStream");
    private static FileSource fs; 
    static{try{
    fs = FileSourceFactory.sourceFor(filePath);
    fs.addSink(g);
    System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

    g.addAttribute("ui.stylesheet", "node { size: 40px, 15px;shape: rounded-box; text-mode: normal; text-style: bold; fill-color: yellow;}");
    fs.begin(filePath);
}
    catch(Exception e){}}
    public static void computeGraph_Non_Interference(String a, String b){
        Dijkstra dijkstra = new Dijkstra();

		dijkstra.init(g);
                try{
		dijkstra.setSource(g.getNode(a));
		dijkstra.compute();

		for (Node node : g)
			System.out.printf("%s->%s:%10.2f%n", dijkstra.getSource(), node,
					dijkstra.getPathLength(node));

                for (Node node : dijkstra.getPathNodes(g.getNode(B)))
			node.addAttribute("ui.style", "fill-color: red;");
                for (Node node: dijkstra.getPathNodes(g.getNode("B")))

                for (Edge edge : dijkstra.getPathEdges(g.getNode(b)))
			edge.addAttribute("ui.style", "fill-color: red;");

		System.out.println(dijkstra.getPath(g.getNode(b)));

		
		List<Node> list2 = dijkstra.getPath(g.getNode(b)).getNodePath();

		dijkstra.clear();}
                catch(Exception e)
                {
                }
        
    }
    public static void compute(String a, String b)throws Exception{
        g.display(); 
                while (fs.nextEvents()) {
                        for (Node node : g) {
                                node.addAttribute("ui.label", node.getId());
                                
                                }
                                System.out.println("*********************");

                                computeGraph_Non_Interference(a, b);
				Thread.sleep(3000);                                
			}
                computeGraph_Non_Interference(a, b);
	}
    
    public static void main(String[] args)throws Exception {
                compute("A", "F");
 }
}