package net;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {

    String config;
    List neighborList = new ArrayList();
    //gets instantiated by pc/switch

    public void parseConfig(String id){
        //this method will get called and the argument passed to this method will be the id of the node using it
        //it will return the node's neighbors, and their ip's and real ports
        try {
            config = Files.readString(Path.of("config.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var initArray = config.split(";");
        var neighborArray = initArray[0].split("/");
        var ipArray = initArray[1].split("/");


        for (String s : neighborArray) {
            if(s.contains(id)) {
                var n = s.split("=");
                neighborList.add(n[1]);
            }
        }

        System.out.println(neighborList);

        for (String s: ipArray){
            System.out.println(s);
        }


    }

    public static void main(String[] args) throws Exception{
        Parser p = new Parser();
        p.parseConfig("A");
    }
}
