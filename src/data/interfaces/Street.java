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
    /**
     * Create street instance
     * @param id street name
     * @param coordinates coordinates of street beginning and ending
     * @return new street instance
     */
    static Street CreateStreet(String id, Coordinate... coordinates) { return MyStreet.CreateStreet(id, coordinates); }

    /**
     * Get street name
     * @return street name
     */
    String getId();

    /**
     * Get list of street coordinates
     * @return list of street coordinates
     */
    List<Coordinate> getStreetPoints();

    /**
     * Check if given GUI object line follows this street
     * @param line GUI object line
     * @return true if follows, otherwise false
     */
    boolean follows(Line line);

    /**
     * Check if given street follows this street
     * @param street given street instance
     * @return true if follows, otherwise false
     */
    boolean follows(Street street);

    /**
     * Add given stop instance to this street
     * @param stop stop instance
     * @return true if could be added, otherwise false
     */
    boolean AddStopToStreet(Stop stop);

    /**
     * Get list of stops on street
     * @return list of stops on street
     */
    List<Stop> getStops();

    /**
     * Get street beginning
     * @return coordinates of street beginning
     */
    Coordinate getBegin();

    /**
     * Get street ending
     * @return coordinates of street ending
     */
    Coordinate getEnd();

    /**
     * Set street density state
     * @param state user-selected density
     */
    void SetStreetState(StreetState state);

    /**
     * Get density of this street
     * @return density of this street
     */
    StreetState getStreetState();

    /**
     * Get coordinates where street was closed
     * @return coordinates of street closure
     */
    List<Coordinate> getClosurePoints();

    /**
     * Add coordinates where street was closed
     * @param point coordinates of street closure
     */
    void AddClosurePoint(Coordinate point);

    /**
     * Remove coordinates where street was close
     * @param point coordinates of street closure
     */
    void RemoveClosurePoint(Coordinate point);
}
