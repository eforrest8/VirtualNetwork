package net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Switch {

    private static String id = "S1";
    private static Map neighbors;

    public static void main(String[] args) throws Exception {

        Parser parser = new Parser();
        neighbors = parser.getNeighbors(id);

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
    }




        //public void readTable(){}

        //public void addToTable(){}

        //public void flood(){}

        //public void send(){}
}


