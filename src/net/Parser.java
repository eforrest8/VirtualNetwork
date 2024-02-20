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

    private String[] splitConfig(){
        try {
            config = Files.readString(Path.of("config.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var initArray = config.split(";");
        return initArray;
    }
    public Map getNeighbors(String id) {
        var addressAndNeighbor = splitConfig();
        var neighborArray = addressAndNeighbor[0].split("/");
        var addressArray = addressAndNeighbor[1].split("/");

        for (String s : neighborArray) {
                var n = s.split("=");
                if (n[0].equals(id)){
                neighborList.add(n[1]);
            }
        }

        for (Object x : neighborList) {
            for (String s : addressArray){
                if (s.contains(x.toString())){
                    var idRemoved = s.split("=");
                    var ipPort = idRemoved[1].split(":");
                    parsed.put(idRemoved[0], new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1])));
                }
            }
        }

        return parsed;
    }

    public int getPortById(String id){
        var addressAndNeighbor = splitConfig();
        var addressArray = addressAndNeighbor[1].split("/");

        for (String s : addressArray) {
            var n = s.split("=");
            if (n[0].equals(id)){
                String[] ipPort = n[1].split(":");
                return Integer.parseInt(ipPort[1]);
            }
        }
        return 0;
    }

}
