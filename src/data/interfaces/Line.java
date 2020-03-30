package data.interfaces;

import data.implementations.MyLine;

import java.util.AbstractMap;
import java.util.List;

public interface Line {
    static Line CreateLine(String id) { return MyLine.CreateLine(id); }
    boolean AddTraversalStreet(Street s);
    boolean AddStop(Stop s);
    boolean AddVehicleToLine(Vehicle v);
    void ConstructRoute();
    List<AbstractMap.SimpleImmutableEntry<Street,Stop>> getRoute();
}
