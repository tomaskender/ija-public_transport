package data.interfaces;

import data.implementations.*;

/**
 * X and Y coordinate of element on the map.
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public interface Coordinate {
    /**
     * Create coordinate for double values
     * @param x position on axe x
     * @param y position on axe y
     * @return coordinate
     */
    static Coordinate CreateCoordinate(double x, double y) { return MyCoordinate.CreateCoordinate(x, y); }

    /**
     * Create coordinate for int values
     * @param x position on axe x
     * @param y position on axe y
     * @return coordinate
     */
    static Coordinate CreateCoordinate(int x, int y) { return MyCoordinate.CreateCoordinate(x, y); }

    /**
     * Get value on X axe
     * @return x value
     */
    int getX();


    /**
     * Get value on Y axe
     * @return y value
     */
    int getY();
}
