package data.interfaces;

import java.util.List;

public interface Street {
    List<Coordinate> getStreetPoints();
    boolean Follows(Street s);
    void AddStopToStreet(Stop s);
}
