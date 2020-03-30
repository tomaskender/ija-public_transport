package data.implementations;

import data.interfaces.*;

import java.util.Objects;

public class MyCoordinate implements Coordinate {
    int x, y;

    public MyCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinate CreateCoordinate(int x, int y) {
        if(x < 0 || y < 0)
            return null;
        else
            return new MyCoordinate(x, y);
    }

    @Override
    public int getX() {
        return x;
    }

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
