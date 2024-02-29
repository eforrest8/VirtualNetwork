package net.layer3;

public class Router {

    public static void main(String... args) {
        RoutingConfig config = RoutingParser.load();
        Router me = new Router(config);
        me.start();
    }

    public Router(RoutingConfig config) {}

    public void start() {
        // initialize table with adjacent subnets w/ distance 0, nexthop self
        // send map to neighbors
        // use received maps to optimize own map
        // print out tables
    }
}
