package org.abraao.concurrency;

import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

public class LinearConcurrentHashMapTest {

    ConcurrentMap<String, String> map = new LinearConcurrentHashMap<>();

    @Test
    public void size() {
        map.put("key", "value");
        map.put("key", "value2");
        assertEquals(1, map.size());
    }

    @Test
    public void isEmpty() {
        assertTrue(map.isEmpty());
    }

    @Test
    public void containsKey() {
        map.put("key3", "value");
        assertTrue(map.containsKey("key3"));
    }

    @Test
    public void containsValue() {
        map.put("hipopotamo", "abacate");
        assertTrue(map.containsValue("abacate"));
    }

    @Test
    public void get() {
        map.put("ervacideira", "relogio");
        assertEquals("relogio", map.get("ervacideira"));
    }

    @Test
    public void put() {
        map.put("ervacideira", "relogio");
        assertEquals("relogio", map.put("ervacideira", "marrom"));
    }

    @Test
    public void remove() {
        map.put("ervacideira", "relogio");
        map.remove("ervacideira");
        assertTrue(map.isEmpty());
    }

    @Test
    public void putAll() {
        HashMap<String, String> tempMap = new HashMap<>();
        tempMap.put("espinafre", "hipotalamo");
        tempMap.put("relogio", "combustao");
        map.putAll(tempMap);
        assertEquals(2, map.size());
    }
}