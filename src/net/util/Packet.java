package net.util;

import java.io.Serializable;

public interface Packet extends Serializable {
    String srcMAC();
    String dstMAC();
    String srcIP();
    String dstIP();
}
