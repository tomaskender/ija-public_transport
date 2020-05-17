/*
 * A point in the path of a vehicle. Contains information about the coordinate, street and route of a vehicle
 * Authors: Tomas Duris and Tomas Kender
 */
package data.implementations;

import data.interfaces.Coordinate;
import data.interfaces.Route;
import data.interfaces.Street;

public class PointInPath {
    final Coordinate point;
    final Street street;
    final Route route;

    /**
     * Initialize point in path instance
     * @param route route of vehicle
     * @param street current street
     * @param point coordinates of point
     */
    public PointInPath(Route route, Street street, Coordinate point) {
        this.point = point;
        this.street = street;
        this.route = route;
    }

    /**
     * Get coordinates
     * @return coordinates
     */
    public Coordinate getCoordinate() { return point; }

    /**
     * Get street
     * @return street
     */
    public Street getStreet() { return street; }

    /**
     * Get route
     * @return route
     */
    public Route getRoute() { return route; }

    @Override
    public int hashCode() {
        return getCoordinate().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PointInPath)
            return getCoordinate().equals(((PointInPath) o).getCoordinate());
        return false;
    }
}
