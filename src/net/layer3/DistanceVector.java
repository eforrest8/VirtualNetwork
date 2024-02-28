package net.layer3;

import java.util.HashMap;
import java.util.Map;

public class DistanceVector {
    /**
     * Mapping of subnets to routes
     */
    Map<String, Route> distances = new HashMap<>();
}

record Route(int distance, String nextHop) {}
