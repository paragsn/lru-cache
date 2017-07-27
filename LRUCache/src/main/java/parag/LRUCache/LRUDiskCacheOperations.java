package parag.LRUCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import parag.LRUCache.exception.EntryNotFoundException;
import parag.LRUCache.model.FileCache;

public class LRUDiskCacheOperations implements Operations<String, FileCache> {

    private final int maxSize;
    private final ConcurrentHashMap<String, FileCache> map;
    private final ConcurrentLinkedQueue<String> queue;

    public LRUDiskCacheOperations(int maxSize, ConcurrentHashMap<String, FileCache> map, ConcurrentLinkedQueue<String> queue) {
        this.maxSize = maxSize;
        this.map = map;
        this.queue = queue;
    }

    @Override
    public synchronized void put(final String key, final FileCache cachedFile) {
        // Checking if the key is already present if yes, then remove from queue
        // We consider this key as least recently used and We will add it again to the tail of queue
        if (map.containsKey(key)) {
            queue.remove(key); // remove the key from the FIFO queue
        }

        // Removing Elements from map until size becomes less than maxSize
        while (map.size() >= maxSize) {
            String leastUsedKey = queue.poll();
            if (null != leastUsedKey) {
                map.remove(leastUsedKey);
            }
        }

        // Adding key in queue and key and FileObject in map
        queue.offer(key);
        map.put(key, cachedFile);
    }

    @Override
    public synchronized FileCache get(final String key) throws EntryNotFoundException {
        if (!queue.remove(key)) {
            throw new EntryNotFoundException("No entry in cache.. Use put method to make an entry in cache");
        }
        FileCache file = map.get(key);
        queue.offer(key);
        return file;
    }

}
