package parag.LRUCache.diskBacked;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class directly interacts with Disk storage
 */
public class DiskStore {

    /**
     * Store Object in file
     * 
     * @param object
     * @param filePath
     * @throws IOException
     */
    public static <K, V> void serialize(V object, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(object);
        }
    }

    /**
     * Retrieve Object from file
     * 
     * @param filePath
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static <K, V> V deserialize(String filePath) throws IOException, ClassNotFoundException {
        V object = null;
        try (InputStream fis = new FileInputStream(filePath); ObjectInputStream ois = new ObjectInputStream(fis)) {
            object =  (V) ois.readObject();
        } catch (FileNotFoundException e) {
            return object;
        }

        return object;
    }

    /**
     * Delete file
     * 
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }

}
