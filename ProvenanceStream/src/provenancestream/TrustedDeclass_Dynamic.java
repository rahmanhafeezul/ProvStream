/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provenancestream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Vector;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
public class TrustedDeclass_Dynamic {
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
 
	static Stack<Integer> visitedNodesStack;
        static HashMap<Integer, String> int_to_node = new HashMap();
        static HashMap<String, Integer> node_to_int = new HashMap();
        static Vector path=new Vector();
 
 	private static final String FILENAME = "D:\\Courses\\sem 9\\MTP\\rahman-project\\ProvenanceStream\\src\\provenancestream\\sample_prov.dgs";

	static boolean found = false;
	private static int numberOfCycle = 0;
	public static void main(String[] args) throws IOException, FileNotFoundException, InterruptedException {
            compute("A","B","D");
        }
        
            public static void compute(String a, String b, String c) throws FileNotFoundException, IOException, InterruptedException{
		List<Integer>[] Adj = new ArrayList[11];
                visitedNodesStack=new Stack<Integer>();
                 path.add(a);path.add(b);path.add(c);

			int counter=1;
			String CurrentLine;

			BufferedReader br = new BufferedReader(new FileReader(FILENAME));

                        for(int i=0;i<3;i++)
                        {
                            br.readLine();
                        }
                        g.display();
                        while (fs.nextEvents()) {
                        for (Node node : g) {
                                node.addAttribute("ui.label", node.getId());                                
                               }
                                //System.out.println("*********************");
                                //computeGraph_Trusted_Declassification(a, b, c);
				Thread.sleep(3000); 
                                CurrentLine = br.readLine();
                                //System.out.println(CurrentLine);
                                String[] curr = CurrentLine.split(" ");
                                if("an".equals(curr[0]))
                                {   //System.out.println(curr[1]+" node");
                                    int_to_node.put(counter, curr[1]);
                                    node_to_int.put(curr[1], counter);
                                    Adj[counter]=new ArrayList<Integer>();
                                    counter=counter+1;
                                    //System.out.println(CurrentLine);
                                } 
                                if("ae".equals(curr[0])){
                                    Adj[node_to_int.get(curr[2])].add(node_to_int.get(curr[4]));
                                    try{
                                        if(g.getNode(c)==g.getNode(curr[4]))
                                        {
                                            findAllDistancebetweentwovertices(Adj,node_to_int.get(a),node_to_int.get(c), b);
                                        }
                                    }
                                    catch(Exception e){}
                                }
			}
                        CurrentLine = br.readLine();
                                //System.out.println(CurrentLine);
                                String[] curr = CurrentLine.split(" ");
                                if("an".equals(curr[0]))
                                {   System.out.println(curr[1]+" node");
                                    int_to_node.put(counter, curr[1]);
                                    node_to_int.put(curr[1], counter);
                                    Adj[counter]=new ArrayList<Integer>();
                                    counter=counter+1;
                                    //System.out.println(CurrentLine);
                                } 
                                if("ae".equals(curr[0])){
                                    Adj[node_to_int.get(curr[2])].add(node_to_int.get(curr[4]));
                                    try{
                                        if(g.getNode(c)==g.getNode(curr[4]))
                                        {
                                            findAllDistancebetweentwovertices(Adj,node_to_int.get(a),node_to_int.get(c), b);
                                        }
                                    }
                                    catch(Exception e){}
                                }
//			while ((CurrentLine = br.readLine()) != null) {
//                                System.out.println(CurrentLine);
//                                String[] curr = CurrentLine.split(" ");
//                                if("an".equals(curr[0]))
//                                {   int_to_node.put(counter, curr[1]);
//                                    node_to_int.put(curr[1], counter);
//                                    Adj[counter]=new ArrayList<Integer>();
//                                    counter=counter+1;
//                                    //System.out.println(CurrentLine);
//                                } 
//                                if("ae".equals(curr[0])){
//                                    Adj[node_to_int.get(curr[2])].add(node_to_int.get(curr[4]));
//                                }
//				
//			}

 
 
//		Adj[1] = new ArrayList<Integer>();
//		Adj[1].add(2);
//		Adj[1].add(3);
//		Adj[1].add(8);
//		Adj[2] = new ArrayList<Integer>();
//		Adj[2].add(3);
//		Adj[2].add(7);
//		Adj[3] = new ArrayList<Integer>();
//		Adj[3].add(4);
//		Adj[5] = new ArrayList<Integer>();
//		Adj[5].add(4);
//		Adj[5].add(6);
//		Adj[7] = new ArrayList<Integer>();
//		Adj[7].add(3);
//		Adj[8] = new ArrayList<Integer>();
//		Adj[8].add(9);
//		Adj[9] = new ArrayList<Integer>();
//		Adj[9].add(3);
// 
               // System.out.print(node_to_int.get(c)+" 6");
                
		//findAllDistancebetweentwovertices(Adj,node_to_int.get(a),node_to_int.get(c), b);
//		if(!found){
//			System.out.println("No path found");
//		}
}
private static  void findAllDistancebetweentwovertices(List<Integer>[] adj,int startingVertix,int endVertex, String b){
		if(startingVertix==endVertex){
			//printNodesInStack(visitedNodesStack);System.out.print(int_to_node.get(endVertex));System.out.println();
                    ListIterator<Integer> itr=visitedNodesStack.listIterator();
                    Vector vec_path = new Vector();
                        while(itr.hasNext()){
			vec_path.add(int_to_node.get(itr.next()));
                            }
                        vec_path.add(int_to_node.get(endVertex));
                        System.out.println(vec_path.toString()+" "+path.toString());
                        if(!vec_path.equals(path))
                        {
                            System.out.println("anamoly");
//                            Iterator<String> node = vec_path.iterator();
//                            while(node.hasNext()){
//                               Node color_node = g.getNode(node.next());
//                               color_node.addAttribute("ui.style", "fill-color: red;");
//                                        }
                            for(int i=0;i<vec_path.size()-1;i++)
                            {
                                Node color_node = g.getNode(vec_path.elementAt(i).toString());
                                Node target_node = g.getNode(vec_path.elementAt(i+1).toString());
                                Edge color_edge = color_node.getEdgeBetween(target_node);
                                color_node.addAttribute("ui.style", "fill-color: red;");
                                color_edge.addAttribute("ui.style", "fill-color: red;");
                            }
                            Node color_node = g.getNode(vec_path.elementAt(vec_path.size()-1).toString());
                            color_node.addAttribute("ui.style", "fill-color: red;");
                        }
                        else{
                            System.out.println("happy");
                        }
			found=true;
			return;
		}
		if(!visitedNodesStack.contains(startingVertix)){
			visitedNodesStack.push(startingVertix);
			if(adj[startingVertix]!=null){
				for(int i:adj[startingVertix]){
					if(!visitedNodesStack.contains(i)){
						findAllDistancebetweentwovertices(adj, i, endVertex, b);
					}
				}
				visitedNodesStack.pop();
			}
 
		}
	}
	private static void printNodesInStack(Stack<Integer> stk){
		ListIterator<Integer> itr=stk.listIterator();
		while(itr.hasNext()){
			System.out.print((int_to_node.get(itr.next()))+",");
		}
 
	}
 
 
}
