package data.implementations;

import data.interfaces.*;
import data.enums.VehicleState;
import javafx.scene.paint.Color;
import utils.Math2D;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyVehicle implements Vehicle, GUIMapElement {
    Line line;
    LocalTime start;
    double progressTowardsNextStop;
    double stopTime=0;
    VehicleState state;
    List<Route> routes = new ArrayList<>();
    int currRouteIndex = 0;

    /**
     * @brief create vehicle instance
     * @param line given line instance
     * @param start start time of vehicle
     * @return vehicle instance
     */
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

    /**
     * @brief get line on which vehicle will travel
     * @return line on which vehicle will travel
     */
    @Override
    public Line getLine() {
        return line;
    }

    /**
     * @brief get start time of vehicle
     * @return start time
     */
    @Override
    public LocalTime getStart() {
        return start;
    }

    /**
     * @brief set state of vehicle
     * @param state of vehicle
     */
    private void SetState(VehicleState state) {
        this.state = state;
    }

    /**
     * @brief get vehicle state
     * @return vehicle state
     */
    @Override
    public VehicleState getState() {
        return state;
    }

    /**
     * @brief update position of vehicle in time
     * @return vehicle position
     */
    @Override
    public void Tick(long deltaInMillis) {
        switch (state) {
            case INACTIVE:
                if(CONFIG.CURRENT_TIME.compareTo(start) >= 0 && CONFIG.CURRENT_TIME.compareTo(start.plus(deltaInMillis, ChronoUnit.MILLIS)) < 0)
                    SetState(VehicleState.MOVING);
                break;
            case MOVING:
                Route currRoute = routes.get(currRouteIndex);
                double streetModifier = currRoute.getRoute().get(0).getStreet().getStreetState().getModifier();
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

    /**
     * @brief get current position of vehicle
     * @param progressToNextStop travel distance to next stop
     * @return position of vehicle
     */
    private Coordinate getPosition(double progressToNextStop) {
        List<PointInPath> coords = routes.get(currRouteIndex).getRoute();
        // get total route length
        double totalDistance = Math2D.getRouteLength(routes.get(currRouteIndex));

        double distanceSum = 0;
        for(int i=0; i<coords.size()-1;i++) {
            Coordinate coord1 = coords.get(i).getCoordinate();
            Coordinate coord2 = coords.get(i+1).getCoordinate();

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

    /**
     * @brief add route for this vehicle
     * @param  route new route
     */
    @Override
    public void AddRoute(Route route) {
        routes.add(route);
    }


    /**
     * @brief edit route for this vehicle
     * @param index in current route
     * @param route new route
     */
    @Override
    public void EditRouteAndNormalizeProgress(int index, Route route) {
        Route editedRoute = routes.get(index);
        double currDistance = Math2D.getRouteLength(editedRoute);

        // some vehicles are already traversing the currently edited route
        // we need to change only the part of the route they haven't crossed yet
        if(route.getRoute().get(0) != getRoutes().get(index).getRoute().get(0)) {
            // get the connecting point between two routes
            int connIndex = editedRoute.getRoute().indexOf(route.getRoute().get(0));
            route.getRoute().addAll(0, editedRoute.getRoute().subList(0, connIndex));

            double shareOfPartBeforeConnectionPoint = Math2D.getRouteLength(route, 0, connIndex)/currDistance;
            route.SetExpectedDeltaTime(editedRoute.getExpectedDeltaTime()*shareOfPartBeforeConnectionPoint+route.getExpectedDeltaTime());
        }

        if(currRouteIndex == index) {
            // adjust currently active routes progress so that the vehicle remains in the same position in getPosition
            double newDistance = Math2D.getRouteLength(route);
            progressTowardsNextStop = progressTowardsNextStop * currDistance/newDistance;
        }
        routes.set(index, route);
    }

    /**
     * @brief remove route from this vehicle
     * @param index route on index to remove
     */
    @Override
    public void RemoveRoute(int index) {
        routes.remove(index);
    }

    /**
     * @brief get vehicle route
     * @return vector of vehicle routes
     */
    @Override
    public List<Route> getRoutes() { return new ArrayList<>(routes); }

    /**
     * @brief get last point before coordinate
     * @param street given street
     * @param coord given coordinate
     * @return last point
     */
    @Override
    public PointInPath getLastRoutePointBeforeCoordinate(Street street, Coordinate coord) {
        for(int r_index = currRouteIndex; r_index<=routes.size()-1; r_index++) {
            Route r = routes.get(r_index);
            for(int i=0; i<r.getRoute().size()-1; i++) {
                PointInPath p1 = r.getRoute().get(i);
                PointInPath p2 = r.getRoute().get(i+1);

                if(p1.getStreet().getId() == street.getId()) {
                    if(Math2D.isLocatedBetweenPoints(coord, p1.getCoordinate(), p2.getCoordinate())) {
                        if(r_index != currRouteIndex) {
                            return new PointInPath(r, p1.getStreet(), p1.getCoordinate());
                        } else {
                            Coordinate vehiclePos = getPosition(progressTowardsNextStop);
                            if(Math2D.isLocatedBetweenPoints(vehiclePos, p1.getCoordinate(), coord)) {
                                PointInPath currVehiclePoint = new PointInPath(r, p1.getStreet(), vehiclePos);
                                r.getRoute().add(i+1, currVehiclePoint);

                                return currVehiclePoint;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @brief get color of line
     * @return color of line
     */
    @Override
    public Color getNormalColor() { return getLine().getMapColor(); }

    /**
     * @brief get highlighted color of line
     * @return color of line
     */
    @Override
    public Color getHighlightedColor() { return getLine().getMapColor(); }
}
