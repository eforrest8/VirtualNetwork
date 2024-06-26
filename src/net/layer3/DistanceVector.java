package net.layer3;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DistanceVector implements Serializable {
    /**
     * Mapping of subnets to routes
     */
    Map<String, Route> distances = new HashMap<>();

    /**
     * <p>
     *     my logic here was not correct ;.;
     * </p>
     */
    public boolean updateRecord(String subnet, Route routeB) {
        if (distances.containsKey(subnet)) {
            var oldRoute = distances.get(subnet);
            var newRoute = distances.computeIfPresent(subnet, (key, routeA) ->
                    routeA.distance() < routeB.distance() ? routeA : routeB);
            return !Objects.equals(oldRoute, newRoute);
        }
        distances.put(subnet, routeB);
        return true;
    }

    public boolean merge(DistanceVector other, String sender) {
        boolean mapChanged = false;
        //receives other distance vector
        for (String subnet : other.distances.keySet()){
            Route oldRoute = other.distances.get(subnet);
            Route newRoute = new Route(oldRoute.distance() + 1, sender);
            if (updateRecord(subnet, newRoute)){
                mapChanged = true;
            }
        }
            return mapChanged;
    }

    public String toString() {
        return distances.toString();
    }
}
