package data.interfaces;

import data.implementations.PointInPath;

import java.util.AbstractMap;
import java.util.List;

public interface Route {
    boolean ConstructRoute(List<Street> streets, PointInPath firstStop, PointInPath secondStop, double deltaTimeInMins);
    List<PointInPath> getRoute();
    double getExpectedDeltaTime();
}
