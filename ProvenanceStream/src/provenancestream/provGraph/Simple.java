import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.view.Viewer;
 
public class Simple {
	
	public static void main(String[] args) {
 
		Graph graph = new SingleGraph("Test");
		
		Viewer viewer = graph.display();
		HierarchicalLayout hl = new HierarchicalLayout();
		viewer.enableAutoLayout(hl);
		
		graph.addNode("A" );		
		graph.addNode("B" );
		graph.addNode("C" );
		graph.addNode("D" );
		graph.addEdge("AB", "A", "B");
		graph.addEdge("AC", "A", "C");
	graph.addEdge("CD", "C", "D");
	}
	
}