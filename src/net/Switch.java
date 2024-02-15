package net;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Switch extends ServerNode {

    private static String id = "S1";
    private static Map<String, InetSocketAddress> neighbors;
    private static  RoutingTable rt = new RoutingTable();

    private static String[] separatedMessage;
    public static void main(String[] args) throws Exception {

        Parser parser = new Parser();
        neighbors = parser.getNeighbors(id);
        SwitchListener l = new SwitchListener(3000, id);
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

        String serverResponse = new String(clientMessage);

        separatedMessage = serverResponse.split("/");


        if(rt.getPortForDestination(separatedMessage[0]).isEmpty()){
            rt.updatePortMapping(separatedMessage[0], neighbors.get(separatedMessage[0]));
        }


        Optional<InetSocketAddress> port = rt.getPortForDestination(separatedMessage[1]);

        if (port.isEmpty()) {
            try {
                flood(separatedMessage[0], separatedMessage[2]);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            System.out.println("flooding");
        }
        else{
            InetSocketAddress a = null;

            try {
                a = new InetSocketAddress(InetAddress.getLocalHost(), 3000);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            Sender x = new Sender(a, separatedMessage[2]);
            sending.submit(x);
        }
        serverSocket.close();

    }
}

    public static void flood(String source, String message) throws UnknownHostException {
        for (String s: neighbors.keySet()){
            if (!s.equals(source)){
                InetSocketAddress a = new InetSocketAddress(InetAddress.getLocalHost(), 3000);
                Sender x = new Sender(a, message);
                sending.submit(x);
            }
        }
    }

}


