package data.implementations;

import data.interfaces.*;
import data.enums.VehicleState;
import javafx.scene.paint.Color;
import utils.Math2D;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MyVehicle implements Vehicle, GUIMapElement {
    Line line;
    LocalTime start;
    double progressTowardsNextStop;
    double stopTime=0;
    VehicleState state;
    List<Route> routes = new ArrayList<>();
    int currRouteIndex = 0;

    public static Vehicle CreateVehicle(Line line, LocalTime start) {
        if(line != null && start != null) {
            Vehicle v = new MyVehicle(line, start);
            if(!line.AddVehicleToLine(v))
                return null;
            return v;
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
    public void Tick(long deltaInMillis) {
        switch (state) {
            case INACTIVE:
                if(CONFIG.CURRENT_TIME.compareTo(start) >= 0 && CONFIG.CURRENT_TIME.compareTo(start.plus(deltaInMillis, ChronoUnit.MILLIS)) < 0)
                    SetState(VehicleState.MOVING);
                break;
            case MOVING:
                Route currRoute = routes.get(currRouteIndex);
                double streetModifier = currRoute.getRoute().get(0).getKey().getStreetState().getModifier();
                double deltaInSecs = (double)deltaInMillis/1000;
                progressTowardsNextStop += deltaInSecs * streetModifier / currRoute.getExpectedDeltaTime();
                if(progressTowardsNextStop >= 1) {
                    currRouteIndex++;
                    SetState(VehicleState.STOPPED);
                    progressTowardsNextStop = 0.0;
                }
                if(currRouteIndex < routes.size())
                    CONFIG.controller.SetVehicle(this, getPosition(progressTowardsNextStop));
                break;
            case STOPPED:
                stopTime += (double)deltaInMillis/1000;
                if (stopTime >= CONFIG.EXPECTED_STOP_TIME) {
                    stopTime = 0;
                    if(currRouteIndex < routes.size()) {
                        SetState(VehicleState.MOVING);
                    } else {
                        SetState(VehicleState.INACTIVE);
                        CONFIG.controller.RemoveVehicle(this);
                    }
                }
                break;
        }
    }

    private Coordinate getPosition(double progressToNextStop) {
        List<AbstractMap.SimpleImmutableEntry<Street,Coordinate>> coords = routes.get(currRouteIndex).getRoute();
        // get total route length
        double totalDistance = 0;
        for(int i=0; i<coords.size()-1;i++) {
            totalDistance += Math2D.getDistanceBetweenPoints(coords.get(i).getValue(), coords.get(i+1).getValue());
        }

        double distanceSum = 0;
        for(int i=0; i<coords.size()-1;i++) {
            Coordinate coord1 = coords.get(i).getValue();
            Coordinate coord2 = coords.get(i+1).getValue();

            double pointsDistance = Math2D.getDistanceBetweenPoints(coord1, coord2);

            double progressToNextPoint = (distanceSum+pointsDistance)/totalDistance;
            if(progressToNextPoint > progressToNextStop) {
                double progressToLastPoint = (distanceSum/totalDistance);
                double progressFromLastPointToCurrPos = progressToNextStop - progressToLastPoint;
                double progressFromLastPointToNextPoint = progressToNextPoint - progressToLastPoint;
                double biasToCoord2 = progressFromLastPointToCurrPos / progressFromLastPointToNextPoint;
                return Coordinate.CreateCoordinate((int)(coord1.getX() * (1-biasToCoord2) + coord2.getX() * (biasToCoord2)),
                                                    (int)(coord1.getY() * (1-biasToCoord2) + coord2.getY() * (biasToCoord2)));
            }

            distanceSum += pointsDistance;
        }

        return null;
    }

    @Override
    public void AddRoute(Route route) {
        routes.add(route);
    }

    @Override
    public List<Route> getRoutes() { return routes; }

    @Override
    public Color getNormalColor() { return getLine().getMapColor(); }

    @Override
    public Color getHighlightedColor() { return getLine().getMapColor(); }
}
