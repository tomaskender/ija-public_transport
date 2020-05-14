package data.implementations;

import data.interfaces.*;

public class MyStop implements Stop {
    final String id;
    Street street;
    final Coordinate coord;

    /**
     * @brief stop instance constructor
     * @param id stop name
     * @param coord stop coordinates
     * @return new stop instance
     */
    public static Stop CreateStop(String id, Coordinate coord) { return new MyStop(id, coord); }

    /**
     * @brief initialize stop instance values
     * @param id stop name
     * @param coord stop coordinates (x and y)
     */
    public MyStop(String id, Coordinate coord) {
        this.id = id;
        this.coord = coord;
    }

    /**
     * @brief get info about street that stop lies on
     * @return street that stop lies on
     */
    @Override
    public Street getStreet() {
        return street;
    }

    /**
     * @brief set street that stop lies on
     * @param s given street instance
     */
    @Override
    public void SetStreet(Street s) {
        street = s;
        s.AddStopToStreet(this);
    }

    /**
     * @brief get stop name
     * @return stop name
     */
    public String getId(){return id;}

    /**
     * @brief get stop coordinates
     * @return stop coordinates
     */
    @Override
    public Coordinate getCoordinate() {
        return coord;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Stop) {
            return getId().equals(((Stop) o).getId()) && getCoordinate().equals(((Stop) o).getCoordinate());
        } else {
            return false;
        }
    }
}
