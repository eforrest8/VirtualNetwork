package net;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNode {
    public static ExecutorService sending = Executors.newFixedThreadPool(3);
    public static ExecutorService receiving = Executors.newFixedThreadPool(3);

    static class Listener implements Runnable {
        private int port;
        private String id;

        public Listener(int port, String id){
            this.port = port;
            this.id = id;
        }

        @Override
        public void run() {
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

            System.out.println(serverResponse);

            var separatedMessage = serverResponse.split("/");

            if (separatedMessage[1].equals(id)){
                System.out.println("\nMessage:\n" + separatedMessage[2]);
            }

            serverSocket.close();
        }

    }


    public static void send(InetSocketAddress destination, String message){
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        DatagramPacket request = new DatagramPacket(message.getBytes(),
                message.getBytes().length,
                destination
        );

        try {
            socket.send(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        socket.close();
    }


    public static String createMessage(String id, String receiver, String message){
        String dataToSend = id + "/" + receiver + "/" + message;
        return dataToSend;

    }

}
