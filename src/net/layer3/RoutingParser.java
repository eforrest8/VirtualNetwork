package net.layer3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RoutingParser {

    public static RoutingConfig load() {
        Map<String, InetSocketAddress> routers = new HashMap<>();
        List<String> subnets = new LinkedList<>();
        Map<String, List<String>> connections = new HashMap<>();
        try (BufferedReader fr = new BufferedReader(new FileReader("layer3config.txt"))) {
            fr.lines().forEachOrdered(line -> {
                if (line.startsWith("router")) {
                    String name = line.substring(
                            line.indexOf(" ") + 1,
                            line.indexOf("="));
                    String ip = line.substring(
                            line.indexOf("=") + 1,
                            line.indexOf(":"));
                    String port = line.substring(line.indexOf(":") + 1);
                    routers.put(name, new InetSocketAddress(ip, Integer.parseInt(port)));
                } else if (line.startsWith("subnet")) {
                    String name = line.substring(line.indexOf(" ") + 1);
                    subnets.add(name);
                } else if (line.startsWith("connect")) {
                    String firstName = line.substring(
                            line.indexOf(" ") + 1,
                            line.indexOf(","));
                    String secondName = line.substring(line.indexOf(", ") + 1);
                    connections.putIfAbsent(firstName, new LinkedList<>());
                    connections.get(firstName).add(secondName);
                    connections.putIfAbsent(secondName, new LinkedList<>());
                    connections.get(secondName).add(firstName);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new RoutingConfig(routers, subnets, connections);
    }
}
