package data.interfaces;

import data.implementations.PointInPath;

import java.util.List;

/**
 * Route of a vehicle.
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public interface Route {
    /**
     * Route constructor
     * @param streets vector of given streets on route
     * @param firstStop first stop on route
     * @param secondStop second stop on route
     * @param deltaTimeInMins time to get to stop
     * @return true if route was sucessfuly constructed, otherwise false
     */
    boolean ConstructRoute(List<Street> streets, PointInPath firstStop, PointInPath secondStop, double deltaTimeInMins);

    /**
     * Get route points
     * @return route points
     */
    List<PointInPath> getRoute();

    /**
     * Get time to get to to next stop
     * @return time in double
     */
    double getExpectedDeltaTime();

    /**
     * Set route time
     * @param delta given route time
     */
    void SetExpectedDeltaTime(double delta);
}
