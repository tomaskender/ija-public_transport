package data.implementations;

import data.interfaces.Coordinate;
import data.interfaces.Line;
import data.interfaces.Stop;
import data.interfaces.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class ChangePath {
    private Line line;
    private List<Vehicle> subscribedVehicles = new ArrayList<>();
    private Stop beginning;
    private Stop end;

    public ChangePath(Line line, Stop beginning, Stop end) {
        this.line = line;
        this.beginning = beginning;
        this.end = end;
    }

    public Stop getBeginning() {
        return beginning;
    }

    public Stop getEnd() {
        return end;
    }

    public void AddVehicle(Vehicle v) {
        subscribedVehicles.add(v);
    }

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
                    getEnd().equals(((ChangePath) o).getEnd());
        }
        return false;
    }
}
