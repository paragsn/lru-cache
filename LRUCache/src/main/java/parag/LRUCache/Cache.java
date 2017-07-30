package parag.LRUCache;

import parag.LRUCache.exception.RetrievalException;
import parag.LRUCache.exception.StoreException;

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
    V get(K key) throws RetrievalException;

    /**
     * Stores the value into the cache, replacing an existing mapping if present.
     * 
     * @param key
     * @param value
     * @throws DiskBackException
     */
    void put(K key, V value) throws StoreException;

}