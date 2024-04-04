package net.layer3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Router {

    private final String id;
    private final RoutingConfig config;
    private final DistanceVector distanceVector;

    public static void main(String... args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments");
            System.exit(1);
        }
        System.out.println("initializing router " + args[0] + " with config file " + args[1]);
        RoutingConfig config = RoutingParser.load(args[1]);
        Router me = new Router(config, args[0]);
    }

    public Router(RoutingConfig config, String id) {
        this.config = config;
        this.id = id;
        this.distanceVector = new DistanceVector();
        List<String> connectedSubnets = config.connectedSubnets(id);
        initializeDistanceVector(connectedSubnets);
        Executor pool = Executors.newCachedThreadPool();
        pool.execute(this::listen);
    }

    public void listen() {
        try (DatagramSocket socket = new DatagramSocket(config.physicalAddressOf(id))) {
            propagateDistanceVector(socket);
            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);
                System.out.println("received packet");
                if (distanceVector.merge(new DistanceVector(packet.getData()),
                        config.idOf(new InetSocketAddress(packet.getAddress(), packet.getPort())))){
                    propagateDistanceVector(socket);
                    System.out.println("distance vector changed:");
                    System.out.println(distanceVector);
                } else {
                    System.out.println("distance vector unchanged");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeDistanceVector(List<String> connectedSubnets) {
        for (String subnet : connectedSubnets) {
            distanceVector.updateRecord(subnet, new Route(0, id));
        }
        System.out.println("Initialized distance vector as:");
        System.out.println(distanceVector);
    }

    private void propagateDistanceVector(DatagramSocket socket) throws IOException {
        System.out.println("propagating distance vector");
        for (String router : config.adjacentRouters(id)) {
            sendMap(router, socket);
        }
    }

    private void sendMap(String target, DatagramSocket socket) throws IOException {
        System.out.println("sending distance vector to " + target);
        socket.connect(config.physicalAddressOf(target));
        byte[] data = distanceVector.toBytes();
        socket.send(new DatagramPacket(data, data.length));
        socket.disconnect();
    }
}
