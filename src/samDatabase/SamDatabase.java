package samDatabase;

import models.Document;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SamDatabase {
    // File to store database state
    private static final String DATABASE_FILE = "sam_database.json";

    // Main storage structure: collection name -> Documents
    private final Map<String, Map<String, Document>> collections;

    // Gson for JSON serialization/deserialization
    private final Gson gson;

    public SamDatabase() {
        // Initialize with pretty-printing for readability
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.collections = new ConcurrentHashMap<>();

        // Load existing data on initialization
        loadDatabase();
    }

    /**
     * Load database state from file
     */
    private void loadDatabase() {
        try {
            File file = new File(DATABASE_FILE);
            if (file.exists()) {
                try (Reader reader = new FileReader(file)) {
                    // Define the type for deserialization
                    Type type = new TypeToken<Map<String, Map<String, Document>>>(){}.getType();

                    // Load collections from file
                    Map<String, Map<String, Document>> loadedCollections =
                            gson.fromJson(reader, type);

                    if (loadedCollections != null) {
                        // Replace current collections with loaded ones
                        collections.clear();
                        collections.putAll(loadedCollections);
                        System.out.println("Database loaded successfully.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }

    /**
     * Save current database state to file
     */
    private void saveDatabase() {
        try (Writer writer = new FileWriter(DATABASE_FILE)) {
            gson.toJson(collections, writer);
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    /**
     * Create a new collection if it doesn't exist
     * @param collectionName Name of the collection
     */
    public void createCollection(String collectionName) {
        collections.putIfAbsent(collectionName, new ConcurrentHashMap<>());
        saveDatabase(); // Persist the change
    }

    /**
     * Add a document to a collection
     * @param collectionName Name of the collection
     * @param documentId ID of the document
     * @return The created document
     */
    public Document addDocument(String collectionName, String documentId) {
        // Ensure collection exists
        createCollection(collectionName);
        Document document = new Document(documentId);
        collections.get(collectionName).put(documentId, document);
        saveDatabase(); // Persist the new document
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
            boolean result = collection.remove(documentId) != null;
            if (result) {
                saveDatabase(); // Persist the deletion
            }
            return result;
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