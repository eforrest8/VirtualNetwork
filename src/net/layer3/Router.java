package net.layer3;

import net.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Router {

    private final CombinedConfig config;
    private final RouterConfig self;
    private final DistanceVector distanceVector;
    private DatagramSocket socket;

    public static void main(String... args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments");
            System.exit(1);
        }
        System.out.println("initializing router " + args[0] + " with config file " + args[1]);
        CombinedConfig config = new CombinedConfig(args[1]);
        new Router(config, args[0]);
    }

    public Router(CombinedConfig config, String id) {
        this.config = config;
        this.distanceVector = new DistanceVector();
        this.self = config.getRouterByMAC(id);
        initializeDistanceVector();
        new Thread(this::listen).start();
    }

    private void listen() {
        try {
            socket = new DatagramSocket(self.address());
            propagateDistanceVector();
            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);
                System.out.println("received packet");
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()))) {
                    switch (ois.readObject()) {
                        case StringPacket p -> handlePacket(p);
                        case DistanceVectorPacket dvp -> handleDistanceVector(
                                dvp.payload(),
                                config.getDeviceByAddress(new InetSocketAddress(packet.getAddress(), packet.getPort()))
                        );
                        default -> throw new RuntimeException();
                    }
                } catch (Exception ignored) {
                    System.out.println("Received invalid packet, discarding...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            socket.close();
        }
    }

    private void handlePacket(StringPacket p) throws IOException {
        System.out.println("Processing packet... " + p);
        String targetSubnet = p.dstIP().split("\\.")[0];
        String finalDestination = p.dstIP().split("\\.")[1];
        var nextHop = distanceVector.distances.get(targetSubnet).nextHop();
        String destination;
        if (nextHop.equals(self.vMAC())) {
            destination = finalDestination;
        } else {
            destination = nextHop;
        }
        self.subnetConnections().get(targetSubnet);
        StringPacket forwarded = new StringPacket(self.vMAC(), destination, p.srcIP(), p.dstIP(), p.payload());
        sendTo(config.getDeviceByMAC(nextHop).address(), forwarded);
    }

    private void initializeDistanceVector() {
        for (String subnet : self.subnetConnections().keySet()) {
            distanceVector.updateRecord(subnet, new Route(0, self.vMAC()));
        }
        System.out.println("Initialized distance vector as:");
        System.out.println(distanceVector);
    }

    private void handleDistanceVector(DistanceVector dv, NetworkDevice sender) throws IOException {
        if (distanceVector.merge(dv, sender.vMAC())) {
            propagateDistanceVector();
            System.out.println("distance vector changed:");
            System.out.println(distanceVector);
        } else {
            System.out.println("distance vector unchanged");
        }
    }

    private void propagateDistanceVector() throws IOException {
        System.out.println("propagating distance vector");
        for (RouterConfig router : config.routerNeighbors(self)) {
            sendMap(router);
        }
    }

    private void sendMap(RouterConfig target) throws IOException {
        System.out.println("sending distance vector to " + target);
        String sharedSubnet = self.subnetConnections().keySet().stream()
                .filter(target.subnetConnections().keySet()::contains)
                .findAny()
                .orElseThrow();
        var dvp = new DistanceVectorPacket(
                self.vMAC(),
                target.vMAC(),
                sharedSubnet + "." + self.vMAC(),
                sharedSubnet + "." + target.vMAC(),
                distanceVector
                );
        sendTo(target.address(), dvp);
    }

    private void sendTo(InetSocketAddress target, Packet payload) throws IOException {
        var out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(payload);
        socket.connect(target);
        socket.send(new DatagramPacket(out.toByteArray(), out.size()));
        socket.disconnect();
    }

}
