package data.interfaces;

import data.implementations.PointInPath;

import java.util.AbstractMap;
import java.util.List;

public interface Route {
    boolean ConstructRoute(List<Street> streets, PointInPath firstStop, PointInPath secondStop, int deltaTimeInMins);
    List<AbstractMap.SimpleImmutableEntry<Street, Coordinate>> getRoute();
    double getExpectedDeltaTime();
}
