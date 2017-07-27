package parag.LRUCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import parag.LRUCache.exception.EntryNotFoundException;
import parag.LRUCache.model.FileCache;

/**
 * Tread Safe LRU Cache Implementation
 */
public class LRUCache implements Operations<String, FileCache> {

    private final ReentrantLock lock = new ReentrantLock();

    private final Integer maxSize;
    private final ConcurrentHashMap<String, FileCache> map;
    private final ConcurrentLinkedQueue<String> queue;

    /**
     * Constructor
     * 
     * @param maxSize
     * @param map
     * @param queue
     */
    public LRUCache(Integer maxSize, ConcurrentHashMap<String, FileCache> map, ConcurrentLinkedQueue<String> queue) {
        this.maxSize = maxSize;
        this.map = map;
        this.queue = queue;
    }

    /* (non-Javadoc)
     * @see parag.LRUCache.Operations#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(final String key, final FileCache cachedFile) {

        // While loop to truncate the size the map i.e delete LRU entries if size of map goes beyond max size
        while (map.size() >= maxSize) {
            removeLRUEntry();
        }

        FileCache oldFile = map.put(key, cachedFile);

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
    public FileCache get(final String key) throws EntryNotFoundException {
        lock.lock();
        try {
            if (!queue.remove(key)) {
                throw new EntryNotFoundException("No entry in cache");
            }
            queue.offer(key);
        } finally {
            lock.unlock();
        }
        return map.get(key);
    }

    /**
     * Remove Least Recently Used entry from Map as well as key from Queue
     */
    private void removeLRUEntry() {
        lock.lock();
        try {
            String leastUsedKey = queue.poll();
            if (null != leastUsedKey) {
                map.remove(leastUsedKey);
            }
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
