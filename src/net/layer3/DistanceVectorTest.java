package net.layer3;

import static org.junit.jupiter.api.Assertions.*;

class DistanceVectorTest {

    @org.junit.jupiter.api.Test
    void updateRecordEmpty() {
        DistanceVector dv = new DistanceVector();
        assertTrue(dv.updateRecord("test", new Route(2, "next")));
        assertEquals(dv.distances.get("test").distance(), 2);
    }

    @org.junit.jupiter.api.Test
    void updateRecordShorter() {
        DistanceVector dv = new DistanceVector();
        assertTrue(dv.updateRecord("test", new Route(2, "next")));
        assertEquals(dv.distances.get("test").distance(), 2);
        assertTrue(dv.updateRecord("test", new Route(1, "other")));
        assertEquals(dv.distances.get("test").distance(), 1);
    }

    @org.junit.jupiter.api.Test
    void updateRecordEqual() {
        DistanceVector dv = new DistanceVector();
        assertTrue(dv.updateRecord("test", new Route(2, "next")));
        assertEquals(dv.distances.get("test").distance(), 2);
        assertFalse(dv.updateRecord("test", new Route(2, "other")));
        assertEquals(dv.distances.get("test").distance(), 2);
    }

    @org.junit.jupiter.api.Test
    void updateRecordLonger() {
        DistanceVector dv = new DistanceVector();
        assertTrue(dv.updateRecord("test", new Route(2, "next")));
        assertEquals(dv.distances.get("test").distance(), 2);
        assertFalse(dv.updateRecord("test", new Route(3, "other")));
        assertEquals(dv.distances.get("test").distance(), 2);
    }

    @org.junit.jupiter.api.Test
    void merge() {
    }
}