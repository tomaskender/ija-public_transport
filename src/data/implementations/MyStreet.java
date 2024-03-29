package data.implementations;

import data.enums.StreetState;
import data.interfaces.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import utils.Math2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A street on map that vehicles have to cross
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public class MyStreet implements Street, GUIMapElement {
    private final String id;
    private final List<Coordinate> closurePoints = new ArrayList<>();
    private StreetState state = StreetState.LOW;
    private final List<Stop> stops = new ArrayList<>();
    private final List<Coordinate> coords;

    /**
     * Create street instance
     * @param id street name
     * @param coordinates coordinates of street beginning and ending
     * @return new street instance
     */
    public static Street CreateStreet(String id, Coordinate... coordinates) { return new MyStreet(id, coordinates); }

    /**
     * Street instance value initialized
     * @param id street name
     * @param coordinates coordinates of street beginning and ending
     */
    public MyStreet(String id, Coordinate... coordinates) {
        this.id = id;
        coords = Arrays.asList(coordinates);
    }

    /**
     * Get street name
     * @return street name
     */
    @Override
    public String getId() { return id; }

    /**
     * Get list of street coordinates
     * @return list of street coordinates
     */
    @Override
    public List<Coordinate> getStreetPoints() { return coords; }

    /**
     * Get street beginning
     * @return coordinates of street beginning
     */
    @Override
    public Coordinate getBegin() { return coords.get(0); }

    /**
     * Get street ending
     * @return coordinates of street ending
     */
    @Override
    public Coordinate getEnd() { return coords.get(coords.size()-1); }

    /**
     * Get list of stops on street
     * @return list of stops on street
     */
    @Override
    public List<Stop> getStops() { return stops; }

    /**
     * Add given stop instance to this street
     * @param stop stop instance
     * @return true if could be added, otherwise false
     */
    @Override
    public boolean AddStopToStreet(Stop stop) {
        // is it located on the street
        boolean isOnStreet = false;
        if(coords.size() == 1) {
            isOnStreet = coords.get(0).equals(stop.getCoordinate());
        } else {
            for (int i = 1; i < coords.size(); i++) {
                Coordinate p1 = coords.get(i);
                Coordinate p2 = coords.get(i - 1);
                Coordinate pStop = stop.getCoordinate();
                if (Math2D.isLocatedBetweenPoints(pStop, p1, p2)) {
                    isOnStreet = true;
                    break;
                }
            }
        }
        if(!isOnStreet) {
            return false;
        }

        if(!stops.contains(stop))
            stops.add(stop);
        return true;
    }

    /**
     * Check if given street follows this street
     * @param street given street instance
     * @return true if follows, otherwise false
     */
    @Override
    public boolean follows(Street street) {
        return follows(new Line(street.getStreetPoints().get(0).getX(), street.getStreetPoints().get(0).getY(), street.getStreetPoints().get(1).getX(), street.getStreetPoints().get(1).getY()));
    }

    /**
     * Check if given GUI object line follows this street
     * @param line GUI object line
     * @return true if follows, otherwise false
     */
    @Override
    public boolean follows(Line line) {
        Coordinate lineStart = Coordinate.CreateCoordinate(line.getStartX(), line.getStartY());
        Coordinate lineEnd = Coordinate.CreateCoordinate(line.getEndX(), line.getEndY());
        return (Math2D.isLocatedBetweenPoints(lineStart, getBegin(), getEnd()) ||
                Math2D.isLocatedBetweenPoints(lineEnd, getBegin(), getEnd()) ||
                Math2D.isLocatedBetweenPoints(getBegin(), lineStart, lineEnd) ||
                Math2D.isLocatedBetweenPoints(getEnd(), lineStart, lineEnd)) &&
                !(Math2D.isLocatedBetweenPoints(lineStart, getBegin(), getEnd()) && Math2D.isLocatedBetweenPoints(lineEnd, getBegin(), getEnd()));
    }

    /**
     * Set street density state
     * @param state user-selected density
     */
    @Override
    public void SetStreetState(StreetState state) {
        this.state = state;
    }

    /**
     * Get density of this street
     * @return density of this street
     */
    @Override
    public StreetState getStreetState() { return state; }

    /**
     * Get coordinates where street was closed
     * @return coordinates of street closure
     */
    @Override
    public List<Coordinate> getClosurePoints() { return closurePoints; }

    /**
     * Add coordinates where street was closed
     * @param point coordinates of street closure
     */
    @Override
    public void AddClosurePoint(Coordinate point) {
        closurePoints.add(point);
    }

    /**
     * Remove coordinates where street was close
     * @param point coordinates of street closure
     */
    @Override
    public void RemoveClosurePoint(Coordinate point) {
        closurePoints.remove(point);
    }

    /**
     * Get normal map color
     * @return return BLACK or GREY, depends on if closing street
     */
    @Override
    public Color getNormalColor() {
        return closurePoints.isEmpty() ? Color.BLACK : Color.GREY;
    }

    /**
     * Get highlighted color
     * @return ORANGE color for highlights
     */
    @Override
    public Color getHighlightedColor() { return Color.ORANGE; }
}
