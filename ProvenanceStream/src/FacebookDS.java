/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
/**
 *
 * @author rahman
 */
public class FacebookDS {
    public static void main( String[] args ) throws IOException
    {
    	Graph graph = new SingleGraph("FacebookGraph");
    	File file = new File("/home/rahman/NetBeansProjects/facebookDataset.txt");
    	String edgenames[][]=new String[88234][2];
    	readFile(file,edgenames);
    	System.out.println("Retreival Done");
    	for(int i=0;i<4039;i++)
    	{
    		graph.addNode("node"+i);
    	}
    	for(int i=0;i<88234;i++)
    	{
    		String edgeTitle="e"+i;
    		String nodeA="node"+edgenames[i][0];
    		String nodeB="node"+edgenames[i][1];
    		graph.addEdge(edgeTitle,nodeB,nodeA);
    	}
    	graph.display();
    	ConnectedComponents cc = new ConnectedComponents();
		cc.init(graph);
		System.out.printf("%d connected component(s) in this graph",cc.getConnectedComponentsCount());

    }
    private static void readFile(File fin,String edgenames[][]) throws IOException {
		FileInputStream fis = new FileInputStream(fin);

		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		int p=0;
		while ((line = br.readLine()) != null){
			//System.out.println(line);
			StringTokenizer st=new StringTokenizer(line);
			int k=0;
			String starr[]=new String[2];
			while(st.hasMoreTokens())
			{
				starr[k++]=st.nextToken();
			}
			edgenames[p][0]=starr[0];
			edgenames[p][1]=starr[1];
			p++;
		}
		br.close();
	}
}
