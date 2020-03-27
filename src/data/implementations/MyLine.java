package data.implementations;

import data.interfaces.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MyLine implements Line {
    private String id;
    private List<Street> streets = new ArrayList<>();
    private List<Stop> stops = new ArrayList<>();

    public static Line CreateLine(String id) { return new MyLine(id); }

    public MyLine(String id) {
        this.id = id;
    }

    @Override
    public boolean AddStop(Stop stop) {
        // is the street neighboring any existing streets?
        if(AddTraversalStreet(stop.getStreet()) == false)
            return false;
        if(stop.getStreet().AddStopToStreet(stop) == false)
            return false;
        stops.add(stop);
        return true;
    }

    @Override
    public boolean AddTraversalStreet(Street street) {
        boolean follows = false;
        if(streets.size() == 0) {
            follows = true;
        } else {
            for (Street s : streets) {
                if (s.Follows(street)) {
                    follows = true;
                    break;
                }
            }
        }
        if(follows == false) {
            return false;
        }
        if(!streets.contains(street))
            streets.add(street);
        return true;
    }

    @Override
    public List<AbstractMap.SimpleImmutableEntry<Street,Stop>> getRoute() {
        ArrayList<AbstractMap.SimpleImmutableEntry<Street,Stop>> list = new ArrayList<AbstractMap.SimpleImmutableEntry<Street,Stop>>();
        for(Street street:streets) {
            boolean found = false;
            for(Stop stop:stops) {
                if(stop.getStreet() == street) {
                    list.add(new AbstractMap.SimpleImmutableEntry<>(street, stop));
                    found = true;
                }
            }
            if(!found)
                list.add(new AbstractMap.SimpleImmutableEntry<>(street, null));
        }
        return list;
    }
}
