package net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Parser {

    String config;
    List neighborList = new ArrayList();
    Map<String, SocketAddress> parsed = new HashMap();
    //gets instantiated by pc/switch

    private String[] parseConfig(){
        try {
            config = Files.readString(Path.of("config.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var initArray = config.split(";");
        return initArray;
    }
    public Map getNeighbors(String id) {
        var initArray = parseConfig();
        var neighborArray = initArray[0].split("/");
        var ipArray = initArray[1].split("/");

        for (String s : neighborArray) {
                var n = s.split("=");
                if (n[0].contains(id)){
                neighborList.add(n[1]);
            }
        }

        System.out.println(neighborList);

        for (Object x : neighborList) {
            for (String s : ipArray){
                if (s.contains(x.toString())){
                    var array = s.split("=");
                    var ipPort = array[1].split(":");
                    parsed.put(array[0], new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1])));
                }
            }
        }

        return parsed;
    }

}
