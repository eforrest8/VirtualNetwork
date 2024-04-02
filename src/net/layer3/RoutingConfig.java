package net.layer3;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RoutingConfig {
    Map<String, InetSocketAddress> routers;
    List<String> subnets;
    Map<String, List<String>> connections;

    public RoutingConfig(
            Map<String, InetSocketAddress> routers,
            List<String> subnets,
            Map<String, List<String>> connections
    ) {
        this.routers = routers;
        this.subnets = subnets;
        this.connections = connections;
    }

    public List<String> connectedSubnets(String id) {
        return connections.getOrDefault(id, List.of()).stream()
                .filter(subnets::contains)
                .toList();
    }

    public List<String> adjacentRouters(String id) {
        return this.connectedSubnets(id).stream()
                .flatMap(subnet -> connections.getOrDefault(subnet, List.of()).stream())
                .filter(Predicate.not(id::equals))
                .toList();
    }

    public InetSocketAddress physicalAddressOf(String id) {
        return routers.get(id);
    }

    public String idOf(InetSocketAddress address) {
        return routers.entrySet().stream()
                .filter(e -> e.getValue().equals(address))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow();
    }
}
