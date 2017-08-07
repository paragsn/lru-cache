package parag.LRUCache.diskBacked;

import java.io.IOException;

import parag.LRUCache.diskBacked.DiskStore;
import parag.LRUCache.exception.DeserializationException;
import parag.LRUCache.exception.SerializationException;

/**
 * This class is responsible for cache operations on disk
 */
public class DiskCache<K, V> {

    private static final String FILE_PREFIX = "cache_";
    private final String filePath;

    public DiskCache(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Serialize
     * 
     * @param key
     * @param value
     * @throws SerializationException
     */
    public void put(K key, V value) throws SerializationException {
        String file = findFileName(key);
        try {
            DiskStore.<K, V>serialize(value, file);
        } catch (IOException e) {
            throw new SerializationException("Error while putting on disk", e);
        }
    }

    /**
     * Deserialize
     * 
     * @param key
     * @return
     * @throws DeserializationException
     */
    public V get(K key) throws DeserializationException {

        String file = findFileName(key);
        V value = null;
        try {
            value = DiskStore.<K, V>deserialize(file);
        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationException("Error while getting from disk", e);
        }
        return value;
    }
    
    /**
     * Remove file
     * 
     * @param key
     */
    public void remove(K key) {
        DiskStore.deleteFile(findFileName(key));
    }

    /**
     * Forms Full file path
     * 
     * @param key
     * @return
     */
    private String findFileName(K key) {
        String fileName = filePath + FILE_PREFIX + key.toString();
        return fileName;
    }

}
