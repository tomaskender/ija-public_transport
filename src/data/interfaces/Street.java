/*
 * A street on map that vehicles have to cross.
 * Authors: Tomas Duris and Tomas Kender
 */
package data.interfaces;

import java.util.List;

import data.enums.StreetState;
import data.implementations.MyStreet;
import javafx.scene.shape.Line;

public interface Street extends GUIMapElement {
    static Street CreateStreet(String id, Coordinate... coordinates) { return MyStreet.CreateStreet(id, coordinates); }
    String getId();
    List<Coordinate> getStreetPoints();
    boolean follows(Line line);
    boolean follows(Street street);
    boolean AddStopToStreet(Stop s);
    List<Stop> getStops();
    Coordinate getBegin();
    Coordinate getEnd();

    void SetStreetState(StreetState state);
    StreetState getStreetState();

    List<Coordinate> getClosurePoints();
    void AddClosurePoint(Coordinate point);
    void RemoveClosurePoint(Coordinate point);
}
