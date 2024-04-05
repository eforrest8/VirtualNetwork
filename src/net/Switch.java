package net;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Switch extends ServerNode{

    private static String id;
    private static int port;
    private static Map<String, InetSocketAddress> neighbors;
    private static  RoutingTable rt = new RoutingTable();

    private static String[] separatedMessage;

    private static String newSource;
    public static void main(String[] args) throws Exception {
        id = args[0];
        Parser parser = new Parser();
        neighbors = parser.getNeighbors(id);
        port = parser.getPortById(id);
        System.out.println(neighbors);
        DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while(true) {
            DatagramPacket clientRequest = new DatagramPacket(
                    new byte[1024],
                    1024);

            try {
                serverSocket.receive(clientRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            byte[] clientMessage = Arrays.copyOf(
                    clientRequest.getData(),
                    clientRequest.getLength()
            );

            String originalMessage = new String(clientMessage);
            separatedMessage = originalMessage.split("/");
            String sourceId = separatedMessage[0];


            if (rt.getAddress(sourceId) == null) {
                rt.updateTable(sourceId, neighbors.get(sourceId));
            }

            InetSocketAddress destination = rt.getAddress(separatedMessage[1]);
            InetSocketAddress sourceAddress = new InetSocketAddress(clientRequest.getAddress(),
                    clientRequest.getPort());

            if (destination == null) {
                try {
                    flood(sourceId, originalMessage, serverSocket, sourceAddress);
                    serverSocket.disconnect();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("flooding");

            } else {
                send(destination, originalMessage, serverSocket);
                serverSocket.disconnect();
                System.out.println("message sent");

            }
        }
    }

    public static void flood(String sourceId, String message, DatagramSocket socket, InetSocketAddress sourceAddress) throws UnknownHostException {
        for (String id: neighbors.keySet()){
            if (!id.equals(sourceId) && !neighbors.get(id).equals(sourceAddress)){
                send(neighbors.get(id), message, socket);
                System.out.println(id);
                System.out.println(newSource);
            }
        }
    }

    public static void send(InetSocketAddress destination, String message, DatagramSocket socket){

        DatagramPacket request = new DatagramPacket(message.getBytes(),
                message.getBytes().length,
                destination
        );

        try {
            socket.send(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        socket.disconnect();
    }
}


