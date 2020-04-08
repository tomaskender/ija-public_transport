package data.implementations;

import data.interfaces.*;
import javafx.scene.paint.Color;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MyLine implements Line {
    private String id;
    private List<Street> streets = new ArrayList<>();
    private List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stops = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();
    private Color mapColor;

    public static Line CreateLine(String id) { return new MyLine(id); }

    public MyLine(String id) {
        this.id = id;
        this.mapColor = Color.color(Math.random(), Math.random(), Math.random());
    }

    @Override
    public String getId() { return id; }

    @Override
    public boolean AddStop(Stop stop, int delta) {
        // is the street neighboring any existing streets?
        if(AddTraversalStreet(stop.getStreet()) == false)
            return false;
        if(stop.getStreet().AddStopToStreet(stop) == false)
            return false;
        stops.add(new AbstractMap.SimpleImmutableEntry<>(stop,delta));
        return true;
    }

    public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStops() { return stops;}

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
    public boolean AddVehicleToLine(Vehicle v) {
        boolean alreadyContainsThisVehicle = false;
        for(Vehicle vehicle : vehicles) {
            if(vehicle.getStart() == v.getStart()) {
                alreadyContainsThisVehicle = true;
                break;
            }
        }
        if(!alreadyContainsThisVehicle) {
            vehicles.add(v);
            for(int i=0; i<stops.size()-1;i++) {
                Route route = new MyRoute();
                route.ConstructRoute(streets, stops.get(i).getKey(), stops.get(i+1).getKey(), stops.get(i+1).getValue());
                v.AddRoute(route);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Color getMapColor() { return mapColor; }
}
