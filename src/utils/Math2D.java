package utils;

import data.interfaces.Coordinate;

public class Math2D {
    public static boolean isLocatedBetweenPoints(Coordinate target, Coordinate p1, Coordinate p2) {
        double lineLen = Math.hypot(p1.getX() - p2.getX(), p1.getY() - p2.getY());
        double distanceFromP1 = getDistanceBetweenPoints(p1, target);
        double distanceFromP2 = getDistanceBetweenPoints(p2, target);
        return lineLen+0.05 > distanceFromP1 + distanceFromP2 && lineLen-0.05 < distanceFromP1 + distanceFromP2;
    }

    public static double getDistanceBetweenPoints(Coordinate p1, Coordinate p2) {
        return Math.hypot(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }
}
