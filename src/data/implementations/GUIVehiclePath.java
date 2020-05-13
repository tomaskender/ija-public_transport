package data.implementations;

import data.interfaces.GUIMapElement;
import javafx.scene.paint.Color;

public class GUIVehiclePath implements GUIMapElement {
    private Color highlightColor;

    /**
     * @brief  set highlighted color
     * @param highlightColor color for highlight
     */
    public GUIVehiclePath(Color highlightColor) { this.highlightColor = highlightColor; }

    /**
     * @brief get normal color
     * @return NULL, because it is highlighted
     */
    @Override
    public Color getNormalColor() {
        return null;
    }

    /**
     * @brief get highlighted color
     * @return highlighted color
     */
    @Override
    public Color getHighlightedColor() {
        return highlightColor;
    }
}
