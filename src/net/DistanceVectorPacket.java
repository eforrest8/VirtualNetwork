package net;

import net.layer3.DistanceVector;

public record DistanceVectorPacket(
        String srcMAC,
        String dstMAC,
        String srcIP,
        String dstIP,
        DistanceVector payload
) implements Packet {
}
