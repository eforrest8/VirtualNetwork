package net;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public record RouterConfig(
        InetSocketAddress address,
        String vMAC,
        Map<String, String> subnetConnections
) implements NetworkDevice {}
