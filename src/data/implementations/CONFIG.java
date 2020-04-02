package data.implementations;

import data.interfaces.Line;
import data.interfaces.Stop;
import data.interfaces.Street;
import data.interfaces.Vehicle;
import sample.Controller;

import java.time.LocalTime;
import java.util.*;

public class CONFIG {
    public static double EXPECTED_STOP_TIME = 2;
    public static LocalTime CURRENT_TIME = LocalTime.parse("08:59:56");
    public static final int DELTA = 1;
    public static Map<String, Street> streets = new LinkedHashMap<>();
    public static Map<String, Stop> stops = new LinkedHashMap<>();
    public static Map<String, Line> lines = new LinkedHashMap<>();
    public static Map<String, Vehicle> vehicles = new LinkedHashMap<>();
    public static Controller controller;
}
