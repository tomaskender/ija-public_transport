package data.interfaces;

import data.implementations.MyStop;

/**
 * A stop on a vehicle route
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public interface Stop {
    /**
     * Create stop instance
     * @param id stop name
     * @param coord stop coordinates
     * @return new stop instance
     */
    static Stop CreateStop(String id, Coordinate coord) { return MyStop.CreateStop(id, coord); }

    /**
     * Get info about street that stop lies on
     * @return street that stop lies on
     */
    Street getStreet();

    /**
     * Get stop name
     * @return stop name
     */
    String getId();

    /**
     * Set street that stop lies on
     * @param s given street instance
     */
    void SetStreet(Street s);

    /**
     * Get stop coordinates
     * @return stop coordinates
     */
    Coordinate getCoordinate();
}
