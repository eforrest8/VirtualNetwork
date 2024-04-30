package net.util;

import java.net.InetSocketAddress;

public interface NetworkDevice {
    String vMAC();
    InetSocketAddress address();
}
