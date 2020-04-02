package data.implementations;

import data.interfaces.Coordinate;
import data.interfaces.Route;
import data.interfaces.Stop;
import data.interfaces.Street;
import utils.Math2D;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MyRoute implements Route {
    List<AbstractMap.SimpleImmutableEntry<Street,Coordinate>> route = new ArrayList<>();
    double deltaTime;

    @Override
    public boolean ConstructRoute(List<Street> streets, Stop firstStop, Stop secondStop, int deltaTimeInMins) {
        this.deltaTime = deltaTimeInMins*60;

        List<AbstractMap.SimpleImmutableEntry<Street,Coordinate>> points = new ArrayList<>();

        // algorithm for stops on same street
        if (firstStop.getStreet() == secondStop.getStreet()) {
            List<Coordinate> streetPoints = firstStop.getStreet().getStreetPoints();
            boolean hasFoundStop1 = false, hasFoundStop2 = false;
            for (int i = 0; i < streetPoints.size() - 1; i++) {
                if (hasFoundStop1 || hasFoundStop2) {
                    points.add(new AbstractMap.SimpleImmutableEntry<>(firstStop.getStreet(),streetPoints.get(i)));
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
                if (Math2D.isLocatedBetweenPoints(secondStop.getCoordinate(), streetPoints.get(i), streetPoints.get(i + 1))) {
                    if (hasFoundStop1) {
                        break;
                    } else {
                        hasFoundStop2 = true;
                    }
                }
            }
            // finally add the stops themselves
            points.add(0, new AbstractMap.SimpleImmutableEntry<>(firstStop.getStreet(),firstStop.getCoordinate()));
            points.add(new AbstractMap.SimpleImmutableEntry<>(secondStop.getStreet(),secondStop.getCoordinate()));
        } else {
            //algorithm for stops at different streets

            //step 1 add points from first stop to end of street
            //step 1a) find out the location of stop on street
            List<Coordinate> firstStreetPoints = firstStop.getStreet().getStreetPoints();
            for (int i = 0; i < firstStreetPoints.size() - 1; i++) {
                if (Math2D.isLocatedBetweenPoints(firstStop.getCoordinate(), firstStreetPoints.get(i), firstStreetPoints.get(i + 1))) {
                    //step 1b) find out which end of the street to go to
                    int nextStreetIndex = streets.indexOf(firstStop.getStreet()) + 1;
                    points.add(new AbstractMap.SimpleImmutableEntry<>(firstStop.getStreet(),firstStop.getCoordinate()));
                    List<Coordinate> pointsToAdd;
                    if (streets.get(nextStreetIndex).getStreetPoints().contains(firstStop.getStreet().getBegin())) {
                        pointsToAdd = firstStreetPoints.subList(0, i);
                        Collections.reverse(pointsToAdd);
                    } else {
                        pointsToAdd = firstStreetPoints.subList(i + 1, firstStreetPoints.size() - 1);
                    }

                    // 1c) assign all street points in the way
                    for(Coordinate coord:pointsToAdd)
                        points.add(new AbstractMap.SimpleImmutableEntry<>(firstStop.getStreet(), coord));
                    break;
                }
            }

            //step 2a) find traversal street points
            //TODO first stops street not found in streets => return false
            //TODO second stops street not found in streets => return false
            for (int street_i = streets.indexOf(firstStop.getStreet()) + 1;
                 street_i < streets.indexOf(secondStop.getStreet());
                 street_i++) {

                List<Coordinate> traversalStreetPoints = streets.get(street_i).getStreetPoints();
                Street prevStreet = streets.get(street_i - 1);
                if (traversalStreetPoints.contains(prevStreet.getBegin()) ||
                        traversalStreetPoints.contains(prevStreet.getEnd())) {
                    Collections.reverse(traversalStreetPoints);
                }
                //step 2b) add traversal street points
                for (Coordinate coord:traversalStreetPoints)
                    points.add(new AbstractMap.SimpleImmutableEntry<>(streets.get(street_i), coord));
            }

            //step 3 add points from start of street to second stop
            //step 3a) find out the location of stop on street
            List<Coordinate> secondStreetPoints = secondStop.getStreet().getStreetPoints();
            for (int i = 0; i < secondStreetPoints.size() - 1; i++) {
                if (Math2D.isLocatedBetweenPoints(secondStop.getCoordinate(), secondStreetPoints.get(i), secondStreetPoints.get(i + 1))) {
                    //step 3b) find out which end of the street to go from
                    int prevStreetIndex = streets.indexOf(secondStop.getStreet()) - 1;
                    List<Coordinate> pointsToAdd;
                    if (streets.get(prevStreetIndex).getStreetPoints().contains(secondStop.getStreet().getBegin())) {
                        pointsToAdd = secondStreetPoints.subList(0, i);
                    } else {
                        pointsToAdd = secondStreetPoints.subList(i + 1, secondStreetPoints.size() - 1);
                        Collections.reverse(pointsToAdd);
                    }

                    //step 3c) assign all street points in the way
                    for (Coordinate coord:pointsToAdd) {
                        points.add(new AbstractMap.SimpleImmutableEntry<>(secondStop.getStreet(), coord));
                    }
                    points.add(new AbstractMap.SimpleImmutableEntry<>(secondStop.getStreet(), secondStop.getCoordinate()));
                    break;
                }
            }
        }
        route = points;
        return true;
    }

    @Override
    public List<AbstractMap.SimpleImmutableEntry<Street, Coordinate>> getRoute() {
        return route;
    }

    @Override
    public double getExpectedDeltaTime() { return deltaTime; }
}
