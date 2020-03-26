package data.implementations;

import data.interfaces.*;

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
}
