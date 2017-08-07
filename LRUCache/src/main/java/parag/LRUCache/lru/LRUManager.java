package parag.LRUCache.lru;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LRUManager<K> implements Runnable {

    private final ConcurrentLinkedQueue<K> queue;
    private final LinkedBlockingQueue<K> linkedBlockingQueue;

    public LRUManager(ConcurrentLinkedQueue<K> queue, LinkedBlockingQueue<K> linkedBlockingQueue) {
        this.queue = queue;
        this.linkedBlockingQueue = linkedBlockingQueue;
    }
    
    @Override
    public void run() {
        while (true) {
            K key = null;
            try {
                key = linkedBlockingQueue.take();
            } catch (InterruptedException e) {
            }
            if (key != null) {
                reInsertKey(key);
            }
        }
    }

    /**
     * Removing key and inserting again to tail Method to be used for key which is recently used
     * 
     * @param key
     */
    private void reInsertKey(K key) {
        // Remove
        queue.remove(key);
        // Insert to tail
        queue.offer(key);
    }

}
