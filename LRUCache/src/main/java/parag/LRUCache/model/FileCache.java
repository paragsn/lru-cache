package parag.LRUCache.model;

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
