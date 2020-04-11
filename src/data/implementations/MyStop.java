package data.implementations;

import data.interfaces.*;

public class MyStop implements Stop {
    String id;
    Street street;
    Coordinate coord;

    public static Stop CreateStop(String id, Coordinate coord) { return new MyStop(id, coord); }

    public MyStop(String id, Coordinate coord) {
        this.id = id;
        this.coord = coord;
    }

    @Override
    public Street getStreet() {
        return street;
    }

    @Override
    public void SetStreet(Street s) {
        street = s;
        s.AddStopToStreet(this);
    }

    public String getId(){return id;}

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
