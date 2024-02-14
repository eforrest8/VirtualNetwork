package net;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNode {
    private static ExecutorService sending = Executors.newFixedThreadPool(3);
    private static ExecutorService receiving = Executors.newFixedThreadPool(3);

    static class Listener implements Runnable {
        private InetSocketAddress reception;

        public Listener(InetSocketAddress address){
            this.reception = address;
        }

        @Override
        public void run() {
            DatagramSocket serverSocket;

            try {
                serverSocket = new DatagramSocket(reception);
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
            serverSocket.close();
        }

    }

    static class Sender implements Runnable{

        private InetSocketAddress destination;

        public Sender(InetSocketAddress address){
            this.destination = address;
        }

        @Override
        public void run() {

            System.out.println("Type your message below.");
            Scanner keyboard = new Scanner(System.in);

            String message = keyboard.nextLine();
            DatagramSocket socket = null;

            try {
                socket = new DatagramSocket(destination);
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

    }


    static class Shutdown implements Runnable{

        @Override
        public void run(){
            sending.shutdown();
            receiving.shutdown();
        }
    }

    public static void main (String[] args) {
        InetSocketAddress send;
        try {
            send = new InetSocketAddress(InetAddress.getLocalHost(), 4000);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        InetSocketAddress listen;
        try {
            listen = new InetSocketAddress(InetAddress.getLocalHost(), 3000);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        Sender sender = new Sender(send);
        Listener listener = new Listener(listen);

        sending.execute(sender);
    }
}
