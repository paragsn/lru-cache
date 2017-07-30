package parag.LRUCache.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import parag.LRUCache.Cache;
import parag.LRUCache.diskBacked.DiskCache;
import parag.LRUCache.exception.DeserializationException;
import parag.LRUCache.exception.RetrievalException;
import parag.LRUCache.exception.SerializationException;
import parag.LRUCache.exception.StoreException;

/**
 * Tread Safe Disk Backed LRU Cache Implementation
 */
public class LRUCache implements Cache<String, String> {

    private final ReentrantLock lock = new ReentrantLock();

    private final Integer maxSize;
    private final Map<String, String> map;
    private final ConcurrentLinkedQueue<String> queue;
    private final DiskCache diskCache;

    /**
     * Constructor
     * 
     * @param maxSize
     * @param map
     * @param queue
     */
    public LRUCache(Integer maxSize, Map<String, String> map, ConcurrentLinkedQueue<String> queue, DiskCache diskLRUCache) {
        this.maxSize = maxSize;
        this.map = map;
        this.queue = queue;
        this.diskCache = diskLRUCache;
    }

    /* (non-Javadoc)
     * @see parag.LRUCache.Operations#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(final String key, final String cachedFile) throws StoreException {
        lock.tryLock();
        try {
            // While loop to truncate the size the map i.e delete LRU entries if size of map goes beyond max size
            while (map.size() >= maxSize) {
                try {
                    removeLRUEntry();
                } catch (SerializationException e) {
                    throw new StoreException("Error while PUT operation", e);
                }
            }

            String oldFile = map.put(key, cachedFile);

            /**
             * If Old value for key is not null then Key was already present. We are reinserting the key again to tail. We assume it to be
             * recently used. If old value is null then add new entry
             */
            if (null != oldFile) {
                reInsertKey(key);
            } else {
                queue.offer(key);
            }
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see parag.LRUCache.Operations#get(java.lang.Object)
     */
    @Override
    public String get(final String key) throws RetrievalException {
        lock.lock();
        try {
            if (!queue.remove(key)) {
                try {
                    // If key not found in memory then check on disk. Return null if not present
                    String diskValue = diskCache.get(key);
                    if (diskValue != null) {
                        diskCache.put(key, diskValue);
                        queue.offer(key);
                    }
                    return diskValue;
                } catch (DeserializationException | SerializationException e) {
                    throw new RetrievalException("Error while GET operation", e);
                }
            }
            queue.offer(key);
        } finally {
            lock.unlock();
        }
        return map.get(key);
    }

    /**
     * Remove Least Recently Used entry from Map as well as key from Queue And Back it up on disk
     * 
     * @throws SerializationException
     */
    private void removeLRUEntry() throws SerializationException {
        String leastUsedKey = queue.poll();
        if (null != leastUsedKey) {
            String value = map.get(leastUsedKey);
            // Adding entry to disk first and then removing from memory
            if (null != value) {
                diskCache.put(leastUsedKey, value);
            }
            map.remove(leastUsedKey);
        }
    }

    /**
     * Removing key and inserting again to tail Method to be used for key which is recently used
     * 
     * @param key
     */
    private void reInsertKey(String key) {
        // Remove
        queue.remove(key);
        // Insert to tail
        queue.offer(key);
    }
}
