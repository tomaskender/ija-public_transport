package sample;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParser {

    public static void main(String[] args) {
        Map<String, List> stops = new HashMap<String, List>();
        try {
            InputStream input = XMLParser.class.getResourceAsStream("source.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(input);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("def_stops");
            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nNode;
                    NodeList stopList = element.getElementsByTagName("def_stop");

                    for(int j = 0; j < stopList.getLength(); j++){
                        Node node1 = stopList.item(j);
                        if(node1.getNodeType() == node1.ELEMENT_NODE){
                            Element stop = (Element) node1;
                            List<String> coordinates = new ArrayList<String>();
                            coordinates.add(stop.getAttribute("cord_X"));
                            coordinates.add(stop.getAttribute("cord_Y"));
                            stops.put(stop.getTextContent(), coordinates);
                        }
                    }
                    System.out.print(stops);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
