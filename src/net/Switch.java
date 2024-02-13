package net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Switch {
    public static void main(String[] args) throws Exception{
    // use parser to read config

    //public void readTable(){}

    //public void addToTable(){}

    //public void flood(){}

    //public void send(){}

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
}


