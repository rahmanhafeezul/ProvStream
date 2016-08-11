/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provenancestream;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
/**
 *
 * @author rahman
 */
public class ProvenanceStream {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Graph graph = new SingleGraph("Trial");

		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		graph.display();
    }
    
}
