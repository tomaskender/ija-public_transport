package data.implementations;

import data.interfaces.Line;
import data.interfaces.Stop;
import data.interfaces.Street;
import data.interfaces.Vehicle;
import sample.Controller;

import java.time.LocalTime;
import java.util.*;

/**
 * Configuration values
 *
 * @author Tomas Duris
 * @author Tomas Kender
 */
public class CONFIG {
    public static final double EXPECTED_STOP_TIME = 60; // in seconds
    public static LocalTime CURRENT_TIME = LocalTime.parse("06:00");
    public static int DELTA = 60; // in seconds
    public static final double SIM_DELTA = 0.01; // update vehicle state every x seconds

    //------------------Don't mess with this---------------------------
    public static final Map<String, Street> streets = new LinkedHashMap<>();
    public static final Map<String, Stop> stops = new LinkedHashMap<>();
    public static final Map<String, Line> lines = new LinkedHashMap<>();
    public static final Map<String, Vehicle> vehicles = new LinkedHashMap<>();
    public static Controller controller;
}
