/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provenancestream;
import org.graphstream.algorithm.community.Leung;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
/**
 *
 * @author rahman
 */
public class ProvenanceStream {

    /**
     * @param args the command line arguments
     */
    private static final Double WEIGHT_VALUE = 1d;
    private static final String WEIGHT = "weight";
    public static void main(String[] args) {
        // TODO code application logic here
        

		Graph graph = new SingleGraph("CLustering");
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addNode("D");
		graph.addNode("E");
		graph.addNode("F");
		// cluster 1
		graph.addEdge("AB", "A", "B").setAttribute(WEIGHT, WEIGHT_VALUE);
		graph.addEdge("BC", "B", "C").setAttribute(WEIGHT, WEIGHT_VALUE);
		graph.addEdge("CA", "C", "A").setAttribute(WEIGHT, WEIGHT_VALUE);
		// cluster 2
		graph.addEdge("DE", "D", "E").setAttribute(WEIGHT, WEIGHT_VALUE);
		graph.addEdge("EF", "E", "F").setAttribute(WEIGHT, WEIGHT_VALUE);
		graph.addEdge("FD", "F", "D").setAttribute(WEIGHT, WEIGHT_VALUE);
		// connection
		graph.addEdge("12", "D", "A").setAttribute(WEIGHT, WEIGHT_VALUE / 2);
		// clustering
		Leung algorithm = new Leung(graph, null, WEIGHT);
		algorithm.compute();
		graph.display();
		for (Node n : graph.getNodeSet()) {
			System.out.println("node " + n.getId() + " is in " + n.getAttribute("ui.class"));
		}
	}
}
