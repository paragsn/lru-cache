package parag.LRUCache;

import parag.LRUCache.exception.DiskBackException;
import parag.LRUCache.exception.EntryNotFoundException;

public interface Cache<K, V> {

    /**
     * Returns the value stored in the cache
     * 
     * @param key
     * @return
     * @throws EntryNotFoundException 
     * @throws DiskBackException 
     * @throws Exception
     */
    V get(K key) throws DiskBackException;

    /**
     * Stores the value into the cache, replacing an existing mapping if present.
     * 
     * @param key
     * @param value
     * @throws DiskBackException
     */
    void put(K key, V value) throws DiskBackException;

}