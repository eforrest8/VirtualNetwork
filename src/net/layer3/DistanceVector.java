package net.layer3;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DistanceVector {
    /**
     * Mapping of subnets to routes
     */
    Map<String, Route> distances = new HashMap<>();

    public DistanceVector() {}

    public DistanceVector(byte[] serialized) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized))) {
            if (ois.readObject() instanceof Map map) {
                distances = (Map<String, Route>) map;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    byte[] toBytes() {
        try (var baos = new ByteArrayOutputStream()) {
            var oos = new ObjectOutputStream(baos);
            oos.writeObject(distances);
            oos.flush();
            oos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <h1>lol i'm doing this in the most unreadable way imaginable</h1>
     * <p>
     *     this is a boolean OR. the left term is true when the old route
     *     to this subnet is not equal to the one we get after running the
     *     inner portion. (i.e. the route changed.)
     *     the right term is true when no route was previously
     *     associated with this subnet. (i.e. the route changed.)
     *     If either is true, we return true; otherwise we return false.
     * </p>
     * <p>i hope my logic here is correct >.<</p>
     */
    public boolean updateRecord(String subnet, Route route) {
        return !Objects.equals(distances.computeIfPresent(subnet, (key, oldRoute) ->
                oldRoute.distance() <= route.distance() ? oldRoute : route), distances.get(subnet)) ||
        distances.putIfAbsent(subnet, route) == null;
    }

    public boolean merge(DistanceVector other, String sender) {
        boolean mapChanged = false;
        //recieves other distance vector
        for (String subnet : other.distances.keySet()){
            Route oldRoute = other.distances.get(subnet);
            Route newRoute = new Route(oldRoute.distance() + 1, sender);
            if (updateRecord(subnet, newRoute)){
                mapChanged = true;
            };
        }
            return mapChanged;
    }

    public String toString() {
        return distances.toString();
    }
}
