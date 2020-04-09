package data.interfaces;

import data.implementations.MyLine;
import javafx.scene.paint.Color;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Line {
    List<Color> mapColors = new ArrayList<>(Arrays.asList(Color.GREEN, Color.BLUE, Color.RED, Color.ORANGE, Color.PURPLE, Color.BROWN, Color.PINK, Color.GREY, Color.LIME, Color.LIGHTBLUE));
    static Line CreateLine(String id) { return MyLine.CreateLine(id); }
    String getId();
    List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStops();
    boolean AddTraversalStreet(Street s);
    boolean AddStop(Stop s, int delta);
    boolean AddVehicleToLine(Vehicle v);
    Color getMapColor();
}
