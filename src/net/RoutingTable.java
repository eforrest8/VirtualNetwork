package net;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RoutingTable {
    private final Map<String, Integer> table = new HashMap<>();

    /**
     * If a destination node is known to the routing table,
     * retrieve the port associated with it.
     * @param destination The name of the node we are looking for.
     * @return If found, an Optional containing the port associated
     * with the given destination. Otherwise, an empty Optional.
     */
    public Optional<Integer> getPortForDestination(String destination) {
        return table.containsKey(destination) ? Optional.of(table.get(destination)) : Optional.empty();
    }

    /**
     * Update the routing table to associate a destination with a
     * particular port.
     * @param destination The name of a node.
     * @param port The port associated with the destination.
     */
    public void updatePortMapping(String destination, Integer port) {
        table.put(destination, port);
    }
}
