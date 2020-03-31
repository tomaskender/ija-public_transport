package data.interfaces;

import java.util.AbstractMap;
import java.util.List;

public interface Route {
    boolean ConstructRoute(List<Street> streets, Stop firstStop, Stop secondStop, int delta);
    List<AbstractMap.SimpleImmutableEntry<Street, Coordinate>> getRoute();
    double getExpectedDeltaTime();
}
