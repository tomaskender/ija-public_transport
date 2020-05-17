/*
 * Brush color interface for elements on a map.
 * Authors: Tomas Duris and Tomas Kender
 */
package data.interfaces;

import javafx.scene.paint.Color;

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
