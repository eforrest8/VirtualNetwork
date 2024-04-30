package net.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Stream;

public class CombinedConfig {

    List<HostConfig> hosts = new LinkedList<>();
    List<SwitchConfig> switches = new LinkedList<>();
    List<RouterConfig> routers = new LinkedList<>();

    public CombinedConfig(String filename) {
        parse(filename);
    }

    private void parse(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty())
                    continue; //comment or empty; skip this line
                switch (line) {
                    case "host" -> parseHost(reader);
                    case "switch" -> parseSwitch(reader);
                    case "router" -> parseRouter(reader);
                    default -> throw new RuntimeException();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    public NetworkDevice getDeviceByMAC(String id) {
        return allDevices().filter(r -> r.vMAC().equals(id)).findAny().orElseThrow();
    }

    public NetworkDevice getDeviceByAddress(InetSocketAddress address) {
        return allDevices().filter(r -> r.address().equals(address)).findAny().orElseThrow();
    }

    public HostConfig getHostByMAC(String id) {
        return hosts.stream().filter(r -> r.vMAC().equals(id)).findAny().orElseThrow();
    }

    public SwitchConfig getSwitchByMAC(String id) {
        return switches.stream().filter(r -> r.vMAC().equals(id)).findAny().orElseThrow();
    }

    public RouterConfig getRouterByMAC(String id) {
        return routers.stream().filter(r -> r.vMAC().equals(id)).findAny().orElseThrow();
    }

    public Collection<RouterConfig> routerNeighbors(String id) { return routerNeighbors(getRouterByMAC(id)); }

    public Collection<RouterConfig> routerNeighbors(RouterConfig self) {
        return routers.stream()
                .filter(other -> other.subnetConnections().keySet().stream()
                        .anyMatch(self.subnetConnections().keySet()::contains)
                ).toList();
    }

    private Stream<NetworkDevice> allDevices() {
        return Stream.concat(hosts.stream(), Stream.concat(switches.stream(), routers.stream()));
    }

    private void parseRouter(BufferedReader reader) throws IOException, NullPointerException {
        String[] address = reader.readLine().trim().split(":");
        String vMAC = reader.readLine().trim();
        String connectionsLine = reader.readLine().trim();
        connectionsLine = connectionsLine.substring(1, connectionsLine.length()-1);
        Map<String, String> connections = Arrays.stream(connectionsLine.split(","))
                .map(e -> {
                    var a = e.split("=");
                    return Map.entry(a[0].trim(), a[1].trim());
                })
                .collect(HashMap::new, (acc, e) -> acc.put(e.getKey(), e.getValue()), HashMap::putAll);
        routers.add(new RouterConfig(
                        new InetSocketAddress(address[0],Integer.parseInt(address[1])),
                        vMAC,
                        connections
                )
        );
    }

    private void parseSwitch(BufferedReader reader) throws IOException, NullPointerException {
        String[] address = reader.readLine().trim().split(":");
        String vMAC = reader.readLine().trim();
        String connectionsLine = reader.readLine().trim();
        connectionsLine = connectionsLine.substring(1, connectionsLine.length()-1);
        String[] connections = connectionsLine.split(",");
        connections = Arrays.stream(connections).map(String::trim).toArray(String[]::new);
        switches.add(new SwitchConfig(
                        new InetSocketAddress(address[0],Integer.parseInt(address[1])),
                        vMAC,
                        connections
                )
        );
    }

    private void parseHost(BufferedReader reader) throws IOException, NullPointerException {
        String[] address = reader.readLine().trim().split(":");
        String vMAC = reader.readLine().trim();
        String vIP = reader.readLine().trim();
        String gateway = reader.readLine().trim();
        String connectionsLine = reader.readLine().trim();
        connectionsLine = connectionsLine.substring(1, connectionsLine.length()-1);
        String[] connections = connectionsLine.split(",");
        connections = Arrays.stream(connections).map(String::trim).toArray(String[]::new);
        hosts.add(new HostConfig(
                        new InetSocketAddress(address[0],Integer.parseInt(address[1])),
                        vMAC,
                        vIP,
                        gateway,
                        connections
                )
        );
    }

}

