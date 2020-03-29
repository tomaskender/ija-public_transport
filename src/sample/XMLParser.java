package sample;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import data.interfaces.Coordinate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.*;

public class XMLParser {

    public static void main(String[] args) {
        Map<String, Coordinate> stops = new HashMap<>();
        Map<String, List> streets = new HashMap<>();
        Map<String, List> links = new HashMap<>();
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
                            stops.put(stop.getTextContent(), Coordinate.CreateCoordinate(Integer.parseInt(stop.getAttribute("cord_X")), Integer.parseInt(stop.getAttribute("cord_Y"))));
                        }
                    }
                }
            }
            nList = doc.getElementsByTagName("streets");
            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nNode;
                    NodeList streetList = element.getElementsByTagName("street");

                    for(int j = 0; j < streetList.getLength(); j++){
                        Node node1 = streetList.item(j);
                        if(node1.getNodeType() == node1.ELEMENT_NODE){
                            Element street = (Element) node1;
                            List<String> begin_end = new ArrayList<>();
                            List<Coordinate> coordinates = new ArrayList<>();
                            coordinates.add(Coordinate.CreateCoordinate(Integer.parseInt(street.getAttribute("begin").split("\\s+")[0]), Integer.parseInt(street.getAttribute("begin").split("\\s+")[1])));
                            coordinates.add(Coordinate.CreateCoordinate(Integer.parseInt(street.getAttribute("end").split("\\s+")[0]), Integer.parseInt(street.getAttribute("end").split("\\s+")[1])));
                            streets.put(street.getTextContent(), coordinates);
                        }
                    }
                }
            }
            nList = doc.getElementsByTagName("link");
            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nNode;
                    List<String> route = new ArrayList<>();
                    route = Arrays.asList(element.getAttribute("route").split("\\s+"));
                    if(route.get(0) != "") {
                        links.put(element.getTextContent().split("\\s+")[0], route);
                    }
                }
            }
            System.out.print(stops);
            System.out.print(streets);
            System.out.print(links);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
