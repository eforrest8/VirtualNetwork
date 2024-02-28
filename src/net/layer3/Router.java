package net.layer3;

public class Router {

    public static void main(String... args) {
        RoutingConfig config = RoutingParser.load();
        Router me = new Router(config);
        me.start();
    }

    public Router(RoutingConfig config) {}

    public void start() {}
}
