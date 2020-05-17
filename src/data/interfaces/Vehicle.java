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
    /**
     * Create vehicle instance
     * @param line given line instance
     * @param start start time of vehicle
     * @return vehicle instance
     */
    static Vehicle CreateVehicle(Line line, LocalTime start) { return MyVehicle.CreateVehicle(line, start); }

    /**
     * Get line on which vehicle will travel
     * @return line on which vehicle will travel
     */
    Line getLine();

    /**
     * Get start time of vehicle from first stop
     * @return start time
     */
    LocalTime getStart();

    /**
     * Update position of vehicle in time
     * @param deltaInMillis delta between two ticks
     */
    void Tick(long deltaInMillis);

    /**
     * Get vehicle state
     * @return vehicle state
     */
    VehicleState getState();

    /**
     * Add route for this vehicle
     * @param  route new route
     */
    void AddRoute(Route route);

    /**
     * Edit route for this vehicle
     * @param index in current route
     * @param route new route
     */
    void EditRouteAndNormalizeProgress(int index, Route route);

    /**
     * Remove route from this vehicle
     * @param index route on index to remove
     */
    void RemoveRoute(int index);

    /**
     * Get vehicle route
     * @return vector of vehicle routes
     */
    List<Route> getRoutes();

    /**
     * Get last point before coordinate
     * @param street given street
     * @param coord given coordinate
     * @return last point
     */
    PointInPath getLastRoutePointBeforeCoordinate(Street street, Coordinate coord);
}
