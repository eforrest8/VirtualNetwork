package net.util;

public record StringPacket(
        String srcMAC,
        String dstMAC,
        String srcIP,
        String dstIP,
        String payload
) implements Packet {
}
