package net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PC {
    //needs to be able to send and receive UDP
    private int MAC;
    private static String id = "A";
    private static List neighbors = new ArrayList();
    private static String[] ips;
    public static void main(String[] args) throws Exception {

        Parser parser = new Parser();
        neighbors = parser.getNeighbors(id);
        ips = parser.getIP();

        InetAddress serverIP = InetAddress.getByName(args[0]);
        int serverPort = Integer.parseInt(args[1]);

        if(args.length != 2){
            System.out.println("Syntax: EchoClient <serverIP> <serverPort>");
            return;
        }

        System.out.println("Type your message below.");
        Scanner keyboard = new Scanner(System.in);

        String message = keyboard.nextLine();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket request = new DatagramPacket(message.getBytes(),
                message.getBytes().length,
                serverIP,
                serverPort
        );
        socket.send(request);
        socket.close();


        //uses parser to read config
        //we're expected to use threads so these can run concurrently

        //create virtual frame for udp payload
        //this will include source mac, message, dest mac

        //public void createFrame(){}

        //public void receive(){

        //reads to check if the packet is intended for this pc
    }
}


