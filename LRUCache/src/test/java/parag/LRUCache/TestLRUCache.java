package parag.LRUCache;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.testng.annotations.Test;

import parag.LRUCache.diskBacked.DiskCache;
import parag.LRUCache.exception.RetrievalException;
import parag.LRUCache.exception.StoreException;
import parag.LRUCache.impl.LRUCache;
import parag.LRUCache.lru.LRUManager;

/**
 * Test Class for {@link LRUCache}
 */
public class TestLRUCache {

    /**
     *  I/P: Cache --> Empty 
     *  O/P: GET() --> null
     * @throws RetrievalException 
     * @throws StoreException 
     * @throws IOException 
     * @throws InterruptedException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testGETWhenQueueEmp() throws RetrievalException, StoreException, IOException, InterruptedException {

        int maxSize = 20;
        Map<String, Integer> map = new HashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();
        DiskCache<String, Integer> diskCache = new DiskCache<>("C:\\Users\\parag123\\Desktop\\Go\\");
        Thread thread = new Thread(new LRUManager<>(queue, linkedBlockingQueue));
        LRUCache<String, Integer> cacheOperations = new LRUCache<>(maxSize, map, queue, diskCache, linkedBlockingQueue, thread);
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
        DiskCache<String, String> diskCache = new DiskCache<>("");
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();
        Thread thread = new Thread(new LRUManager<>(queue, linkedBlockingQueue));
        LRUCache<String, String> cacheOperations = new LRUCache<>(maxSize, map, queue, diskCache, linkedBlockingQueue, thread);
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
     * @throws InterruptedException 
     * @throws StoreException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testGETWhenQueueNotEmp() throws RetrievalException, InterruptedException, StoreException {
        int maxSize = 20;
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        DiskCache<String, String> diskCache = new DiskCache<>("");
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();
        Thread thread = new Thread(new LRUManager<>(queue, linkedBlockingQueue));
        LRUCache<String, String> cacheOperations = new LRUCache<>(maxSize, map, queue, diskCache, linkedBlockingQueue, thread);
        cacheOperations.put("key1", "");
        cacheOperations.put("key2", "");
        cacheOperations.put("key3", "");
        
        cacheOperations.get("key1");
        Thread.sleep(100);
        assertEquals(queue.poll(), "key2");
        assertEquals(queue.poll(), "key3");
        assertEquals(queue.poll(), "key1");
    }

    /**
     * I/P: Multiple Put() and Get() Operations
     * O/P: Valid entries in Map and Queue
     * @throws StoreException 
     * @throws RetrievalException 
     * @throws InterruptedException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testOperationsForMultipleInputs() throws StoreException, RetrievalException, InterruptedException {

        int maxSize = 20;
        String fileContent1 = "fileContent1";
        String fileContent2 = "fileContent2";
        String fileContent3 = "fileContent3";
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        DiskCache<String, String> diskCache = new DiskCache<>("");
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();
        Thread thread = new Thread(new LRUManager<>(queue, linkedBlockingQueue));
        LRUCache<String, String> cacheOperations = new LRUCache<>(maxSize, map, queue, diskCache, linkedBlockingQueue, thread);
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
        Thread.sleep(10);
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
     * @throws InterruptedException 
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testIfSizeLimitReached() throws StoreException, RetrievalException, InterruptedException {

        int maxSize = 3;
        String fileContent1 = "fileContent1";
        String fileContent2 = "fileContent2";
        String fileContent3 = "fileContent3";
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        
        DiskCache<String, String> diskCache = new DiskCache<>("");
        
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();
        Thread thread = new Thread(new LRUManager<>(queue, linkedBlockingQueue));
        LRUCache<String, String> cacheOperations = new LRUCache<>(maxSize, map, queue, diskCache, linkedBlockingQueue, thread);
        
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

        Thread.sleep(10);
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
        
        Thread.sleep(1);
        // Queue
        assertEquals(queue.poll(), "key7");
        assertEquals(queue.poll(), "key6");
        assertEquals(queue.poll(), "key5");

    }

}
