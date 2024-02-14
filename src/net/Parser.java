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
    List ipArray = new ArrayList();
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
    public List getNeighbors(String id){
        var initArray = parseConfig();
        var neighborArray = initArray[0].split("/");


        for (String s : neighborArray) {
            if(s.contains(id)) {
                var n = s.split("=");
                neighborList.add(n[1]);
            }
        }

        return neighborList;
    }

    public String[] getIP(){
        var initArray = parseConfig();
        var ipArray = initArray[1].split("/");
        return ipArray;
    }

}
