package data.implementations;

import data.interfaces.*;
import utils.Math2D;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MyLine implements Line {
    private String id;
    private List<Street> streets = new ArrayList<>();
    private List<Stop> stops = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();

    private List<Coordinate> route = new ArrayList<>();

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
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void ConstructRoute() {
        route = new ArrayList<>();

        //add traversal street points
        for(int stop_i=0; stop_i<stops.size()-1; stop_i++) {
            Stop firstStop = stops.get(stop_i);
            Stop secondStop = stops.get(stop_i+1);
            List<Coordinate> points = new ArrayList<>();

            // algorithm for stops on same street
            if(firstStop.getStreet() == secondStop.getStreet()) {
                List<Coordinate> streetPoints = firstStop.getStreet().getStreetPoints();
                boolean hasFoundStop1 = false, hasFoundStop2 = false;
                for (int i = 0; i < streetPoints.size() - 1; i++) {
                    if(hasFoundStop1 || hasFoundStop2) {
                        points.add(streetPoints.get(i));
                    }

                    if (Math2D.isLocatedBetweenPoints(firstStop.getCoordinate(), streetPoints.get(i), streetPoints.get(i + 1))) {
                        if (hasFoundStop2) {
                            //if stop2 was found first, then it means we are going from end of street to its start
                            Collections.reverse(points);
                            break;
                        } else {
                            hasFoundStop1 = true;
                        }
                    }
                    if(Math2D.isLocatedBetweenPoints(secondStop.getCoordinate(), streetPoints.get(i), streetPoints.get(i + 1))) {
                        if(hasFoundStop1) {
                            break;
                        } else {
                            hasFoundStop2 = true;
                        }
                    }
                }
                // finally add the stops themselves
                points.add(0, firstStop.getCoordinate());
                points.add(secondStop.getCoordinate());
            } else {
                //algorithm for stops at different streets
                
                //step 1 add points from first stop to end of street
                //step 1a) find out the location of stop on street
                List<Coordinate> firstStreetPoints = firstStop.getStreet().getStreetPoints();
                for (int i = 0; i < firstStreetPoints.size() - 1; i++) {
                    if (Math2D.isLocatedBetweenPoints(firstStop.getCoordinate(), firstStreetPoints.get(i), firstStreetPoints.get(i + 1))) {
                        //step 1b) find out which end of the street to go to and assign all street points in the way
                        int nextStreetIndex = streets.indexOf(firstStop.getStreet()) + 1;
                        route.add(firstStop.getCoordinate());
                        if (streets.get(nextStreetIndex).getStreetPoints().contains(firstStop.getStreet().getBegin()))
                            route.addAll(1, firstStreetPoints.subList(0, i));
                        else
                            route.addAll(1, firstStreetPoints.subList(i + 1, firstStreetPoints.size() - 1));
                        break;
                    }
                }

                //step 2a) find traversal street points
                for (int street_i = streets.indexOf(firstStop.getStreet()) + 1;
                     street_i < streets.indexOf(secondStop.getStreet());
                     street_i++) {
                    List<Coordinate> traversalStreetPoints = streets.get(street_i).getStreetPoints();
                    Street prevStreet = streets.get(street_i - 1);
                    if (traversalStreetPoints.contains(prevStreet.getBegin()) ||
                            traversalStreetPoints.contains(prevStreet.getEnd())) {
                        Collections.reverse(traversalStreetPoints);
                    }
                    //step 2b) add traversal street points except first one
                    route.addAll(traversalStreetPoints.stream().skip(1).collect(Collectors.toList()));
                }

                //step 3 add points from start of street to second stop
                //step 3a) find out the location of stop on street
                List<Coordinate> secondStreetPoints = firstStop.getStreet().getStreetPoints();
                for (int i = 0; i < secondStreetPoints.size() - 1; i++) {
                    if (Math2D.isLocatedBetweenPoints(firstStop.getCoordinate(), secondStreetPoints.get(i), secondStreetPoints.get(i + 1))) {
                        //step 3b) find out which end of the street to go from and assign all street points in the way except start of street
                        int prevStreetIndex = streets.indexOf(firstStop.getStreet()) - 1;
                        List<Coordinate> pointsToAdd;
                        if (streets.get(prevStreetIndex).getStreetPoints().contains(firstStop.getStreet().getBegin()))
                            pointsToAdd = secondStreetPoints.subList(0, i);
                        else
                            pointsToAdd = secondStreetPoints.subList(i + 1, secondStreetPoints.size() - 1);
                        route.addAll(pointsToAdd.stream().skip(1).collect(Collectors.toList()));
                        route.add(secondStop.getCoordinate());
                        break;
                    }
                }
            }
        }
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
