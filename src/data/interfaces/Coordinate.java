package data.interfaces;

import data.implementations.*;

public interface Coordinate {
    static Coordinate CreateCoordinate(int x, int y) { return MyCoordinate.CreateCoordinate(x, y); }
    int getX();
    int getY();
}
