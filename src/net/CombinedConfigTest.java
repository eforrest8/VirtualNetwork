package net;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CombinedConfigTest {

    @Test
    void parse() {
        CombinedConfig config = new CombinedConfig("combinedconfig.txt");
        HostConfig expectedHost = new HostConfig(
                new InetSocketAddress("127.0.0.1", 5001),
                "h1",
                "N1",
                "R1",
                new String[] {"s1"}
        );
        SwitchConfig expectedSwitch = new SwitchConfig(
                new InetSocketAddress("127.0.0.1", 6001),
                "s1",
                new String[] {"h1", "R1"}
        );
        RouterConfig expectedRouter = new RouterConfig(
                new InetSocketAddress("127.0.0.1", 7001),
                "R1",
                Map.of("N1", "s1", "N2", "s2")
        );
        assertEquals(
                expectedHost.address(),
                config.hosts.get(0).address());
        assertEquals(
                expectedHost.gateway(),
                config.hosts.get(0).gateway());
        assertEquals(
                expectedHost.vMAC(),
                config.hosts.get(0).vMAC());
        assertEquals(
                expectedHost.subnet(),
                config.hosts.get(0).subnet());
        assertTrue(
                Arrays.deepEquals(expectedHost.connections(), config.hosts.get(0).connections()));
        assertEquals(
                expectedSwitch.address(),
                config.switches.get(0).address());
        assertEquals(
                expectedSwitch.vMAC(),
                config.switches.get(0).vMAC());
        assertTrue(
                Arrays.deepEquals(expectedSwitch.connections(), config.switches.get(0).connections()));
        assertEquals(
                expectedRouter,
                config.routers.get(0));
    }
}