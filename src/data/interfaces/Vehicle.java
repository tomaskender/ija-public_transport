package data.interfaces;

import java.time.LocalTime;
import java.util.List;

import data.enums.VehicleState;
import data.implementations.MyVehicle;

public interface Vehicle extends GUIMapElement {
    static Vehicle CreateVehicle(Line line, LocalTime start) { return MyVehicle.CreateVehicle(line, start); }
    Line getLine();
    LocalTime getStart();
    void Tick(long deltaInMillis);
    VehicleState getState();
    void AddRoute(Route route);
    List<Route> getRoutes();
}
