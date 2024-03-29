package net.layer3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.Executors;

public class Router {

    private final String id;
    private final RoutingConfig config;
    private final DistanceVector distanceVector;

    public static void main(String... args) {
        RoutingConfig config = RoutingParser.load();
        Router me = new Router(config, args[0]);
    }

    public Router(RoutingConfig config, String id) {
        this.config = config;
        this.id = id;
        this.distanceVector = new DistanceVector();
        List<String> connectedSubnets = config.connectedSubnets(id);
        initializeDistanceVector(connectedSubnets);
        propagateDistanceVector();
        Executors.newCachedThreadPool().execute(this::listen);
        // use received maps to optimize own map
        // print out tables
    }

    public void listen() {
        while (true) {
            try (DatagramSocket socket = new DatagramSocket(config.physicalAddressOf(id))) {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);
                distanceVector.merge(new DistanceVector(packet.getData(),//recieve who sent the distance vector));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void initializeDistanceVector(List<String> connectedSubnets) {
        for (String subnet : connectedSubnets) {
            distanceVector.updateRecord(subnet, new Route(0, id));
        }
    }

    private void propagateDistanceVector() {
        for (String router : config.adjacentRouters(id)) {
            sendMap(router);
        }
    }

    private void sendMap(String target) {
        try (DatagramSocket socket = new DatagramSocket(config.physicalAddressOf(id))) {
            socket.connect(config.physicalAddressOf(target));
            byte[] data = distanceVector.toBytes();
            socket.send(new DatagramPacket(data, data.length));
            socket.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
