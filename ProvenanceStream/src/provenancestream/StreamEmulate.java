/*
 * Code to render the streaming provenance graph from the file sample_prov.dgs
 */
package provenancestream;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
/**
 *
 * @author rahman
 */
public class StreamEmulate {
    public static void main(String[] args) throws Exception {
		String filePath = "/home/rahman/NetBeansProjects/rahman-project/ProvenanceStream/sample_prov.dgs";
		Graph g = new DefaultGraph("ProvStream");
		FileSource fs = FileSourceFactory.sourceFor(filePath);
                fs.addSink(g);
                g.addAttribute("ui.stylesheet", "node { text-mode: normal; text-style: bold; fill-color: blue;}");
                g.display();                
		fs.begin(filePath);
		while (fs.nextEvents()) {
                        for (Node node : g) {
                                node.addAttribute("ui.label", node.getId());
                                }
				Thread.sleep(2000);
			}
                fs.end();
		fs.removeSink(g);
		
	}
}
