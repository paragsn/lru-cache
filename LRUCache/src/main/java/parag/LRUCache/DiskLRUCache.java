package parag.LRUCache;

import java.io.IOException;

import parag.LRUCache.SerializationHelper;
import parag.LRUCache.exception.DeserializationException;
import parag.LRUCache.exception.SerializationException;

public class DiskLRUCache {

    private static final String FILE_PREFIX = "cache_";
    private static final String FILE_PATH = "F:\\NewParag\\";

    public void put(String key, String value) throws SerializationException {
        String fileName = findFileName(key);
        try {
            SerializationHelper.serialize(value, FILE_PATH + fileName);
        } catch (IOException e) {
            throw new SerializationException("Error while putting on disk", e);
        }
    }

    public String get(String key) throws DeserializationException {
        return getSerializedObject(key);
    }

    public String getSerializedObject(String key) throws DeserializationException {
        String fileName = findFileName(key);
        String value = null;
        try {
            value = (String) SerializationHelper.deserialize(fileName);
        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationException("Error while getting from disk", e);
        }
        return value;
    }

    private String findFileName(String key) {
        String fileName = FILE_PREFIX + key;
        return fileName;
    }

}
