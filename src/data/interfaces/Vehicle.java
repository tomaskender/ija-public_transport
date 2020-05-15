/*
 * A vehicle that belongs to certain line. It's movement is controlled by the state it is currently in.
 * Authors: Tomas Duris and Tomas Kender
 */
package data.interfaces;

import java.time.LocalTime;
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
    void EditRouteAndNormalizeProgress(int index, Route route);
    void RemoveRoute(int index);
    List<Route> getRoutes();
    PointInPath getLastRoutePointBeforeCoordinate(Street street, Coordinate coord);
}
