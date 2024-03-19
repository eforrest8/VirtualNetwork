package net.layer3;

import java.util.List;

public class Router {

    private String id;
    private RoutingConfig config;
    public static void main(String... args) {
        RoutingConfig config = RoutingParser.load();
        Router me = new Router(config, args[0]);
        me.start();
    }

    public Router(RoutingConfig config, String id) {}

    public void start() {
        DistanceVector distanceVector = new DistanceVector();
        List<String> connectedSubnets = config.connectedSubnets(id);
        for (String subnet : connectedSubnets){
            distanceVector.addRecord(subnet, new Route(0, id));
        }
        //send map to adjacent routers
        // use received maps to optimize own map
        // print out tables
    }
}
