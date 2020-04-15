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
    public boolean ConstructRoute(List<Street> streets, PointInPath firstStop, PointInPath secondStop, int deltaTimeInMins) {
        this.deltaTime = deltaTimeInMins*60;

        //algorithm for stops at different streets
        route = new ArrayList<>();

        route.add(new AbstractMap.SimpleImmutableEntry<>(firstStop.getStreet(), firstStop.getCoordinate()));
        for(int street_i=streets.indexOf(firstStop.getStreet()); street_i<streets.size(); street_i++) {
            Street currStreet = streets.get(street_i);

            if(Math2D.isLocatedBetweenPoints(secondStop.getCoordinate(),
                    currStreet.getBegin(),
                    currStreet.getEnd())) {
                boolean noClosurePointInWay = true;

                for(Coordinate closurePoint: currStreet.getClosurePoints()) {
                    if(Math2D.isLocatedBetweenPoints(closurePoint, route.get(route.size()-1).getValue(), secondStop.getCoordinate()))
                        noClosurePointInWay = false;
                }

                if(noClosurePointInWay) {
                    route.add(new AbstractMap.SimpleImmutableEntry<>(currStreet, secondStop.getCoordinate()));
                    return true;
                }
            }

            if(street_i+1 < streets.size()){
                Street nextStreet = streets.get(street_i+1);
                if(Math2D.isLocatedBetweenPoints(currStreet.getBegin(),
                        nextStreet.getBegin(),
                        nextStreet.getEnd())) {
                    // |------
                    route.add(new AbstractMap.SimpleImmutableEntry<>(currStreet, currStreet.getBegin()));
                    route.add(new AbstractMap.SimpleImmutableEntry<>(nextStreet, currStreet.getBegin()));
                } else if(Math2D.isLocatedBetweenPoints(currStreet.getEnd(),
                        nextStreet.getBegin(),
                        nextStreet.getEnd())) {
                    // ------|
                    route.add(new AbstractMap.SimpleImmutableEntry<>(currStreet, currStreet.getEnd()));
                    route.add(new AbstractMap.SimpleImmutableEntry<>(nextStreet, currStreet.getEnd()));
                } else {
                    // ---|---
                    if(Math2D.isLocatedBetweenPoints(nextStreet.getBegin(),
                            currStreet.getBegin(),
                            currStreet.getEnd())) {
                        route.add(new AbstractMap.SimpleImmutableEntry<>(currStreet, nextStreet.getBegin()));
                        route.add(new AbstractMap.SimpleImmutableEntry<>(nextStreet, nextStreet.getBegin()));
                    } else if(Math2D.isLocatedBetweenPoints(nextStreet.getEnd(),
                            currStreet.getBegin(),
                            currStreet.getEnd())) {
                        route.add(new AbstractMap.SimpleImmutableEntry<>(currStreet, nextStreet.getEnd()));
                        route.add(new AbstractMap.SimpleImmutableEntry<>(nextStreet, nextStreet.getEnd()));
                    } else {
                        break;
                    }
                }
            } else {
                // street list does not contain full path to second stop
                route.add(new AbstractMap.SimpleImmutableEntry<>(streets.get(street_i), currStreet.getBegin()));
                route.add(new AbstractMap.SimpleImmutableEntry<>(streets.get(street_i), currStreet.getEnd()));
                break;
            }
        }
        return false;
    }

    @Override
    public List<AbstractMap.SimpleImmutableEntry<Street, Coordinate>> getRoute() {
        return route;
    }

    @Override
    public double getExpectedDeltaTime() { return deltaTime; }
}
