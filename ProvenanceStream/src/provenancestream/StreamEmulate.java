/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provenancestream;
import java.io.IOException;
import org.graphstream.graph.Graph;
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
                g.display();
		fs.addSink(g);

		try {
			fs.begin(filePath);

			while (fs.nextEvents()) {
				   Thread.sleep(10000);
			}
		} catch( IOException e) {
		}

		try {
			fs.end();
		} catch( IOException e) {
		} finally {
			fs.removeSink(g);
		}
	}
}
