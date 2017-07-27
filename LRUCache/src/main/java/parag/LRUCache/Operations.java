package parag.LRUCache;

public interface Operations<K, V> {

    /**
     * Returns the value stored in the cache
     * 
     * @param key
     * @return
     * @throws Exception
     */
    V get(K key) throws Exception;

    /**
     * Stores the value into the cache, replacing an existing mapping if present.
     * 
     * @param key
     * @param value
     */
    void put(K key, V value);

}