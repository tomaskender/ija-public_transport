/*
 * X and Y coordinate of element on the map
 * Authors: Tomas Duris and Tomas Kender
 */
package data.implementations;

import data.interfaces.*;

public class MyCoordinate implements Coordinate {
    final int x;
    final int y;

    /**
     * Initialize coordinate
     * @param x position on axe x
     * @param y position on axe y
     */
    public MyCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create coordinate for double values
     * @param x position on axe x
     * @param y position on axe y
     * @return coordinate
     */
    public static Coordinate CreateCoordinate(double x, double y) {
        return CreateCoordinate((int)x, (int)y);
    }

    /**
     * Create coordinate for int values
     * @param x position on axe x
     * @param y position on axe y
     * @return coordinate
     */
    public static Coordinate CreateCoordinate(int x, int y) {
        if(x < 0 || y < 0)
            return null;
        else
            return new MyCoordinate(x, y);
    }

    /**
     * Get value on X axe
     * @return x value
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Get value on Y axe
     * @return y value
     */
    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Coordinate)
            return this.getX() == ((Coordinate)o).getX() && this.getY() == ((Coordinate)o).getY();
        else
            return false;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }
}
