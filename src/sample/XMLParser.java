/*
 * Parse input xml file which contains the map and timetables.
 * Authors: Tomas Duris and Tomas Kender
 */
package sample;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import data.implementations.*;
import data.interfaces.Coordinate;
import data.interfaces.Line;
import data.interfaces.Stop;
import data.interfaces.Street;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.time.LocalTime;
import java.util.*;

import static data.interfaces.Coordinate.CreateCoordinate;
import static data.interfaces.Line.CreateLine;
import static data.interfaces.Stop.CreateStop;
import static data.interfaces.Street.CreateStreet;
import static data.interfaces.Vehicle.CreateVehicle;

//Parsing input XML into LinkedHashMaps to save order and ease of finding corresponding values
public class XMLParser {

    /**
     * Parsing input file
     * @param file input file
     */
    public static void main(File file) {
        //maps for saving data from XML input
        Map<String, Coordinate> stops = new LinkedHashMap<>();
        Map<String, List> streets = new LinkedHashMap<>();
        Map<String, List> links = new LinkedHashMap<>();
        Map<String, String> stops_on_streets = new LinkedHashMap<>();
        Map<String, Map<String, String>> line_stops = new LinkedHashMap<>();


        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            //parsing stops
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
                            //saving info about streets on which the stops lie
                            stops_on_streets.put(stop.getTextContent(), stop.getAttribute("street"));
                            //put info about coordinates into local struct
                            stops.put(stop.getTextContent(), CreateCoordinate(Integer.parseInt(stop.getAttribute("cord_X")), Integer.parseInt(stop.getAttribute("cord_Y"))));
                        }
                    }
                }
            }

            //parsing streets
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
                            List<Coordinate> coordinates = new ArrayList<>();
                            //saving info about street beginning and ending
                            coordinates.add(CreateCoordinate(Integer.parseInt(street.getAttribute("begin").split("\\s+")[0]), Integer.parseInt(street.getAttribute("begin").split("\\s+")[1])));
                            coordinates.add(CreateCoordinate(Integer.parseInt(street.getAttribute("end").split("\\s+")[0]), Integer.parseInt(street.getAttribute("end").split("\\s+")[1])));
                            //put streets with coordinates into local struct
                            streets.put(street.getTextContent(), coordinates);
                        }
                    }
                }
            }

            //parsing bus lines
            nList = doc.getElementsByTagName("link");
            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nNode;
                    List<String> route;
                    List<List> attribs = new ArrayList<>();
                    List<String> link_timesList = new ArrayList<>();
                    //saving route (streets in order in which they lie on route).
                    route = Arrays.asList(element.getAttribute("route").split("\\s+"));
                    if(!route.get(0).equals("")) {
                        attribs.add(route);
                    }
                    //saving bus line starting time
                    NodeList link_times = element.getElementsByTagName("link_t");
                    for(int j = 0; j < link_times.getLength(); j++){
                        Node node1 = link_times.item(j);
                        if(node1.getNodeType() == node1.ELEMENT_NODE){
                            Element link_time = (Element) node1;
                            link_timesList.add(link_time.getAttribute("start"));
                        }
                    }
                    //saving stops on route
                    attribs.add(link_timesList);
                    NodeList link_stops = element.getElementsByTagName("stop");
                    Map<String, String> stop = new LinkedHashMap<>();
                    for(int k = 0; k < link_stops.getLength(); k++){
                        Node node1 = link_stops.item(k);
                        if(node1.getNodeType() == node1.ELEMENT_NODE){
                            Element link_stop = (Element) node1;
                            //saving time of bus traveling between stops (time to get ro current stop)
                            stop.put(link_stop.getTextContent(),link_stop.getAttribute("time"));
                        }
                    }
                    //put bus lines into local struct
                    line_stops.put(element.getTextContent().split("\\s+")[0], stop);
                    links.put(element.getTextContent().split("\\s+")[0], attribs);
                }
            }
        //catch wrong input XML file
        } catch (Exception e) {
            System.out.print("Wrong input format");
            System.exit(1);
        }

        //put created streets into global CONFIG struct with all necessary info
        for(String street_key : streets.keySet()){
            CONFIG.streets.put(street_key, CreateStreet(street_key, (Coordinate) streets.get(street_key).get(0), (Coordinate) streets.get(street_key).get(1)));
        }
        //put created stops into global CONFIG struct with all necessary info
        for(String stop_key : stops.keySet()){
            Stop stop = CreateStop(stop_key, stops.get(stop_key));
            CONFIG.stops.put(stop_key, stop);
            for(String street_key : stops_on_streets.keySet()){
                if(stop_key.equals(street_key)){
                   Street street = CONFIG.streets.get(stops_on_streets.get(street_key));
                   stop.SetStreet(street);
                }
            }
        }
        //put created lines into global CONFIG struct with all necessary info
        for(String line_key : line_stops.keySet()){
            Line line = CreateLine(line_key);
            List route = (List)links.get(line_key).get(0);
            List times = (List)links.get(line_key).get(1);
            for(Object street : route){
                line.AddTraversalStreet(CONFIG.streets.get(street));
            }
            Map<String, String> stop = line_stops.get(line_key);
            for(String line_stop : stop.keySet()){
                line.AddStop(CONFIG.stops.get(line_stop), Integer.parseInt(stop.get(line_stop)));
            }
            CONFIG.lines.put(line_key, line);
            //Create vehicles on lines and but them into global CONFIG struct with all necessary info
            for(Object vehicle_time : times){
                CONFIG.vehicles.put(line_key + "_" + vehicle_time.toString(), CreateVehicle(CONFIG.lines.get(line_key), LocalTime.parse(vehicle_time.toString())));

            }
        }
    }
}
