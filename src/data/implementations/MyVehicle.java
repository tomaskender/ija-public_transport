package data.implementations;

import data.enums.StreetState;
import data.interfaces.*;
import data.enums.VehicleState;
import javafx.scene.paint.Color;
import utils.Math2D;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * A vehicle that belongs to certain line. It's movement is controlled by the state it is currently in.
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public class MyVehicle implements Vehicle, GUIMapElement {
    final Line line;
    final LocalTime start;
    double progressTowardsNextStop;
    long stopTimeInMillis=0;
    VehicleState state;
    final List<Route> routes = new ArrayList<>();
    int currRouteIndex = 0;

    /**
     * Create vehicle instance
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

    /**
     * Initialize vehicle instance
     * @param line given line instance
     * @param start start time of vehicle
     */
    public MyVehicle(Line line, LocalTime start) {
        this.line = line;
        this.start = start;
        this.state = VehicleState.INACTIVE;
    }

    /**
     * Get line on which vehicle will travel
     * @return line on which vehicle will travel
     */
    @Override
    public Line getLine() {
        return line;
    }

    /**
     * Get start time of vehicle from first stop
     * @return start time
     */
    @Override
    public LocalTime getStart() {
        return start;
    }

    /**
     * Set state of vehicle
     * @param state of vehicle
     */
    private void SetState(VehicleState state) {
        this.state = state;
    }

    /**
     * Get vehicle state
     * @return vehicle state
     */
    @Override
    public VehicleState getState() {
        return state;
    }

    /**
     * Update position of vehicle in time
     * @param deltaInMillis delta between two ticks
     */
    @Override
    public void Tick(long deltaInMillis) {
        long wastedDeltaInMillis = 0;
        switch (state) {
            case INACTIVE:
                if(CONFIG.CURRENT_TIME.compareTo(start) >= 0 && CONFIG.CURRENT_TIME.compareTo(start.plus(deltaInMillis, ChronoUnit.MILLIS)) < 0) {
                    currRouteIndex = 0;
                    SetState(VehicleState.MOVING);
                    wastedDeltaInMillis = CONFIG.CURRENT_TIME.minusNanos(start.toNanoOfDay()).toNanoOfDay()/1000000;
                }
                break;
            case MOVING:
                Route currRoute = routes.get(currRouteIndex);

                // get current street modifier
                double streetModifier = StreetState.LOW.getModifier();
                for(int i=0; i<currRoute.getRoute().size(); i++) {
                    if(Math2D.getRouteLength(currRoute, 0, i) / Math2D.getRouteLength(currRoute) > progressTowardsNextStop)
                        break;
                    streetModifier = currRoute.getRoute().get(i).getStreet().getStreetState().getModifier();
                }

                progressTowardsNextStop += deltaInMillis * streetModifier / (currRoute.getExpectedDeltaTime()*1000);
                // vehicle arrived to next stop
                if(progressTowardsNextStop >= 1) {
                    wastedDeltaInMillis = (long)((progressTowardsNextStop-1)*currRoute.getExpectedDeltaTime()/streetModifier)*1000;
                    SetState(VehicleState.STOPPED);
                }
                if(currRouteIndex < routes.size())
                    CONFIG.controller.SetVehicle(this, getPosition(progressTowardsNextStop));
                break;
            case STOPPED:
                stopTimeInMillis += deltaInMillis;
                // after arriving at a stop, wait for passenger to get off
                if (stopTimeInMillis >= CONFIG.EXPECTED_STOP_TIME*1000) {
                    wastedDeltaInMillis = stopTimeInMillis-(long)CONFIG.EXPECTED_STOP_TIME*1000;
                    stopTimeInMillis = 0;
                    progressTowardsNextStop = 0.0;
                    currRouteIndex++;

                    if(currRouteIndex < routes.size()) {
                        // start new route
                        SetState(VehicleState.MOVING);
                    } else {
                        // if there is no route left, delete the vehicle and wait for next day
                        SetState(VehicleState.INACTIVE);
                        CONFIG.controller.RemoveVehicle(this);
                    }
                }
                break;
        }

        // when the delta is set to a high number, we might experience situations where one delta is enough to satisfy more states in a row in a single tick
        if(wastedDeltaInMillis > 0) {
            Tick(wastedDeltaInMillis);
        }
    }

    /**
     * Get current position of vehicle
     * @param progressToNextStop travel distance to next stop
     * @return position of vehicle
     */
    private Coordinate getPosition(double progressToNextStop) {
        List<PointInPath> coords = routes.get(currRouteIndex).getRoute();
        // get total route length
        double totalDistance = Math2D.getRouteLength(routes.get(currRouteIndex));

        if(progressTowardsNextStop > 0.99) {
            return coords.get(coords.size()-1).getCoordinate();
        } else {
            double distanceSum = 0;
            for (int i = 0; i < coords.size() - 1; i++) {
                Coordinate coord1 = coords.get(i).getCoordinate();
                Coordinate coord2 = coords.get(i + 1).getCoordinate();

                double pointsDistance = Math2D.getDistanceBetweenPoints(coord1, coord2);

                double progressToNextPoint = (distanceSum + pointsDistance) / totalDistance;
                if (progressToNextPoint > progressToNextStop) {
                    double progressToLastPoint = (distanceSum / totalDistance);
                    double progressFromLastPointToCurrPos = progressToNextStop - progressToLastPoint;
                    double progressFromLastPointToNextPoint = progressToNextPoint - progressToLastPoint;
                    double biasToCoord2 = progressFromLastPointToCurrPos / progressFromLastPointToNextPoint;
                    return Coordinate.CreateCoordinate((int) (coord1.getX() * (1 - biasToCoord2) + coord2.getX() * (biasToCoord2)),
                            (int) (coord1.getY() * (1 - biasToCoord2) + coord2.getY() * (biasToCoord2)));
                }

                distanceSum += pointsDistance;
            }
        }

        return null;
    }

    /**
     * Add route for this vehicle
     * @param  route new route
     */
    @Override
    public void AddRoute(Route route) {
        routes.add(route);
    }


    /**
     * Edit route for this vehicle
     * @param index in current route
     * @param route new route
     */
    @Override
    public void EditRouteAndNormalizeProgress(int index, Route route) {
        Route editedRoute = routes.get(index);
        double currDistance = Math2D.getRouteLength(editedRoute);

        // some vehicles are already traversing the currently edited route
        // we need to change only the part of the route they haven't crossed yet
        if(route.getRoute().get(0) != editedRoute.getRoute().get(0)) {
            // find index where the start of alt route is connecting to old route
            int connIndex = editedRoute.getRoute().indexOf(route.getRoute().get(0));
            route.getRoute().addAll(0, editedRoute.getRoute().subList(0, connIndex));

            // normalize time it takes to go from stop to stop
            double shareOfPartBeforeConnectionPoint = Math2D.getRouteLength(route, 0, connIndex)/currDistance;
            route.SetExpectedDeltaTime(editedRoute.getExpectedDeltaTime()*shareOfPartBeforeConnectionPoint+route.getExpectedDeltaTime());
        }

        // vehicle is already going on this route- path progress needs to be normalized
        if(currRouteIndex == index) {
            // adjust currently active routes progress so that the vehicle remains in the same position in getPosition
            double newDistance = Math2D.getRouteLength(route);
            progressTowardsNextStop = progressTowardsNextStop * currDistance/newDistance;
        }
        routes.set(index, route);
    }

    /**
     * Remove route from this vehicle
     * @param index route on index to remove
     */
    @Override
    public void RemoveRoute(int index) {
        routes.remove(index);
    }

    /**
     * Get vehicle route
     * @return vector of vehicle routes
     */
    @Override
    public List<Route> getRoutes() { return new ArrayList<>(routes); }

    /**
     * Get last point before coordinate
     * @param street given street
     * @param coord given coordinate
     * @return last point
     */
    @Override
    public PointInPath getLastRoutePointBeforeCoordinate(Street street, Coordinate coord) {
        for(int r_index = currRouteIndex; r_index<=routes.size()-1; r_index++) {
            Route r = routes.get(r_index);

            // iterate through the route
            for(int i=0; i<r.getRoute().size()-1; i++) {
                PointInPath p1 = r.getRoute().get(i);
                PointInPath p2 = r.getRoute().get(i+1);

                if(p1.getStreet().getId().equals(street.getId())) {
                    if(Math2D.isLocatedBetweenPoints(coord, p1.getCoordinate(), p2.getCoordinate())) {
                        if(r_index != currRouteIndex) {
                            // return last route point that is already a part of the street
                            return new PointInPath(r, p1.getStreet(), p1.getCoordinate());
                        } else {
                            // returned route point is the vehicle's position
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
     * Get color of line
     * @return color of line
     */
    @Override
    public Color getNormalColor() { return getLine().getMapColor(); }

    /**
     * Get highlighted color of line
     * @return color of line
     */
    @Override
    public Color getHighlightedColor() { return getLine().getMapColor(); }
}
