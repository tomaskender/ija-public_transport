/*
 * Brush color interface for elements on a map.
 * Authors: Tomas Duris and Tomas Kender
 */
package data.interfaces;

import javafx.scene.paint.Color;

public interface GUIMapElement {
    Color getNormalColor();
    Color getHighlightedColor();
}
