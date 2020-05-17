/*
 * Entry for groups of vehicles that need to have alt route set.
 * Authors: Tomas Duris and Tomas Kender
 */

package data.implementations;

import data.interfaces.*;

import java.util.ArrayList;
import java.util.List;


public class ChangePath {
    private final Line line;
    private final List<Vehicle> subscribedVehicles;
    private final PointInPath beginning;
    private final List<PointInPath> ends;
    private int deltaInMins;

    private Route foundAlternativeRoute = null;

    /**
     * Change path of vehicle
     * @param line line of vehicle
     * @param beginning beginning of route
     * @param ends end of route
     * @param deltaInMins time to get to end
     */
    public ChangePath(Line line, PointInPath beginning, List<PointInPath> ends, int deltaInMins) {
        this.line = line;
        this.beginning = beginning;
        this.ends = ends;
        this.deltaInMins = deltaInMins;
        this.subscribedVehicles = new ArrayList<>();
    }

    /**
     * Get beginning of route
     * @return beginning point of alt route
     */
    public PointInPath getBeginning() {
        return beginning;
    }

    /**
     * Get ending of route
     * @return points where alt route can end
     */
    public List<PointInPath> getEnds() {
        return ends;
    }

    /**
     * Add vehicle to vehicle on route list
     * @param v vehicle to be added
     */
    public void AddVehicle(Vehicle v) {
        subscribedVehicles.add(v);
    }

    /**
     * Get vehicles on route
     * @return list of vehicles
     */
    public List<Vehicle> getSubscribedVehicles() { return subscribedVehicles; }

    /**
     * Change alternative route
     * @param altRoute new alternative route
     */
    public void SetFoundAlternativeRoute(Route altRoute) { foundAlternativeRoute = altRoute; }

    /**
     * Get alternative route
     * @return alternative route if found; otherwise null
     */
    public Route getFoundAlternativeRoute() { return foundAlternativeRoute; }

    /**
     * Get time of route
     * @return time of route
     */
    public int getDeltaInMins() { return deltaInMins; }

    /**
     * Set time of route
     * @param delta time of route
     */
    public void SetDeltaInMins(int delta) { this.deltaInMins = delta; }

    /**
     * Get info about route
     * @return string with info about route
     */
    @Override
    public String toString() {
        switch (subscribedVehicles.size()) {
            case 0: return "Vacuum";
            case 1: return "Bus from "+line.getId();
            default: return "Line "+line.getId();
        }
    }

    @Override
    public int hashCode() {
        return line.getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ChangePath) {
            return line.getId().equals(((ChangePath) o).line.getId()) &&
                    getBeginning().equals(((ChangePath) o).getBeginning()) &&
                    getEnds().equals(((ChangePath) o).getEnds());
        }
        return false;
    }
}
