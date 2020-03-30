package sample;

import java.io.InputStream;
import javax.security.auth.login.Configuration;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import data.implementations.CONFIG;
import data.implementations.MyLine;
import data.implementations.MyStop;
import data.implementations.MyStreet;
import data.interfaces.Coordinate;
import data.interfaces.Line;
import data.interfaces.Stop;
import data.interfaces.Street;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.sql.Array;
import java.util.*;

public class XMLParser {

    public static void main(String[] args) {
        Map<String, Coordinate> stops = new HashMap<>();
        Map<String, List> streets = new HashMap<>();
        Map<String, List> links = new HashMap<>();
        Map<String, String> stops_on_streets = new HashMap<>();
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
                            stops_on_streets.put(stop.getTextContent(), stop.getAttribute("street"));
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
                    List<String> route;
                    List<List> attribs = new ArrayList<>();
                    List<String> link_timesList = new ArrayList<>();
                    route = Arrays.asList(element.getAttribute("route").split("\\s+"));
                    if(route.get(0) != "") {
                        attribs.add(route);
                    }
                    NodeList link_times = element.getElementsByTagName("link_t");
                    for(int j = 0; j < link_times.getLength(); j++){
                        Node node1 = link_times.item(j);
                        if(node1.getNodeType() == node1.ELEMENT_NODE){
                            Element link_time = (Element) node1;
                            link_timesList.add(link_time.getAttribute("start"));
                        }
                    }
                    attribs.add(link_timesList);
                    List<Map> links_stops = new ArrayList<>();
                    NodeList link_stops = element.getElementsByTagName("stop");
                    for(int k = 0; k < link_stops.getLength(); k++){
                        Node node1 = link_stops.item(k);
                        if(node1.getNodeType() == node1.ELEMENT_NODE){
                            Element link_stop = (Element) node1;
                            Map<String, String> stop = new HashMap<>();
                            stop.put(link_stop.getTextContent(),link_stop.getAttribute("time"));
                            links_stops.add(stop);
                        }
                    }
                    attribs.add(links_stops);
                    links.put(element.getTextContent().split("\\s+")[0], attribs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(String street_key : streets.keySet()){
            CONFIG.streets.put(street_key,MyStreet.CreateStreet(street_key, (Coordinate) streets.get(street_key).get(0), (Coordinate) streets.get(street_key).get(1)));
        }
        for(String stop_key : stops.keySet()){
            Stop stop = MyStop.CreateStop(stop_key, stops.get(stop_key));
            CONFIG.stops.put(stop_key, stop);
            for(String street_key : stops_on_streets.keySet()){
                if(stop_key.equals(street_key)){
                   Street street = CONFIG.streets.get(stops_on_streets.get(street_key));
                   stop.SetStreet(street);
                }
            }
        }
        for(String line_key : links.keySet()){
            Line line = MyLine.CreateLine(line_key);
            List<Map<String, Object>> line_stops = (List<Map<String, Object>>) links.get(line_key).get(2);
            for(Map<String, Object> map : line_stops){
                for(String line_stop_key : map.keySet()){
                    line.AddStop(CONFIG.stops.get(line_stop_key));
                }
            }
            CONFIG.lines.put(line_key, line);
        }
        System.out.print(CONFIG.streets);
        System.out.print("\n");
        System.out.print(CONFIG.stops);
        System.out.print("\n");
        System.out.print(CONFIG.lines);
        System.out.print("\n");
    }
}
