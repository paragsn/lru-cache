package parag.LRUCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import parag.LRUCache.exception.DeserializationException;
import parag.LRUCache.exception.DiskBackException;
import parag.LRUCache.exception.SerializationException;

/**
 * Tread Safe Disk Backed LRU Cache Implementation
 */
public class LRUCache implements Cache<String, String> {

    private final ReentrantLock lock = new ReentrantLock();

    private final Integer maxSize;
    private final ConcurrentHashMap<String, String> map;
    private final ConcurrentLinkedQueue<String> queue;
    private DiskLRUCache diskLRUCache;

    /**
     * Constructor
     * 
     * @param maxSize
     * @param map
     * @param queue
     */
    public LRUCache(Integer maxSize, ConcurrentHashMap<String, String> map, ConcurrentLinkedQueue<String> queue) {
        this.maxSize = maxSize;
        this.map = map;
        this.queue = queue;
        this.diskLRUCache = new DiskLRUCache();
    }

    /* (non-Javadoc)
     * @see parag.LRUCache.Operations#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(final String key, final String cachedFile) throws DiskBackException {

        // While loop to truncate the size the map i.e delete LRU entries if size of map goes beyond max size
        while (map.size() >= maxSize) {
            removeLRUEntry();
        }

        String oldFile = map.put(key, cachedFile);

        /**
         * If Old value for key is not null then Key was already present. We are reinserting the key again to tail. We assume it to be recently
         * used. If old value is null then add new entry
         */
        if (null != oldFile) {
            reInsertKey(key);
        } else {
            queue.offer(key);
        }
    }

    /* (non-Javadoc)
     * @see parag.LRUCache.Operations#get(java.lang.Object)
     */
    @Override
    public String get(final String key) throws DiskBackException {
        lock.lock();
        try {
            if (!queue.remove(key)) {
                try {
                    return diskLRUCache.get(key);
                } catch (DeserializationException e) {
                    throw new DiskBackException("Error while getting data from disk", e);
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
     */
    private void removeLRUEntry() throws DiskBackException {
        lock.lock();
        try {
            String leastUsedKey = queue.poll();
            if (null != leastUsedKey) {
                String value = map.get(leastUsedKey);
                if (null != value) {
                    diskLRUCache.put(leastUsedKey, value);
                }
                map.remove(leastUsedKey);
            }
        } catch (SerializationException e) {
            throw new DiskBackException("Error while saving data on disk", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removing key and inserting again to tail Method to be used for key which is recently used
     * 
     * @param key
     */
    private void reInsertKey(String key) {
        lock.lock();
        try {
            // Remove
            queue.remove(key);
            // Insert to tail
            queue.offer(key);
        } finally {
            lock.unlock();
        }
    }
}
