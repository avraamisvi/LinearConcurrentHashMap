package org.abraao.concurrency;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * A open addressing and linear probing concurrent HashMap.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Abraao Isvi
 */
public class LinearConcurrentHashMap<K, V> implements ConcurrentMap<K, V> {

    private static final int DEFAULT_INITIAL_SIZE = 64;
    private Nodes nodes;

    public LinearConcurrentHashMap() {
        this(LinearConcurrentHashMap.DEFAULT_INITIAL_SIZE);
    }

    public LinearConcurrentHashMap(int initialSize) {
        this.nodes = new Nodes(initialSize);
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public boolean isEmpty() {
        return nodes.size() == 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return nodes.findEntry((K) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return nodes.containsValue(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        Entry<K, V> entry = nodes.findEntry((K) key);
        return entry != null ? entry.getValue() : null;
    }

    @Override
    public V put(K key, V value) {
        return putValue(key, value);
    }

    private V putValue(K key, V value) {
        if (shouldResize()) {
            resize();
        }
        return nodes.put(key, value);
    }

    private boolean shouldResize() {
        return nodes.isFull();
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Nodes resizedNodes = new Nodes(nodes.size() * 2);
        reorganize(resizedNodes);
    }

    private void reorganize(Nodes resizedNodes) {
        for (Node<K, V> node : nodes.entries) {
            resizedNodes.put(node.key, node.value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        return nodes.remove((K) key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::putValue);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!this.containsKey(key)) {
            this.putValue(key, value);
        }
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V replace(K key, V value) {
        throw new UnsupportedOperationException();
    }

    class Nodes {
        private Node<K, V>[] entries;
        private int itemsSize = 0;

        @SuppressWarnings("unchecked")
        Nodes(int size) {
            this.entries = new Node[size];
        }

        int size() {
            return itemsSize;
        }

        int findNextValidIndex(int index, K key) {
            for (int i = index; i < entries.length; i++) {
                Entry<K, V> entry = entries[i];
                if (entry == null || entry.getKey().equals(key)) {
                    return i;
                }
            }
            return -1;
        }

        Entry<K, V> findEntry(K key) {
            int index = findEntryIndex(key);
            return index != -1 ? entries[index] : null;
        }

        int findEntryIndex(K key) {
            int index = hash(key);
            for (int i = index; i < entries.length; i++) {
                Entry<K, V> entry = entries[i];
                if (entry != null && key.equals(entry.getKey())) {
                    return i;
                }
            }
            return -1;
        }

        boolean isFull() {
            return entries.length - itemsSize <= 0;
        }

        boolean containsValue(Object value) {
            for (Entry<K, V> entry : entries) {
                if (entry != null && value.equals(entry.getValue())) {
                    return true;
                }
            }
            return false;
        }

        private V put(K key, V value) {
            int hash = hash(key);
            int index = this.findNextValidIndex(hash, key);
            return this.putValue(hash, index, key, value);
        }

        private V putValue(int hash, int index, K key, V value) {
            if (this.entries[index] == null) {
                this.entries[index] = new Node<>(hash, key, value);
                itemsSize++;
                return null;
            } else {
                V previousValue = this.entries[index].getValue();
                this.entries[index].setValue(value);
                return previousValue;
            }
        }

        V remove(K key) {
            int index = nodes.findEntryIndex(key);
            V previousValue = null;
            if (index != 0) {
                previousValue = entries[index].getValue();
                entries[index] = null;
                itemsSize--;
            }
            return previousValue;
        }

        int hash(K key) {
            return key.hashCode() % entries.length;
        }
    }

    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;

        Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

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
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }
}
