package org.abraao.concurrency;

import java.util.*;
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

    private Node<K,V>[] entries;
    private int addedItems = 0;
    private int initialSize;
    private int threshold;

    public LeapFrogHashMap() {
        this.init(DEFAULT_INITIAL_SIZE);
    }

    public LeapFrogHashMap(int initialSize) {
        this.init(initialSize);
    }

    private void init(int initialSize) {
        this.initialSize = initialSize;
        this.threshold = initialSize/4;
        this.createBuckets();
    }

    @SuppressWarnings("unchecked")
    private void createBuckets() {
        this.entries = new Node[this.initialSize];
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
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return findEntry((K) key) != null;
    }

    private Entry<K, V> findEntry(K key) {
        return findEntryByLinearProbing(key);
    }

    private Entry<K, V> findEntryByLinearProbing(K key) {
        int index = findEntryIndexByLinearProbing(key);
        return index != -1? entries[index]:null;
    }

    private int findEntryIndex(K key) {
        return findEntryIndexByLinearProbing(key);
    }

    private int findEntryIndexByLinearProbing(K key) {
        int index = hash(key);
        for(int i = index; i < entries.length; i++) {
            Entry<K,V> entry = entries[i];
            if(entry != null && key.equals(entry.getKey())) {
                return i;
            }
        }
        return -1;
    }

    private int hash(K key) {
        return key.hashCode() % entries.length;
    }

    @Override
    public boolean containsValue(Object value) {
        for(Entry<K,V> entry : entries) {
            if(entry != null && value.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        Entry<K, V> entry = findEntry((K) key);
        return entry != null? entry.getValue() : null;
    }

    @Override
    public V put(K key, V value) {
        return putValue(key,value);
    }

    private V putValue(K key, V value) {
        if(shouldResize()) {
            resize();
        }
        int hash = hash(key);
        int index = findNextValidIndex(hash, key);
        this.entries[index] = new Node<>(hash, key, value);
        return putValue(hash, index, key, value);
    }

    private V putValue(int hash, int index, K key, V value) {
        if(this.entries[index] == null) {
            this.entries[index] = new Node<>(hash, key, value);
            addedItems++;
            return null;
        } else {
            V previousValue = this.entries[index].getValue();
            this.entries[index].setValue(value);
            return previousValue;
        }
    }

    private boolean shouldResize() {
        return addedItems >= threshold;
    }

    private void resize() {
        reorganize();
    }

    private void reorganize() {

    }

    private int findNextValidIndex(int index, K key) {
        return findNextValidByLinearProbing(index, key);
    }

    private int findNextValidByLinearProbing(int index, K key) {
        for(int i = index; i < entries.length; i++) {
            Entry<K,V> entry = entries[i];
            if(entry == null || entry.getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int index = findEntryIndex((K) key);
        V previousValue = null;
        if(index != 0) {
            previousValue = entries[index].getValue();
            entries[index] = null;
            addedItems--;
        }
        return previousValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::putValue);
    }

    @Override
    public void clear() {
        this.createBuckets();
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
        int index = findEntryIndex(key);
        if(index != -1) {
            V value = this.entries[index].getValue();
            if(Objects.equals(oldValue, value)) {
                this.entries[index].setValue(newValue);
                return true;
            }
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        return null;
    }

    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;

        Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }
}
