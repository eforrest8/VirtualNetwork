package net;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Switch extends ServerNode {

    private static String id;
    private static Map<String, InetSocketAddress> neighbors;
    private static  RoutingTable rt = new RoutingTable();

    private static String[] separatedMessage;
    public static void main(String[] args) throws Exception {
        id = args[0];
        Parser parser = new Parser();
        neighbors = parser.getNeighbors(id);
        System.out.println(neighbors);
        SwitchListener l = new SwitchListener(parser.getPortById(id), id);
        receiving.submit(l);
    }


    static class SwitchListener implements Runnable{
        private int port;
        private String id;

        public SwitchListener( int port, String id){
            this.port = port;
            this.id = id;
        }

        @Override
        public void run () {
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


                if (rt.getAddress(source) == null) {
                    rt.updateTable(source, neighbors.get(source));
                }



                InetSocketAddress destination = rt.getAddress(separatedMessage[1]);

                String createdMessage = createMessage(id, separatedMessage[1], separatedMessage[2]);

                if (destination == null) {
                    try {
                        System.out.println(createdMessage);
                        flood(source, createdMessage);
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("flooding");
                } else {
                    send(destination, createdMessage);
                    System.out.println("message sent");
                }
                serverSocket.close();
            }
        }
}

    public static void flood(String source, String message) throws UnknownHostException {
        for (String s: neighbors.keySet()){
            if (!s.equals(source)){
                send(neighbors.get(s), message);
                System.out.println(message);
            }
        }
    }

}


