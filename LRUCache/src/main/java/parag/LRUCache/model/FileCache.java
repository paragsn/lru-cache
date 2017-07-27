package parag.LRUCache.model;

/**
 * Model Class contains the Content of the Entries which will go in Cache
 */
public class FileCache {

    private String content;

    public FileCache(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "FileCache [content=" + content + "]";
    }

}
