package net;

public class PC {
    private int MAC;

    //uses parser to read config

    //we're expected to use threads so these can run concurrently
    public void send(){
        //create virtual frame for udp payload
        //this will include source mac, message, dest mac
    }

    public void createFrame(){}

    public void receive(){
        //reads to check if the packet is intended for this pc
    }


}
