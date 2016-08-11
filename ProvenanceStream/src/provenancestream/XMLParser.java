/*
 * Script to conditions specified in constraints.xml
 */
package provenancestream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;
/**
 *
 * @author rahman
 */
public class XMLParser {
    public static void main(String[] args)throws IOException{
    try {

	File fXmlFile = new File("/home/rahman/NetBeansProjects/rahman-project/ProvenanceStream/constraints.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
	doc.getDocumentElement().normalize();

	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

	NodeList nList = doc.getElementsByTagName("node");

	System.out.println("----------------------------");

	for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);

		System.out.println("\nCurrent Element :" + nNode.getNodeName());

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;

			System.out.println("Node Type : " + eElement.getElementsByTagName("nodeType").item(0).getTextContent());
			System.out.println("Owner Type : " + eElement.getElementsByTagName("ownerType").item(0).getTextContent());
			System.out.println("Status : " + eElement.getElementsByTagName("status").item(0).getTextContent());
			System.out.println("TimeStamp : " + eElement.getElementsByTagName("timestamp").item(0).getTextContent());

		}
	}
    } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
    }
  }
    }
    