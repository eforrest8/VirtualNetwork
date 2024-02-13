package net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PC {
    private String MAC;
    private final Map<String, SocketAddress> topology = new HashMap<>();

    //uses parser to read config

    //we're expected to use threads so these can run concurrently
    public void send(){
        //create virtual frame for udp payload
        //this will include source mac, message, dest mac
        DatagramPacket dp = createFrame();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(dp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatagramPacket createFrame() {
        Scanner s = new Scanner(System.in);
        String message = s.nextLine();
        String destination = s.nextLine();
        ByteBuffer frame = ByteBuffer.allocate(message.length() + 8);
        frame.put(MAC.getBytes(StandardCharsets.UTF_8));
        frame.put(destination.getBytes(StandardCharsets.UTF_8));
        frame.put(message.getBytes(StandardCharsets.UTF_8));
        frame.flip();
        return new DatagramPacket(frame.array(), frame.capacity(), topology.get(destination));
    }

    public void receive() {
        SocketAddress listenPort = topology.get(this.MAC);
        DatagramPacket p = new DatagramPacket(new byte[2048], 2048);
        try (var socket = new DatagramSocket(listenPort)) {
            socket.receive(p);
            ByteBuffer data = ByteBuffer.wrap(p.getData());
            String sender = data.slice(0, 4).asCharBuffer().toString();
            String destination = data.slice(0,4).asCharBuffer().toString();
            String message = data.asCharBuffer().toString().trim();
            data.flip();
            //reads to check if the packet is intended for this pc
            if (!destination.equals(this.MAC)) {
                return;
            }
            System.out.println("Message from " + sender + ": " + message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
