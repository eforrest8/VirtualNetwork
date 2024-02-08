package net;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RoutingTable {
    private final Map<String, Integer> table = new HashMap<>();

    public Optional<Integer> getPortForDestination(String destination) {
        return table.containsKey(destination) ? Optional.of(table.get(destination)) : Optional.empty();
    }

    public void updatePortMapping(String destination, Integer port) {
        table.put(destination, port);
    }
}
