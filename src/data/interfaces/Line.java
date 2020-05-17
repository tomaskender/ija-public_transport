/*
 * Transport line with it's associated vehicles
 * Authors: Tomas Duris and Tomas Kender
 */
package data.interfaces;

import data.implementations.MyLine;
import javafx.scene.paint.Color;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Line {
    /*
     * Pool of colors to be assigned to lines
     */
    List<Color> mapColors = new ArrayList<>(Arrays.asList(Color.GREEN, Color.BLUE, Color.RED, Color.ORANGE, Color.PURPLE, Color.BROWN, Color.PINK, Color.GREY, Color.LIME, Color.LIGHTBLUE));

    /**
     * Create line instance with name and color
     * @param id line identifier
     * @return line instance
     */
    static Line CreateLine(String id) { return MyLine.CreateLine(id); }

    /**
     * Get line identifier
     * @return line identifier
     */
    String getId();

    /**
     * Get all stops line vehicles have to cross
     * @return List of stops and times it takes to get to them from previous stop
     */
    List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStops();

    /**
     * Add a street that vehicles will have to traverse on their way
     * @param street street to be added to line
     * @return true if street was successfully added, otherwise false
     */
    boolean AddTraversalStreet(Street street);

    /**
     * Add given stop with name and time to get there on line
     * @param stop given stop
     * @param delta time to get to sto
     */
    void AddStop(Stop stop, int delta);

    /**
     * Add vehicle instance to line
     * @param v vehicle instance to be added to line
     * @return true if could be added, otherwise false
     */
    boolean AddVehicleToLine(Vehicle v);

    /**
     * Get color of this line
     * @return color value
     */
    Color getMapColor();
}
