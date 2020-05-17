package data.interfaces;

import javafx.scene.paint.Color;

/**
 * Brush color interface for elements on a map.
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public interface GUIMapElement {
    /**
     * Get normal element color
     * @return y value
     */
    Color getNormalColor();

    /**
     * Get highlighted element color
     * @return y value
     */
    Color getHighlightedColor();
}
