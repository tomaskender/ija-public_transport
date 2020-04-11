package data.interfaces;

import java.util.List;

import data.enums.StreetState;
import data.implementations.MyStreet;

public interface Street extends GUIMapElement {
    static Street CreateStreet(String id, Coordinate... coordinates) { return MyStreet.CreateStreet(id, coordinates); }
    String getId();
    List<Coordinate> getStreetPoints();
    boolean Follows(Street s);
    boolean AddStopToStreet(Stop s);
    List<Stop> getStops();
    Coordinate getBegin();
    Coordinate getEnd();
    void SetStreetState(StreetState state);
    StreetState getStreetState();
    void AddClosurePoint(Coordinate point);
    void RemoveClosurePoint(Coordinate point);
}
