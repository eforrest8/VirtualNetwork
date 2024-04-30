package net.util;

import java.net.InetSocketAddress;

public record HostConfig(
        InetSocketAddress address,
        String vMAC,
        String subnet,
        String gateway,
        String[] connections
) implements NetworkDevice {}
