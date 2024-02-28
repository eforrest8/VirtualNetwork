package net;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Switch extends ServerNode {

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
        while(true) {
            DatagramSocket serverSocket;

            try {
                serverSocket = new DatagramSocket(port);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

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
            String source = separatedMessage[0];

            if (separatedMessage.length == 4){
                newSource = separatedMessage[3];
            }
            else{
                newSource = source;
            }

            if (rt.getAddress(source) == null) {
                rt.updateTable(source, neighbors.get(newSource));
            }

            InetSocketAddress destination = rt.getAddress(separatedMessage[1]);
            var updatedMessage = createMessage(separatedMessage[0], separatedMessage[1], separatedMessage[2], id);

            if (destination == null) {
                try {
                    flood(newSource, updatedMessage);
                    serverSocket.close();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("flooding");

            } else {
                send(destination, updatedMessage);
                serverSocket.close();
                System.out.println("message sent");

            }
        }
    }

    public static void flood(String source, String message) throws UnknownHostException {
        for (String id: neighbors.keySet()){
            if (!id.equals(source) && !id.equals(newSource)){
                send(neighbors.get(id), message);
                System.out.println(id);
                System.out.println(newSource);
            }
        }
    }
}


