package data.interfaces;

import data.implementations.MyLine;

public interface Line {
    static Line CreateLine(String id) { return MyLine.CreateLine(id); }
    boolean AddTraversalStreet(Street s);
    boolean AddStop(Stop s);
    boolean AddVehicleToLine(Vehicle v);
}
