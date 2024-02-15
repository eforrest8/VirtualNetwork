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
    private int MAC;
    private static String id = "A";
    private static Map neighbors;

    public static void main(String[] args) throws Exception {
        Parser parser = new Parser();
        neighbors = parser.getNeighbors(id);

        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 3000);

        while (true){
            System.out.println("Would you like to send a message?");
            Scanner keyboard = new Scanner(System.in);
            String response = keyboard.nextLine();
            Listener l = new Listener(3000, id);
            if (response.equals("q")){
                sending.shutdown();
                receiving.shutdown();
            } else if (response.equals("y")) {
                String message = createMessage(id);
                Sender s = new Sender(address, message);
                sending.submit(s);
            }
            receiving.submit(l);
        }

    }

}


