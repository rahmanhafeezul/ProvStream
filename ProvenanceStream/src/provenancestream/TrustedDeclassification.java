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
public class TrustedDeclassification{
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
    public static void computeGraph_Trusted_Declassification(String a, String b, String c){
        
                Dijkstra dijkstra_1 = new Dijkstra();
		dijkstra_1.init(g);
                Dijkstra dijkstra_2 = new Dijkstra();
                dijkstra_2.init(g);
                try{
		dijkstra_1.setSource(g.getNode(a));
		dijkstra_1.compute();
                dijkstra_2.setSource(g.getNode(b));
                dijkstra_2.compute();

		for (Node node : g)
			System.out.printf("%s->%s:%10.2f%n", dijkstra_1.getSource(), node,
					dijkstra_1.getPathLength(node));
                for (Node node : g)
			System.out.printf("%s->%s:%10.2f%n", dijkstra_2.getSource(), node,
					dijkstra_2.getPathLength(node));
                List<Node> list_1 = dijkstra_1.getPath(g.getNode(b)).getNodePath();
                List<Node> list_2 = dijkstra_2.getPath(g.getNode(c)).getNodePath();
                list_1.remove(g.getNode(a));
                list_1.remove(g.getNode(b));
                list_2.remove(g.getNode(b));
                list_2.remove(g.getNode(c));
                if(list_1.size()>=1 || list_2.size()>=1){
                for (Node node : dijkstra_1.getPathNodes(g.getNode(b)))
			node.addAttribute("ui.style", "fill-color: red;");

                for (Edge edge : dijkstra_1.getPathEdges(g.getNode(b)))
			edge.addAttribute("ui.style", "fill-color: red;");
                for (Node node : dijkstra_2.getPathNodes(g.getNode(c)))
			node.addAttribute("ui.style", "fill-color: red;");

                for (Edge edge : dijkstra_2.getPathEdges(g.getNode(c)))
			edge.addAttribute("ui.style", "fill-color: red;");
                }
		System.out.println(dijkstra_1.getPath(g.getNode(b)));
                System.out.println(dijkstra_2.getPath(g.getNode(b)));

		

		dijkstra_1.clear();
                dijkstra_2.clear();
                }
                catch(Exception e)
                {
                }
        
    }
    public static void compute(String a, String b, String c)throws Exception
    {
        g.display(); 
                while (fs.nextEvents()) {
                        for (Node node : g) {
                                node.addAttribute("ui.label", node.getId());                                
                               }
                                System.out.println("*********************");
                                computeGraph_Trusted_Declassification(a, b, c);
				Thread.sleep(3000);                                
			}
                computeGraph_Trusted_Declassification(a, b, c);
        
    }
    public static void main(String[] args)throws Exception {
                compute("A","B","E");
	}
 }
