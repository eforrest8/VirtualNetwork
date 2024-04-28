package net;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RoutingTable {
    private final Map<String, InetSocketAddress> table = new HashMap<>();

    public InetSocketAddress getAddress(String destination) {
        return table.getOrDefault(destination, null);
    }

    public void updateTable(String destination, InetSocketAddress port) {
        table.put(destination, port);
    }
}
