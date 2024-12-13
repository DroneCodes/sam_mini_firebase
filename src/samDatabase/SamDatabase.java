package samDatabase;

import models.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SamDatabase {
    // Main storage structure: collection name -> Documents
    private final Map<String, Map<String, Document>> collections;
    public SamDatabase() {
        this.collections = new ConcurrentHashMap<>();
    }

    /**
     * Create a new collection if it doesn't exist
     * @param collectionName Name of the collection
     */
    public void createCollection(String collectionName) {
        collections.putIfAbsent(collectionName, new ConcurrentHashMap<>());
    }

    /**
     * Add a document to a collection
     * @param collectionName Name of the application
     * @param documentId ID of the document
     * @return The created document
     */
    public Document addDocument(String collectionName, String documentId) {
        // Ensure collection exists
        createCollection(collectionName);
        Document document = new Document(documentId);
        collections.get(collectionName).put(documentId, document);
        return document;
    }

    /**
     * Get a document from a collection
     * @param collectionName Name of the collection
     * @param documentId ID of the document
     * @return The document, null if not found
     */
    public Document getDocument(String collectionName, String documentId) {
        Map<String, Document> collection = collections.get(collectionName);
        return collection != null ? collection.get(documentId) : null;
    }

    /**
     * Get all documents in a collection
     * @param collectionName Name of the collection
     * @return List of documents in the collection
     */
    public List<Document> getDocuments(String collectionName) {
        Map<String, Document> collection = collections.get(collectionName);
        return collection != null ? new ArrayList<>(collection.values()) : Collections.emptyList();
    }

    /**
     * Delete a document from a collection
     * @param collectionName Name of the collection
     * @param documentId ID of the document you want to delete
     * @return True if document was deleted, false if not found
     */
    public boolean deleteDocument(String collectionName, String documentId) {
        Map<String, Document> collection = collections.get(collectionName);
        if (collection != null) {
            return collection.remove(documentId) != null;
        }
        return false;
    }

    /**
     * Find documents matching a specific condition
     * @param collectionName Name of the collection
     * @param key Field to search
     * @param value Value to match
     * @return List of matching documents
     */
    public List<Document> findDocuments(String collectionName, String key, Object value) {
        Map<String, Document> collection = collections.get(collectionName);
        if (collection == null) {
            return Collections.emptyList();
        }

        return collection.values().stream()
                .filter(doc -> {
                    Object docValue = doc.get(key);
                    return docValue != null && docValue.equals(value);
                })
                .collect(Collectors.toList());
    }
}
