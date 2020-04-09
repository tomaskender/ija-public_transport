package data.implementations;

import data.interfaces.GUIMapElement;
import javafx.scene.paint.Color;

public class GUIVehiclePath implements GUIMapElement {
    private Color highlightColor;

    public GUIVehiclePath(Color highlightColor) { this.highlightColor = highlightColor; }

    @Override
    public Color getNormalColor() {
        return null;
    }

    @Override
    public Color getHighlightedColor() {
        return highlightColor;
    }
}
