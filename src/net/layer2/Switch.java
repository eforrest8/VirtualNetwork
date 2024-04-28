package net.layer2;

import net.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class Switch {
    private final CombinedConfig config;
    private final SwitchConfig self;
    private DatagramSocket socket;
    private final Map<String, InetSocketAddress> switchingTable = new HashMap<>();

    public static void main(String... args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments");
            System.exit(1);
        }
        System.out.println("initializing switch " + args[0] + " with config file " + args[1]);
        CombinedConfig config = new CombinedConfig(args[1]);
        new Switch(config, args[0]);
    }

    public Switch(CombinedConfig config, String id) {
        this.config = config;
        this.self = config.getSwitchByMAC(id);
        new Thread(this::listen).start();
    }

    private void listen() {
        try {
            socket = new DatagramSocket(self.address());
            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()))) {
                    Object rawObject = ois.readObject();
                    if (rawObject instanceof Packet p) {
                        switchingTable.put(p.srcMAC(), new InetSocketAddress(packet.getAddress(), packet.getPort()));
                        Optional.ofNullable(switchingTable.get(p.dstMAC())).ifPresentOrElse(target -> forward(p, target), flood(p));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            socket.close();
        }
    }

    private void forward(Packet p, InetSocketAddress target) {
        try {
            socket.connect(target);
            var out = new ByteArrayOutputStream();
            new ObjectOutputStream(out).writeObject(p);
            socket.connect(target);
            socket.send(new DatagramPacket(out.toByteArray(), out.size()));
            socket.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            socket.disconnect();
        }
    }

    private Runnable flood(Packet p) {
        return () -> Arrays.stream(self.connections())
                .filter(Predicate.not(mac -> mac.equals(self.vMAC())))
                .map(config::getDeviceByMAC)
                .map(NetworkDevice::address)
                .forEach(target -> forward(p, target));
    }
}
