package data.implementations;

import data.interfaces.Coordinate;
import data.interfaces.Route;
import data.interfaces.Street;

public class PointInPath {
    Coordinate point;
    Street street;
    Route route;

    public PointInPath(Route route, Street street, Coordinate point) {
        this.point = point;
        this.street = street;
        this.route = route;
    }

    public Coordinate getCoordinate() { return point; }
    public Street getStreet() { return street; }
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
