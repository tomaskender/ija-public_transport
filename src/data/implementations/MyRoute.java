package data.implementations;

import data.interfaces.Coordinate;
import data.interfaces.Route;
import data.interfaces.Stop;
import data.interfaces.Street;
import utils.Math2D;

import java.awt.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MyRoute implements Route {
    List<PointInPath> route = new ArrayList<>();
    double deltaTime;

    @Override
    public boolean ConstructRoute(List<Street> streets, PointInPath firstStop, PointInPath secondStop, double deltaTimeInMins) {
        this.deltaTime = deltaTimeInMins*60;

        //algorithm for stops at different streets
        route = new ArrayList<>();

        route.add(new PointInPath(this, firstStop.getStreet(), firstStop.getCoordinate()));
        for(int street_i=streets.indexOf(firstStop.getStreet()); street_i<streets.size(); street_i++) {
            Street currStreet = streets.get(street_i);

            if(Math2D.isLocatedBetweenPoints(secondStop.getCoordinate(),
                    currStreet.getBegin(),
                    currStreet.getEnd())) {
                boolean noClosurePointInWay = true;

                for(Coordinate closurePoint: currStreet.getClosurePoints()) {
                    if(Math2D.isLocatedBetweenPoints(closurePoint, route.get(route.size()-1).getCoordinate(), secondStop.getCoordinate()))
                        noClosurePointInWay = false;
                }

                if(noClosurePointInWay) {
                    route.add(new PointInPath(this, currStreet, secondStop.getCoordinate()));
                    return true;
                }
            }

            if(street_i+1 < streets.size()){
                Street nextStreet = streets.get(street_i+1);
                if(Math2D.isLocatedBetweenPoints(currStreet.getBegin(),
                        nextStreet.getBegin(),
                        nextStreet.getEnd())) {
                    // |------
                    route.add(new PointInPath(this, currStreet, currStreet.getBegin()));
                    route.add(new PointInPath(this, nextStreet, currStreet.getBegin()));
                } else if(Math2D.isLocatedBetweenPoints(currStreet.getEnd(),
                        nextStreet.getBegin(),
                        nextStreet.getEnd())) {
                    // ------|
                    route.add(new PointInPath(this, currStreet, currStreet.getEnd()));
                    route.add(new PointInPath(this, nextStreet, currStreet.getEnd()));
                } else {
                    // ---|---
                    if(Math2D.isLocatedBetweenPoints(nextStreet.getBegin(),
                            currStreet.getBegin(),
                            currStreet.getEnd())) {
                            route.add(new PointInPath(this, currStreet, nextStreet.getBegin()));
                            route.add(new PointInPath(this, nextStreet, nextStreet.getBegin()));
                    } else if(Math2D.isLocatedBetweenPoints(nextStreet.getEnd(),
                            currStreet.getBegin(),
                            currStreet.getEnd())) {
                            route.add(new PointInPath(this, currStreet, nextStreet.getEnd()));
                            route.add(new PointInPath(this, nextStreet, nextStreet.getEnd()));
                    } else {
                        break;
                    }
                }
            } else {
                // street list does not contain full path to second stop
                Coordinate closestBeginStop = currStreet.getBegin();
                Coordinate closestEndStop = currStreet.getEnd();
                for(Coordinate closurePoint: currStreet.getClosurePoints()) {
                    if(Math2D.isLocatedBetweenPoints(closurePoint, route.get(route.size()-1).getCoordinate(), currStreet.getBegin())) {
                        if(closestBeginStop == null || Math2D.getDistanceBetweenPoints(route.get(route.size()-1).getCoordinate(), closurePoint) <
                                                        Math2D.getDistanceBetweenPoints(route.get(route.size()-1).getCoordinate(), closestBeginStop))
                            closestBeginStop = closurePoint;
                    } else {
                        if(closestBeginStop == null || Math2D.getDistanceBetweenPoints(route.get(route.size()-1).getCoordinate(), closurePoint) <
                                                        Math2D.getDistanceBetweenPoints(route.get(route.size()-1).getCoordinate(), closestEndStop))
                            closestEndStop = closurePoint;
                    }
                }
                route.add(new PointInPath(this, streets.get(street_i), closestBeginStop));
                route.add(new PointInPath(this, streets.get(street_i), closestEndStop));
                break;
            }
        }
        return false;
    }

    @Override
    public List<PointInPath> getRoute() {
        return route;
    }

    @Override
    public double getExpectedDeltaTime() { return deltaTime; }

    @Override
    public void SetExpectedDeltaTime(double delta) {
        this.deltaTime = delta;
    }


}
