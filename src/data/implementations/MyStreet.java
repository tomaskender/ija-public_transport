package data.implementations;

import data.enums.StreetState;
import data.interfaces.*;
import utils.Math2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyStreet implements Street {
    private String id;
    private StreetState state = StreetState.CLOSED;
    private List<Stop> stops = new ArrayList<>();
    private List<Coordinate> coords = new ArrayList<>();

    public static Street CreateStreet(String id, Coordinate... coordinates) { return new MyStreet(id, coordinates); }

    public MyStreet(String id, Coordinate... coordinates) {
        this.id = id;
        coords = Arrays.asList(coordinates);
    }

    @Override
    public String getId() { return id; }

    @Override
    public List<Coordinate> getStreetPoints() { return coords; }

    @Override
    public Coordinate getBegin() { return coords.get(0); }

    @Override
    public Coordinate getEnd() { return coords.get(coords.size()-1); }

    @Override
    public List<Stop> getStops() { return stops; }

    @Override
    public boolean AddStopToStreet(Stop stop) {
        // is it located on the street
        boolean isOnStreet = false;
        if(coords.size() == 1) {
            isOnStreet = coords.get(0).equals(stop.getCoordinate());
        } else {
            for (int i = 1; i < coords.size(); i++) {
                Coordinate p1 = coords.get(i);
                Coordinate p2 = coords.get(i - 1);
                Coordinate pStop = stop.getCoordinate();
                if (Math2D.isLocatedBetweenPoints(pStop, p1, p2)) {
                    isOnStreet = true;
                    break;
                }
            }
        }
        if(isOnStreet == false) {
            return false;
        }

        if(!stops.contains(stop))
            stops.add(stop);
        return true;
    }

    @Override
    public boolean Follows(Street s) {
        return this.getBegin().equals(s.getBegin()) ||
                this.getBegin().equals(s.getEnd()) ||
                this.getEnd().equals(s.getBegin()) ||
                this.getEnd().equals(s.getEnd());
    }

    @Override
    public void SetStreetState(StreetState state) {
        this.state = state;
    }

    @Override
    public double getStreetStateModifier() { return state.getModifier(); }
}
