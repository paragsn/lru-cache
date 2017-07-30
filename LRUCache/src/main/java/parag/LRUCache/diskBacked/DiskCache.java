package parag.LRUCache.diskBacked;

import java.io.IOException;

import parag.LRUCache.diskBacked.DiskStore;
import parag.LRUCache.exception.DeserializationException;
import parag.LRUCache.exception.SerializationException;

/**
 * This class is responsible for cache operations on disk
 */
public class DiskCache {

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
    public void put(String key, String value) throws SerializationException {
        String file = findFileName(key);
        try {
            DiskStore.serialize(value, file);
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
    public String get(String key) throws DeserializationException {

        String file = findFileName(key);
        String value = null;
        try {
            value = (String) DiskStore.deserialize(file);
        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationException("Error while getting from disk", e);
        }
        return value;
    }

    /**
     * Forms Full file path
     * 
     * @param key
     * @return
     */
    private String findFileName(String key) {
        String fileName = filePath + FILE_PREFIX + key;
        return fileName;
    }

}
