package data.interfaces;

import java.util.AbstractMap;
import java.util.List;

public interface Line {
    boolean AddTraversalStreet(Street s);
    boolean AddStop(Stop s);
    List<AbstractMap.SimpleImmutableEntry<Street,Stop>> getRoute();
}
