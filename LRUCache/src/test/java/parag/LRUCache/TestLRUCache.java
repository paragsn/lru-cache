package parag.LRUCache;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.testng.annotations.Test;

import parag.LRUCache.exception.EntryNotFoundException;
import parag.LRUCache.model.FileCache;

/**
 * Test Class for {@link LRUCache}
 */
public class TestLRUCache {

    /**
     * I/P: Cache --> Empty 
     * O/P: GET() throws  {@link EntryNotFoundException}
     */
    @Test(expectedExceptions = EntryNotFoundException.class, threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testGETWhenQueueEmp() throws EntryNotFoundException {

        int maxSize = 20;
        ConcurrentHashMap<String, FileCache> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        LRUCache cacheOperations = new LRUCache(maxSize, map, queue);
        cacheOperations.get("");
    }

    /**
     * I/P: Cache --> Empty
     * O/P: Valid Insertions via PUT()
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testPUTWhenQueueEmp() {

        int maxSize = 20;
        ConcurrentHashMap<String, FileCache> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        LRUCache cacheOperations = new LRUCache(maxSize, map, queue);
        cacheOperations.put("key1", new FileCache("content1"));
        cacheOperations.put("key2", new FileCache("content2"));

        // Check Map
        assertEquals(map.get("key1").getContent(), "content1");
        assertEquals(map.get("key2").getContent(), "content2");

        // Check Queue
        assertEquals(queue.poll(), "key1");
        assertEquals(queue.poll(), "key2");
    }

    /**
     * I/P: Cache not Empty
     * O/P: Valid Keys present in Queue
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testGETWhenQueueNotEmp() throws EntryNotFoundException {
        int maxSize = 20;
        ConcurrentHashMap<String, FileCache> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.offer("key1");
        queue.offer("key2");
        queue.offer("key3");
        LRUCache cacheOperations = new LRUCache(maxSize, map, queue);
        cacheOperations.get("key1");

        assertEquals(queue.poll(), "key2");
        assertEquals(queue.poll(), "key3");
        assertEquals(queue.poll(), "key1");
    }

    /**
     * I/P: Multiple Put() and Get() Operations
     * O/P: Valid entries in Map and Queue
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testOperationsForMultipleInputs() throws EntryNotFoundException {

        int maxSize = 20;
        String fileContent1 = "fileContent1";
        String fileContent2 = "fileContent2";
        String fileContent3 = "fileContent3";
        ConcurrentHashMap<String, FileCache> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        LRUCache cacheOperations = new LRUCache(maxSize, map, queue);

        FileCache fileCache1 = new FileCache(fileContent1);
        FileCache fileCache2 = new FileCache(fileContent2);
        FileCache fileCache3 = new FileCache(fileContent3);

        // PUT
        cacheOperations.put("key1", fileCache1);
        cacheOperations.put("key2", fileCache2);
        cacheOperations.put("key3", fileCache3);

        // Check Queue
        assertEquals(queue.size(), 3);
        assertEquals(map.get("key1").getContent(), fileContent1);
        assertEquals(map.get("key2").getContent(), fileContent2);
        assertEquals(map.get("key3").getContent(), fileContent3);

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
     */
    @Test(threadPoolSize = 3, invocationCount = 6, timeOut = 1000)
    public void testIfSizeLimitReached() throws EntryNotFoundException {

        int maxSize = 3;
        String fileContent1 = "fileContent1";
        String fileContent2 = "fileContent2";
        String fileContent3 = "fileContent3";
        ConcurrentHashMap<String, FileCache> map = new ConcurrentHashMap<>(maxSize);
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        LRUCache cacheOperations = new LRUCache(maxSize, map, queue);

        FileCache fileCache1 = new FileCache(fileContent1);
        FileCache fileCache2 = new FileCache(fileContent2);
        FileCache fileCache3 = new FileCache(fileContent3);

        // PUT
        cacheOperations.put("key1", fileCache1);
        cacheOperations.put("key2", fileCache2);
        cacheOperations.put("key3", fileCache3);

        // Check Queue
        assertEquals(queue.size(), 3);
        assertEquals(map.get("key1").getContent(), fileContent1);
        assertEquals(map.get("key2").getContent(), fileContent2);
        assertEquals(map.get("key3").getContent(), fileContent3);

        // GET
        cacheOperations.get("key2");

        // Now Checking the Removal of least recently used entry
        FileCache fileCache4 = new FileCache("fileContents4");
        cacheOperations.put("Key4", fileCache4);

        // We did GET on key2 and inserted key4 so key3 was least recently used key
        assertEquals(queue.peek(), "key3");

        // Lets try out multiple insertion

        FileCache fileCache5 = new FileCache("fileContents5");
        FileCache fileCache6 = new FileCache("fileContents6");
        FileCache fileCache7 = new FileCache("fileContents7");

        cacheOperations.put("key5", fileCache5);
        cacheOperations.put("key6", fileCache6);
        cacheOperations.put("key7", fileCache7);

        cacheOperations.get("key7");
        cacheOperations.get("key6");
        cacheOperations.get("key5");

        // Lets check both map and queue

        // Map
        assertEquals(map.get("key5").getContent(), "fileContents5");
        assertEquals(map.get("key6").getContent(), "fileContents6");
        assertEquals(map.get("key7").getContent(), "fileContents7");

        // Queue
        assertEquals(queue.poll(), "key7");
        assertEquals(queue.poll(), "key6");
        assertEquals(queue.poll(), "key5");

    }

}
