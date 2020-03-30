package data.implementations;

import data.interfaces.Line;
import data.interfaces.Stop;
import data.interfaces.Street;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CONFIG {
    public static double EXPECTED_STOP_TIME = 2;
    public static LocalTime CURRENT_TIME;
    public static Map<String, Street> streets = new HashMap<>();
    public static Map<String, Stop> stops = new HashMap<>();
    public static Map<String, Line> lines = new HashMap<>();
}
