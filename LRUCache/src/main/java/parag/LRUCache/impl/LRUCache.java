package parag.LRUCache.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
public class LRUCache<K, V> implements Cache<K, V> {

    private final ReentrantLock lock = new ReentrantLock();

    private final Integer maxSize;
    private final Map<K, V> map;
    private final ConcurrentLinkedQueue<K> queue;
    private final DiskCache<K, V> diskCache;
    private final LinkedBlockingQueue<K> linkedBlockingQueue;

    /**
     * Constructor
     * 
     * @param maxSize
     * @param map
     * @param queue
     */
    public LRUCache(Integer maxSize, Map<K, V> map, ConcurrentLinkedQueue<K> queue, DiskCache<K, V> diskLRUCache,
            LinkedBlockingQueue<K> linkedBlockingQueue, Thread thread) {
        this.maxSize = maxSize;
        this.map = map;
        this.queue = queue;
        this.diskCache = diskLRUCache;
        this.linkedBlockingQueue = linkedBlockingQueue;
        startLRUManagerThread(thread);
    }

    /**
     * This is async operation to reArrange the keys in queue This is done to improve the performance of cache mainly in get Operation
     */
    public void startLRUManagerThread(Thread thread) {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    /* (non-Javadoc)
     * @see parag.LRUCache.Operations#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(final K key, final V cachedFile) throws StoreException {
        if (key == null) {
            return;
        }
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

            V oldFile = map.put(key, cachedFile);

            /**
             * If Old value for key is not null then Key was already present. We are reinserting the key again to tail. We assume it to be
             * recently used. If old value is null then add new entry
             */
            if (null != oldFile) {
                linkedBlockingQueue.offer(key);
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
    public V get(final K key) throws RetrievalException {
        if (key == null) {
            return null;
        }
        lock.lock();
        V value = null;
        try {
            value = map.get(key);

            if (value != null) {
                linkedBlockingQueue.offer(key);
            } else {
                try {
                    // If key not found in memory then check on disk. Return null if not present
                    V diskValue = diskCache.get(key);
                    if (diskValue != null) {
                        put(key, diskValue);
                        queue.offer(key);
                        diskCache.remove(key);
                    }
                    return diskValue;
                } catch (DeserializationException | StoreException e) {
                    throw new RetrievalException("Error while GET operation", e);
                }
            }
        } finally {
            lock.unlock();
        }
        return value;
    }

    /**
     * Remove Least Recently Used entry from Map as well as key from Queue And Back it up on disk
     * 
     * @throws SerializationException
     */
    private void removeLRUEntry() throws SerializationException {
        K leastUsedKey = queue.poll();
        if (null != leastUsedKey) {
            V value = map.get(leastUsedKey);
            // Adding entry to disk first and then removing from memory
            if (null != value) {
                diskCache.put(leastUsedKey, value);
            }
            map.remove(leastUsedKey);
        }
    }

}
