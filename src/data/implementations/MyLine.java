/*
 * Transport line with it's associated vehicles
 * Authors: Tomas Duris and Tomas Kender
 */
package data.implementations;

import data.interfaces.*;
import javafx.scene.paint.Color;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MyLine implements Line {
    private final String id;
    private final List<Street> streets = new ArrayList<>();
    private final List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stops = new ArrayList<>();
    private final List<Vehicle> vehicles = new ArrayList<>();
    private Color mapColor;

    /**
     * @brief Create line instance with name and color
     * @param id line identifier
     * @return line instance
     */
    public static Line CreateLine(String id) { return new MyLine(id); }

    public MyLine(String id) {
        this.id = id;
        try {
            this.mapColor = mapColors.remove(0);
        } catch (Exception e){
            this.mapColor = Color.color(Math.random(), Math.random(), Math.random());
        }
    }

    /**
     * @brief get line identifier
     * @return line identifier
     */
    @Override
    public String getId() { return id; }

    /**
     * @brief add given stop with name and time to get there on line
     * @param stop given stop
     * @param delta time to get to sto
     */
    @Override
    public void AddStop(Stop stop, int delta) {
        // is the street neighboring any existing streets?
        if(!AddTraversalStreet(stop.getStreet()))
            return;
        if(!stop.getStreet().AddStopToStreet(stop))
            return;
        stops.add(new AbstractMap.SimpleImmutableEntry<>(stop,delta));
    }

    public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStops() { return stops;}

    @Override
    public boolean AddTraversalStreet(Street street) {
        boolean follows = false;
        if(streets.size() == 0) {
            follows = true;
        } else {
            for (Street s : streets) {
                if (s.follows(street)) {
                    follows = true;
                    break;
                }
            }
        }
        if(!follows) {
            return false;
        }
        if(!streets.contains(street))
            streets.add(street);
        return true;
    }

    /**
     * @brief add vehicle instance to line
     * @param v vehicle instance
     * @return true if could be added, otherwise false
     */
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
                route.ConstructRoute(streets,
                                    new PointInPath(route, stops.get(i).getKey().getStreet(), stops.get(i).getKey().getCoordinate()),
                                    new PointInPath(route, stops.get(i+1).getKey().getStreet(), stops.get(i+1).getKey().getCoordinate()),
                                    stops.get(i+1).getValue());
                v.AddRoute(route);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @brief get color of this line
     * @return color value
     */
    @Override
    public Color getMapColor() { return mapColor; }
}
