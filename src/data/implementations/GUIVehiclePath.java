package data.implementations;

import data.interfaces.GUIMapElement;
import javafx.scene.paint.Color;

/**
 * Selected path element in graphical interface
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public class GUIVehiclePath implements GUIMapElement {
    private final Color highlightColor;

    /**
     * Set highlighted color
     * @param highlightColor color for highlight
     */
    public GUIVehiclePath(Color highlightColor) { this.highlightColor = highlightColor; }

    /**
     * Get normal color
     * @return NULL, because it is highlighted
     */
    @Override
    public Color getNormalColor() {
        return null;
    }

    /**
     * Get highlighted color
     * @return highlighted color
     */
    @Override
    public Color getHighlightedColor() {
        return highlightColor;
    }
}
