package net.layer3;

import java.io.Serializable;

public record Route(int distance, String nextHop) implements Serializable {}
