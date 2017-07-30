package parag.LRUCache;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.testng.annotations.Test;

import parag.LRUCache.DiskBacked.DiskCache;
import parag.LRUCache.exception.RetrievalException;
import parag.LRUCache.exception.StoreException;
import parag.LRUCache.impl.LRUCache;

/**
 * Test Class for {@link LRUCache}
 */
public class TestLRUCache {

    /**
     *  I/P: Cache --> Empty 
     *  O/P: GET() --> null
     * @throws RetrievalException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testGETWhenQueueEmp() throws RetrievalException {

        int maxSize = 20;
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        DiskCache diskCache = new DiskCache("");
        LRUCache cacheOperations = new LRUCache(maxSize, map, queue, diskCache);
        assertEquals(null, cacheOperations.get(""));
    }

    /**
     * I/P: Cache --> Empty
     * O/P: Valid Insertions via PUT()
     * @throws StoreException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testPUTWhenQueueEmp() throws StoreException {

        int maxSize = 20;
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        DiskCache diskCache = new DiskCache("");
        
        LRUCache cacheOperations = new LRUCache(maxSize, map, queue, diskCache);
        cacheOperations.put("key1", "content1");
        cacheOperations.put("key2", "content2");

        // Check Map
        assertEquals(map.get("key1"), "content1");
        assertEquals(map.get("key2"), "content2");

        // Check Queue
        assertEquals(queue.poll(), "key1");
        assertEquals(queue.poll(), "key2");
    }

    /**
     * I/P: Cache not Empty
     * O/P: Valid Keys present in Queue
     * @throws RetrievalException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testGETWhenQueueNotEmp() throws RetrievalException {
        int maxSize = 20;
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.offer("key1");
        queue.offer("key2");
        queue.offer("key3");
        DiskCache diskCache = new DiskCache("");
        LRUCache cacheOperations = new LRUCache(maxSize, map, queue, diskCache);
        cacheOperations.get("key1");

        assertEquals(queue.poll(), "key2");
        assertEquals(queue.poll(), "key3");
        assertEquals(queue.poll(), "key1");
    }

    /**
     * I/P: Multiple Put() and Get() Operations
     * O/P: Valid entries in Map and Queue
     * @throws StoreException 
     * @throws RetrievalException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testOperationsForMultipleInputs() throws StoreException, RetrievalException {

        int maxSize = 20;
        String fileContent1 = "fileContent1";
        String fileContent2 = "fileContent2";
        String fileContent3 = "fileContent3";
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        DiskCache diskCache = new DiskCache("");
        LRUCache cacheOperations = new LRUCache(maxSize, map, queue, diskCache);

        // PUT
        cacheOperations.put("key1", fileContent1);
        cacheOperations.put("key2", fileContent2);
        cacheOperations.put("key3", fileContent3);

        // Check Queue
        assertEquals(queue.size(), 3);
        assertEquals(map.get("key1"), fileContent1);
        assertEquals(map.get("key2"), fileContent2);
        assertEquals(map.get("key3"), fileContent3);

        // GET
        cacheOperations.get("key2");

        // Check Queue
        assertEquals(queue.poll(), "key1");
        assertEquals(queue.poll(), "key3");
        assertEquals(queue.poll(), "key2");
    }

    /**
     * I/P: Maximum size of Map Reached
     * O/P: Valid Removal of LRU entries
     * @throws StoreException 
     * @throws RetrievalException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testIfSizeLimitReached() throws StoreException, RetrievalException {

        int maxSize = 3;
        String fileContent1 = "fileContent1";
        String fileContent2 = "fileContent2";
        String fileContent3 = "fileContent3";
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        
        DiskCache diskCache = new DiskCache("");
        
        LRUCache cacheOperations = new LRUCache(maxSize, map, queue, diskCache);

        // PUT
        cacheOperations.put("key1", fileContent1);
        cacheOperations.put("key2", fileContent2);
        cacheOperations.put("key3", fileContent3);

        // Check Queue
        assertEquals(queue.size(), 3);
        assertEquals(map.get("key1"), fileContent1);
        assertEquals(map.get("key2"), fileContent2);
        assertEquals(map.get("key3"), fileContent3);

        // GET
        cacheOperations.get("key2");

        // Now Checking the Removal of least recently used entry
        String fileCache4 = "fileContents4";
        cacheOperations.put("Key4", fileCache4);

        // We did GET on key2 and inserted key4 so key3 was least recently used key
        assertEquals(queue.peek(), "key3");

        // Lets try out multiple insertion

        String fileCache5 = "fileContents5";
        String fileCache6 = "fileContents6";
        String fileCache7 = "fileContents7";

        cacheOperations.put("key5", fileCache5);
        cacheOperations.put("key6", fileCache6);
        cacheOperations.put("key7", fileCache7);

        cacheOperations.get("key7");
        cacheOperations.get("key6");
        cacheOperations.get("key5");

        // Lets check both map and queue

        // Map
        assertEquals(map.get("key5"), "fileContents5");
        assertEquals(map.get("key6"), "fileContents6");
        assertEquals(map.get("key7"), "fileContents7");

        // Queue
        assertEquals(queue.poll(), "key7");
        assertEquals(queue.poll(), "key6");
        assertEquals(queue.poll(), "key5");

    }

}
