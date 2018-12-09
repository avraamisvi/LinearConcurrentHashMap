package org.abraao.concurrency;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Concurrent HashMap inspired on the implementation of Junction library.
 * For more information visit <a href="https://preshing.com/20160314/leapfrog-probing/">leapfrog probing<a/>
 *
 * @author Abraao Isvi
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class LeapFrogHashMap<K, V> implements ConcurrentMap<K, V> {

    private final int DEFAULT_INITIAL_SIZE = 64;

    private Entry<K,V>[] entries;
    private int addedItems = 0;
    private int initialSize = DEFAULT_INITIAL_SIZE;

    public LeapFrogHashMap() {
        this.initialSize = DEFAULT_INITIAL_SIZE;
    }

    public LeapFrogHashMap(int initialSize) {
        this.initialSize = initialSize;
    }

    @Override
    public int size() {
        return addedItems;
    }

    @Override
    public boolean isEmpty() {
        return addedItems == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public V replace(K key, V value) {
        return null;
    }
}
