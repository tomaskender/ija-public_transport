package data.interfaces;

import data.implementations.MyLine;
import javafx.scene.paint.Color;

public interface Line {
    static Line CreateLine(String id) { return MyLine.CreateLine(id); }
    boolean AddTraversalStreet(Street s);
    boolean AddStop(Stop s, int delta);
    boolean AddVehicleToLine(Vehicle v);
    Color getMapColor();
}
