package net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Switch {

    private static String id;
    private static List neighbors = new ArrayList<>();
    private static String[] ips;

    public static void main(String[] args) throws Exception{

        Parser parser = new Parser();
        List neighbors = parser.getNeighbors(id);
        ips = parser.getIP();

        DatagramSocket serverSocket = new DatagramSocket(3000);
        DatagramPacket clientRequest = new DatagramPacket(
                new byte[1024],
                1024);
        serverSocket.receive(clientRequest);
        byte[] clientMessage = Arrays.copyOf(
                clientRequest.getData(),
                clientRequest.getLength()
        );

        String serverResponse = new String(clientMessage);
        System.out.println(serverResponse);
        serverSocket.close();

        // use parser to read config

        //public void readTable(){}

        //public void addToTable(){}

        //public void flood(){}

        //public void send(){}
    }
}


