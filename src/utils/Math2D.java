package utils;

import data.implementations.PointInPath;
import data.interfaces.Coordinate;
import data.interfaces.Route;

import java.util.List;

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

    public static Coordinate getClosestPointOnSegment(Coordinate s1, Coordinate s2, Coordinate sp)
    {
        int s1X = s1.getX(), s2X = s2.getX(), spX  = sp.getX();
        int s1Y = s1.getY(), s2Y = s2.getY(), spY  = sp.getY();

        double xDelta = s2X - s1X;
        double yDelta = s2Y - s1Y;

        if ((xDelta == 0) && (yDelta == 0))
        {
            return s1;
        }

        double u = ((spX - s1X) * xDelta + (spY - s1Y) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final Coordinate closestPoint;
        if (u < 0)
        {
            return s1;
        }
        else if (u > 1)
        {
            return s2;
        }
        else
        {
            return Coordinate.CreateCoordinate((int) Math.round(s1X + u * xDelta), (int) Math.round(s1Y + u * yDelta));
        }
    }

    public static double getRouteLength(Route route) {
        return getRouteLength(route, 0, route.getRoute().size()-1);
    }

    public static double getRouteLength(Route route, int startIndex, int endIndex) {
        List<PointInPath> coords = route.getRoute();
        // get total route length
        double totalDistance = 0;
        for(int i=startIndex; i<coords.size()-1 && i<endIndex;i++) {
            totalDistance += Math2D.getDistanceBetweenPoints(coords.get(i).getCoordinate(), coords.get(i+1).getCoordinate());
        }
        return totalDistance;
    }
}
