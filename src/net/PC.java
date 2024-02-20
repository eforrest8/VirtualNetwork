package net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class PC extends ServerNode {
    //needs to be able to send and receive UDP
    private static String mySwitch;
    private static String id;
    private static Map<String, InetSocketAddress> neighbors;

    public static void main(String[] args) throws Exception {
        id = args[0];
        Parser parser = new Parser();
        neighbors = parser.getNeighbors(id);
        for (String n : neighbors.keySet()){
            mySwitch = n;
        }

        while (true){
            Listener l = new Listener(parser.getPortById(id), id);
            receiving.submit(l);
            System.out.println("Would you like to send a message?");
            Scanner keyboard = new Scanner(System.in);
            String response = keyboard.nextLine();
            if (response.equals("q")){
                receiving.shutdown();
            } else if (response.equals("y")) {
                System.out.println("Type your message below.");
                String message = keyboard.nextLine();
                System.out.println("Type the address of the recipient");
                String receiver = keyboard.nextLine();
                String packet = createMessage(id, receiver, message);
                send(neighbors.get(mySwitch), packet);
            }

        }

    }

}


