package net.layer3;

public record Route(int distance, String nextHop) {
    public int byteLength() {
        return 4 + nextHop().length();
    }
}
