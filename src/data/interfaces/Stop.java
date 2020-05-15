/*
 * A stop on a vehicle route
 * Authors: Tomas Duris and Tomas Kender
 */
package data.interfaces;

import data.implementations.MyStop;

public interface Stop {
    static Stop CreateStop(String id, Coordinate coord) { return MyStop.CreateStop(id, coord); }
    Street getStreet();
    String getId();
    void SetStreet(Street s);
    Coordinate getCoordinate();
}
