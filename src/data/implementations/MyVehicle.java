package data.implementations;

import data.interfaces.Line;
import data.interfaces.Vehicle;
import data.enums.VehicleState;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class MyVehicle implements Vehicle {
    Line line;
    LocalTime start;
    double progressTowardsNextStop;
    double stopTime=0;
    VehicleState state;
    //TODO: object representation on the map

    public static Vehicle CreateVehicle(Line line, LocalTime start) {
        if(line != null && start != null) {
            Vehicle v = new MyVehicle(line, start);
            return line.AddVehicleToLine(v) ? v : null;
        } else {
            return null;
        }
    }

    public MyVehicle(Line line, LocalTime start) {
        this.line = line;
        this.start = start;
        this.state = VehicleState.INACTIVE;
    }

    @Override
    public Line getLine() {
        return line;
    }

    @Override
    public LocalTime getStart() {
        return start;
    }

    private void SetState(VehicleState state) {
        this.state = state;
    }

    @Override
    public VehicleState getState() {
        return state;
    }

    @Override
    public void Tick(long delta) {
        switch (state) {
            case INACTIVE:
                if(CONFIG.CURRENT_TIME.compareTo(start) >= 0 && CONFIG.CURRENT_TIME.compareTo(start.plus(delta, ChronoUnit.SECONDS)) < 0)
                    SetState(VehicleState.MOVING);
                break;
            case STOPPED:
                stopTime += delta;
                if (stopTime >= CONFIG.EXPECTED_STOP_TIME) {
                    stopTime = 0;
                    SetState(VehicleState.MOVING);
                }
                break;
            case MOVING:
                //TODO: progressTowardsNextStop += delta * curr_street_state_factor / expected_travel_time;
                //TODO: set position
                //TODO: set curr street
                if(progressTowardsNextStop >= 1) {
                    SetState(VehicleState.STOPPED);
                    //TODO: set next stop as current one
                    progressTowardsNextStop = 0.0;
                }
                break;
        }
    }
}
