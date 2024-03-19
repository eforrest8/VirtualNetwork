package net.layer3;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class DistanceVector {
    /**
     * Mapping of subnets to routes
     */
    Map<String, Route> distances = new HashMap<>();

    byte[] toBytes() {
        ByteBuffer buf = ByteBuffer.allocate(byteLength());
        return buf.array();
    }

    private int byteLength() {
        return distances.entrySet().stream()
                .mapToInt(e -> e.getKey().length() + e.getValue().byteLength())
                .sum();
    }
}

record Route(int distance, String nextHop) {
    public int byteLength() {
        return 4 + nextHop().length();
    }
}
