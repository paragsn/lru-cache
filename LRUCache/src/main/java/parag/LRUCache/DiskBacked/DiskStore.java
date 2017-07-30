package parag.LRUCache.DiskBacked;

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
    public static void serialize(Object object, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);

        fos.close();
    }

    /**
     * Retrieve Object from file
     * 
     * @param filePath
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(String filePath) throws IOException, ClassNotFoundException {
        InputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            return null;
        }

        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        ois.close();
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
