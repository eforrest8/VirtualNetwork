package net.layer3;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

    public void updateRecord(String subnet, Route route) {
        distances.computeIfPresent(subnet, (key, oldRoute) ->
                oldRoute.distance() <= route.distance() ? oldRoute : route);
        distances.putIfAbsent(subnet, route);
    }

    public void merge(DistanceVector other, String sender) {
        //recieves other distance vector
        for (String subnet : other.distances.keySet()){
            if(!this.distances.containsKey(subnet)){
                Route oldRoute = other.distances.get(subnet);
                Route newRoute = new Route(oldRoute.distance() + 1, sender);
                updateRecord(subnet, newRoute);
            }
        }
            //send updated table
    }
}
