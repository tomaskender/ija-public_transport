package data.implementations;

import data.interfaces.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class ChangePath {
    private Line line;
    private List<Vehicle> subscribedVehicles;
    private PointInPath beginning;
    private List<PointInPath> ends;
    private int deltaInMins;

    private Route foundAlternativeRoute = null;

    public ChangePath(Line line, PointInPath beginning, List<PointInPath> ends, int deltaInMins) {
        this.line = line;
        this.beginning = beginning;
        this.ends = ends;
        this.deltaInMins = deltaInMins;
        this.subscribedVehicles = new ArrayList<>();
    }

    public PointInPath getBeginning() {
        return beginning;
    }

    public List<PointInPath> getEnds() {
        return ends;
    }

    public void AddVehicle(Vehicle v) {
        subscribedVehicles.add(v);
    }

    public List<Vehicle> getSubscribedVehicles() { return subscribedVehicles; }

    public void SetFoundAlternativeRoute(Route altRoute) { foundAlternativeRoute = altRoute; }

    public Route getFoundAlternativeRoute() { return foundAlternativeRoute; }

    public int getDeltaInMins() { return deltaInMins; }

    public void SetDeltaInMins(int delta) { this.deltaInMins = delta; }

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
