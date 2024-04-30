package net.layer2;

import net.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.*;

public class Host {
    private final CombinedConfig config;
    private final HostConfig self;
    private DatagramSocket socket;
    private final Map<String, InetSocketAddress> switchingTable = new HashMap<>();

    public static void main(String... args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments");
            System.exit(1);
        }
        System.out.println("initializing host " + args[0] + " with config file " + args[1]);
        CombinedConfig config = new CombinedConfig(args[1]);
        new Host(config, args[0]);
    }

    public Host(CombinedConfig config, String id) {
        this.config = config;
        this.self = config.getHostByMAC(id);
        new Thread(this::listen).start();
        new Thread(this::userInput).start();
    }

    private void listen() {
        try {
            socket = new DatagramSocket(self.address());
            socket.setSoTimeout(1000);
            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException ignored) {
                    Thread.sleep(100);
                    continue;
                }
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()))) {
                    Object rawObject = ois.readObject();
                    if (rawObject instanceof StringPacket p) {
                        if (p.dstMAC().equals(self.vMAC())) {
                            System.out.println(p.payload());
                        } else {
                            System.out.println("received a package not intended for this device");
                        }
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
    private StringPacket createPacket(String dstIP, String payload){
        String srcMAC = self.vMAC();
        String srcIP = self.subnet().concat(".").concat(self.vMAC());
        String dstMAC = dstIP.split("\\.")[0].equals(self.subnet())
                ? dstIP.split("\\.")[1]
                : self.gateway();
        return new StringPacket(srcMAC, dstMAC, srcIP, dstIP, payload);
    }

    private void userInput(){
        Scanner keyboard = new Scanner(System.in);

        while (true) {
            System.out.println("Would you like to send a message? (y/n/q)");
            String response = keyboard.nextLine().toLowerCase(Locale.ROOT);
            if (response.equals("q") || response.equals("n")) {
                System.exit(0);
            } else if (response.equals("y")) {
                System.out.println("Type your message below.");
                String message = keyboard.nextLine().trim();
                System.out.println("Type the address of the recipient.");
                String receiver = keyboard.nextLine().trim();
                if (validate(receiver)) {
                    StringPacket packet = createPacket(receiver, message);
                    forward(packet, config.getDeviceByMAC(self.connections()[0]).address());
                } else {
                    System.out.println("Invalid address format. Please use the format SUBNET.MAC");
                }
            }
        }
    }

    private boolean validate(String receiver) {
        return receiver.split("\\.").length == 2;
    }
}

