package parag.LRUCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import parag.LRUCache.model.FileCache;

public class LRUDiskCacheOperations implements Operations<String, FileCache> {

    private final int maxSize;
    private ConcurrentHashMap<String, FileCache> map;
    private ConcurrentLinkedQueue<String> queue;

    public LRUDiskCacheOperations(int maxSize) {
        this.maxSize = maxSize;
        map = new ConcurrentHashMap<>(maxSize);
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void put(final String key, final FileCache cachedFile) {
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
    public FileCache get(final String key) throws Exception {
        if (!queue.remove(key)) {
            throw new Exception();
        }
        FileCache file = map.get(key);
        queue.offer(key);
        return file;
    }

}
