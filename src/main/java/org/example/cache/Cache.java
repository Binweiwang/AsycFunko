package org.example.cache;

public interface Cache<K,V> {
    // Metodos propios de la entidad
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    void clear();
    void shutdown();
}
