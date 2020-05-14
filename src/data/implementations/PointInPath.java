package data.implementations;

import data.interfaces.Coordinate;
import data.interfaces.Route;
import data.interfaces.Street;

public class PointInPath {
    final Coordinate point;
    final Street street;
    final Route route;

    /**
     * @brief Point in path of vehicle route
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
     * @brief get coordinates
     * @return coordinates
     */
    public Coordinate getCoordinate() { return point; }

    /**
     * @brief get street
     * @return street
     */
    public Street getStreet() { return street; }

    /**
     * @brief get route
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
