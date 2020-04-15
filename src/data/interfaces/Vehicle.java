package data.interfaces;

import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.List;

import data.enums.VehicleState;
import data.implementations.MyVehicle;
import data.implementations.PointInPath;

public interface Vehicle extends GUIMapElement {
    static Vehicle CreateVehicle(Line line, LocalTime start) { return MyVehicle.CreateVehicle(line, start); }
    Line getLine();
    LocalTime getStart();
    void Tick(long deltaInMillis);
    VehicleState getState();
    void AddRoute(Route route);
    List<Route> getRoutes();
    PointInPath getLastRoutePointBeforeCoordinate(Street street, Coordinate coord);
}
