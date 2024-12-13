package models;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Document {
    private final Map<String, Object> data;
    private final String id;

    public Document(String id) {
        this.id = id;
        this.data = new ConcurrentHashMap<>();
    }

    /**
     * set a value for a specific key in the document
     * @param key The key to set
     * @param value The value to associate with the key
     */

    public void set(String key, Object value) {
        data.put(key, value);
    }

    /**
     * get a value for a specific key
     * @param key The key to retrieve
     * @return The value associated with the key, or null if not found
     */
    public Object get(String key) {
        return data.get(key);
    }

    /**
     * get a copy of all data in the document
     * @return a map of all key-value pairs in the document
     */
    public Map<String, Object> getData() {
        return new ConcurrentHashMap<>(data);
    }

    /**
     * get the document's unique identifier
     * @return The document ID
     */
    public String getId() {
        return id;
    }

    /**
     * convert document to a string representation
     * @return string representation of the document
     */
    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", data=" + data +
                '}';
    }
}
