package data.interfaces;

import java.time.LocalTime;

import data.enums.VehicleState;
import data.implementations.MyVehicle;

public interface Vehicle {
    static Vehicle CreateVehicle(Line line, LocalTime start) { return MyVehicle.CreateVehicle(line, start); }
    Line getLine();
    LocalTime getStart();
    void Tick(long delta);
    VehicleState getState();
}
