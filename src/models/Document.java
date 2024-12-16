package models;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Document {
    private final Map<String, Object> data;
    private final String id;
    private final Map<String, Map<String, Document>> nestedCollections;

    public Document(String id) {
        this.id = id;
        this.data = new ConcurrentHashMap<>();
        this.nestedCollections = new ConcurrentHashMap<>();
    }

    /**
     * Set a value for a specific key in the document
     * @param key The key to set
     * @param value The value to associate with the key
     */
    public void set(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Get a value for a specific key
     * @param key The key to retrieve
     * @return The value associated with the key, or null if not found
     */
    public Object get(String key) {
        return data.get(key);
    }

    /**
     * Create a nested collection within this document
     * @param collectionName Name of the nested collection
     */
    public void createNestedCollection(String collectionName) {
        nestedCollections.putIfAbsent(collectionName, new ConcurrentHashMap<>());
    }

    /**
     * Add a document to a nested collection
     * @param collectionName Name of the nested collection
     * @param documentId ID of the document
     * @return The created nested document
     */
    public Document addNestedDocument(String collectionName, String documentId) {
        createNestedCollection(collectionName);
        Document nestedDocument = new Document(documentId);
        nestedCollections.get(collectionName).put(documentId, nestedDocument);
        return nestedDocument;
    }

    /**
     * Get a nested document from a collection
     * @param collectionName Name of the nested collection
     * @param documentId ID of the document
     * @return The nested document, null if not found
     */
    public Document getNestedDocument(String collectionName, String documentId) {
        Map<String, Document> collection = nestedCollections.get(collectionName);
        return collection != null ? collection.get(documentId) : null;
    }

    /**
     * Get all nested documents in a collection
     * @param collectionName Name of the nested collection
     * @return Map of nested documents in the collection
     */
    public Map<String, Document> getNestedDocuments(String collectionName) {
        return nestedCollections.getOrDefault(collectionName, new ConcurrentHashMap<>());
    }

    /**
     * Get a copy of all data in the document
     * @return a map of all key-value pairs in the document
     */
    public Map<String, Object> getData() {
        return new ConcurrentHashMap<>(data);
    }

    /**
     * Get the document's unique identifier
     * @return The document ID
     */
    public String getId() {
        return id;
    }

    /**
     * Convert document to a string representation
     * @return string representation of the document
     */
    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", data=" + data +
                ", nestedCollections=" + nestedCollections.keySet() +
                '}';
    }
}