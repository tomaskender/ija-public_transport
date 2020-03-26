package data.implementations;

import data.interfaces.*;

public class MyStop implements Stop {
    String id;
    Street street;
    Coordinate coord;

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

    @Override
    public Coordinate getCoordinate() {
        return coord;
    }
}
