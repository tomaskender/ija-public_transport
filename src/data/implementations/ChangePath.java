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
     * @brief change path of vehicle
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
     * @brief get beginning of route
     * @return beginning
     */
    public PointInPath getBeginning() {
        return beginning;
    }

    /**
     * @brief get ending of route
     * @return ending
     */
    public List<PointInPath> getEnds() {
        return ends;
    }

    /**
     * @brief add vehicle to vehicle on route list
     * @param v vehicle
     */
    public void AddVehicle(Vehicle v) {
        subscribedVehicles.add(v);
    }

    /**
     * @brief get vehicles on route
     * @return list of vehicles
     */
    public List<Vehicle> getSubscribedVehicles() { return subscribedVehicles; }

    /**
     * @brief change alternative route
     * @param altRoute new alternative route
     */
    public void SetFoundAlternativeRoute(Route altRoute) { foundAlternativeRoute = altRoute; }

    /**
     * @brief get alternative route
     * @return alternative route
     */
    public Route getFoundAlternativeRoute() { return foundAlternativeRoute; }

    /**
     * @brief get time of route
     * @return time of route
     */
    public int getDeltaInMins() { return deltaInMins; }

    /**
     * @brief set time of route
     * @param delta time of route
     */
    public void SetDeltaInMins(int delta) { this.deltaInMins = delta; }

    /**
     * @brief get info about route
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
