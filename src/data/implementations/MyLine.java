package data.implementations;

import data.interfaces.*;
import javafx.scene.paint.Color;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Transport line with it's associated vehicles
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public class MyLine implements Line {
    private final String id;
    private final List<Street> streets = new ArrayList<>();
    private final List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stops = new ArrayList<>();
    private final List<Vehicle> vehicles = new ArrayList<>();
    private Color mapColor;

    /**
     * Create line instance with name and color
     * @param id line identifier
     * @return line instance
     */
    public static Line CreateLine(String id) { return new MyLine(id); }

    /**
     * Initialize line instance
     * @param id line identifier
     */
    public MyLine(String id) {
        this.id = id;
        try {
            this.mapColor = mapColors.remove(0);
        } catch (Exception e){
            this.mapColor = Color.color(Math.random(), Math.random(), Math.random());
        }
    }

    /**
     * Get line identifier
     * @return line identifier
     */
    @Override
    public String getId() { return id; }

    /**
     * Add given stop with name and time to get there on line
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

    /**
     * Get all stops line vehicles have to cross
     * @return List of stops and times it takes to get to them from previous stop
     */
    public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStops() { return stops;}

    /**
     * Add a street that vehicles will have to traverse on their way
     * @param street street to be added to line
     * @return true if street was successfully added, otherwise false
     */
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
     * Add vehicle instance to line
     * @param v vehicle instance to be added to line
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
     * Get color of this line
     * @return color value
     */
    @Override
    public Color getMapColor() { return mapColor; }
}
