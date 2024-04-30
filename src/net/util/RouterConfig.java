package net.util;

import java.net.InetSocketAddress;
import java.util.Map;

public record RouterConfig(
        InetSocketAddress address,
        String vMAC,
        Map<String, String> subnetConnections
) implements NetworkDevice {}
