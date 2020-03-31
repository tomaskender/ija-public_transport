package data.implementations;

import data.interfaces.Line;
import data.interfaces.Stop;
import data.interfaces.Street;
import data.interfaces.Vehicle;

import java.time.LocalTime;
import java.util.*;

public class CONFIG {
    public static double EXPECTED_STOP_TIME = 2;
    public static LocalTime CURRENT_TIME;
    public static Map<String, Street> streets = new TreeMap<>();
    public static Map<String, Stop> stops = new TreeMap<>();
    public static Map<String, Line> lines = new TreeMap<>();
    public static Map<String, Vehicle> vehicles = new TreeMap<>();

}
