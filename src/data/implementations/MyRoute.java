package data.implementations;

import data.interfaces.Coordinate;
import data.interfaces.Route;
import data.interfaces.Street;
import utils.Math2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Route of a vehicle
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public class MyRoute implements Route {
    List<PointInPath> route = new ArrayList<>();
    double deltaTime;

    /**
     * Route constructor
     * @param streets vector of given streets on route
     * @param firstStop first stop on route
     * @param secondStop second stop on route
     * @param deltaTimeInMins time to get to stop
     * @return true if route was sucessfuly constructed, otherwise false
     */
    @Override
    public boolean ConstructRoute(List<Street> streets, PointInPath firstStop, PointInPath secondStop, double deltaTimeInMins) {
        this.deltaTime = deltaTimeInMins*60;

        //algorithm for stops at different streets
        route = new ArrayList<>();

        route.add(new PointInPath(this, firstStop.getStreet(), firstStop.getCoordinate()));
        for(int street_i=streets.indexOf(firstStop.getStreet()); street_i<streets.size(); street_i++) {
            Street currStreet = streets.get(street_i);

            // find second stop
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

            // find path through current street to next street in street list
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
                // street list is not finished yet, highlight entire street from start to finish - typically used during alternative route selection
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

    /**
     * Get route points
     * @return route points
     */
    @Override
    public List<PointInPath> getRoute() {
        return route;
    }

    /**
     * Get time to get to to next stop
     * @return time in double
     */
    @Override
    public double getExpectedDeltaTime() { return deltaTime; }

    /**
     * Set route time
     * @param delta given route time
     */
    @Override
    public void SetExpectedDeltaTime(double delta) {
        this.deltaTime = delta;
    }


}
