/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provenancestream;
import org.graphstream.algorithm.networksimplex.DynamicOneToAllShortestPath;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
/**
 *
 * @author HafeezulRahman
 */
public class NonInterference_Simplex {
    private static String filePath="D:\\Courses\\sem 9\\MTP\\rahman-project\\ProvenanceStream\\src\\provenancestream\\sample_prov.dgs";;
    private static Graph g = new DefaultGraph("ProvStream");
    private static DynamicOneToAllShortestPath d = new DynamicOneToAllShortestPath("length");
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
        try{
            Node source = g.getNode(a);            
            d.setSource(source.getId());
            d.compute();
                for (Node node : g)
                                System.out.printf("%s->%s:%d%n", d.getSource(), node, d.getPathLength(node));
                
                for (Node node : d.getPathNodes(g.getNode(b)))
			node.addAttribute("ui.style", "fill-color: red;");

                for (Edge edge : d.getPathEdges(g.getNode(b)))
			edge.addAttribute("ui.style", "fill-color: red;");

		System.out.println(d.getPath(g.getNode(b)));
        }
        catch(Exception e)
        {
           System.out.println("caught"); 
        }
    }
    public static void compute(String a, String b)throws Exception{
        g.display();
        d.init(g);  
        
        while (fs.nextEvents()) {
            	for (Node node : g) {
                                node.addAttribute("ui.label", node.getId());                                
                                }
                                System.out.println("*********************");

                                computeGraph_Non_Interference(a, b);
                                
				Thread.sleep(3000);                                
			}
        computeGraph_Non_Interference(a, b);
        d.terminate();
        }
    
    public static void main(String[] args)throws Exception{
        compute("B", "D");
    }
    }   

