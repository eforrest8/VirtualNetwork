package net.util;

import java.net.InetSocketAddress;

public record SwitchConfig(
        InetSocketAddress address,
        String vMAC,
        String[] connections
) implements NetworkDevice {}
