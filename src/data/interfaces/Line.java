package data.interfaces;

import data.implementations.MyLine;
import javafx.scene.paint.Color;

import java.util.AbstractMap;
import java.util.List;

public interface Line {
    static Line CreateLine(String id) { return MyLine.CreateLine(id); }
    String getId();
    List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStops();
    boolean AddTraversalStreet(Street s);
    boolean AddStop(Stop s, int delta);
    boolean AddVehicleToLine(Vehicle v);
    Color getMapColor();
}
