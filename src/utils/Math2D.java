package utils;

import data.interfaces.Coordinate;

public class Math2D {
    public static boolean isLocatedBetweenPoints(Coordinate target, Coordinate p1, Coordinate p2) {
        double lineLen = Math.hypot(p1.getX() - p2.getX(), p1.getY() - p2.getY());
        double distanceFromP1 = Math.hypot(p1.getX() - target.getX(), p1.getY() - target.getY());
        double distanceFromP2 = Math.hypot(p2.getX() - target.getX(), p2.getY() - target.getY());
        return lineLen+0.05 > distanceFromP1 + distanceFromP2 && lineLen-0.05 < distanceFromP1 + distanceFromP2;
    }
}
