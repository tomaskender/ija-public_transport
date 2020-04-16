package data.implementations;

import data.enums.StreetState;
import data.interfaces.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import utils.Math2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyStreet implements Street, GUIMapElement {
    private String id;
    private List<Coordinate> closurePoints = new ArrayList<>();
    private StreetState state = StreetState.LOW;
    private List<Stop> stops = new ArrayList<>();
    private List<Coordinate> coords = new ArrayList<>();

    public static Street CreateStreet(String id, Coordinate... coordinates) { return new MyStreet(id, coordinates); }

    public MyStreet(String id, Coordinate... coordinates) {
        this.id = id;
        coords = Arrays.asList(coordinates);
    }

    @Override
    public String getId() { return id; }

    @Override
    public List<Coordinate> getStreetPoints() { return coords; }

    @Override
    public Coordinate getBegin() { return coords.get(0); }

    @Override
    public Coordinate getEnd() { return coords.get(coords.size()-1); }

    @Override
    public List<Stop> getStops() { return stops; }

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
        if(isOnStreet == false) {
            return false;
        }

        if(!stops.contains(stop))
            stops.add(stop);
        return true;
    }

    @Override
    public boolean follows(Street street) {
        return follows(new Line(street.getStreetPoints().get(0).getX(), street.getStreetPoints().get(0).getY(), street.getStreetPoints().get(1).getX(), street.getStreetPoints().get(1).getY()));
    }

    @Override
    public boolean follows(Line line) {
        return Math2D.isLocatedBetweenPoints(Coordinate.CreateCoordinate(line.getStartX(), line.getStartY()), getBegin(), getEnd()) ||
                Math2D.isLocatedBetweenPoints(Coordinate.CreateCoordinate(line.getEndX(), line.getEndY()), getBegin(), getEnd()) ||
                Math2D.isLocatedBetweenPoints(getBegin(), Coordinate.CreateCoordinate(line.getStartX(), line.getStartY()), Coordinate.CreateCoordinate(line.getEndX(), line.getEndY())) ||
                Math2D.isLocatedBetweenPoints(getEnd(), Coordinate.CreateCoordinate(line.getStartX(), line.getStartY()), Coordinate.CreateCoordinate(line.getEndX(), line.getEndY()));
    }

    @Override
    public void SetStreetState(StreetState state) {
        this.state = state;
    }

    @Override
    public StreetState getStreetState() { return state; }

    @Override
    public List<Coordinate> getClosurePoints() { return closurePoints; }

    @Override
    public void AddClosurePoint(Coordinate point) {
        closurePoints.add(point);
    }

    @Override
    public void RemoveClosurePoint(Coordinate point) {
        closurePoints.remove(point);
    }

    @Override
    public Color getNormalColor() {
        return closurePoints.isEmpty() ? Color.BLACK : Color.GREY;
    }

    @Override
    public Color getHighlightedColor() { return Color.ORANGE; }
}
