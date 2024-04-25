package net;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CombinedConfigTest {

    @Test
    void parse() {
        CombinedConfig config = new CombinedConfig("combinedconfig.txt");
        assertEquals(
                new HostConfig(
                        new InetSocketAddress("127.0.0.1", 5001),
                        "h1",
                        "N1",
                        "R1",
                        new String[] {"s1"}
                        ),
                config.hosts.get(0));
        assertEquals(
                new SwitchConfig(
                        new InetSocketAddress("127.0.0.1", 6001),
                        "s1",
                        new String[] {"h1", "R1"}
                ),
                config.switches.get(0));
        assertEquals(
                new RouterConfig(
                        new InetSocketAddress("127.0.0.1", 7001),
                        "R1",
                        Map.of("N1", "s1", "N2", "s2")
                ),
                config.routers.get(0));
    }
}