package samDatabase;

import com.google.gson.*;
import models.Document;
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
        // Create a custom Gson builder to handle nested collections
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Document.class, new DocumentTypeAdapter());

        this.gson = gsonBuilder.create();
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

    /**
     * Custom type adapter for Document to handle nested collections
     */
    private static class DocumentTypeAdapter
            implements JsonSerializer<Document>, JsonDeserializer<Document> {

        @Override
        public JsonElement serialize(Document document, Type type,
                                     JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            // Serialize basic document data
            jsonObject.add("id", new JsonPrimitive(document.getId()));

            // Serialize document fields
            JsonObject dataObject = new JsonObject();
            for (Map.Entry<String, Object> entry : document.getData().entrySet()) {
                dataObject.add(entry.getKey(), context.serialize(entry.getValue()));
            }
            jsonObject.add("data", dataObject);

            // Serialize nested collections
            JsonObject nestedCollectionsObject = new JsonObject();
            // You would need a method in Document to get nested collections
            // This is a placeholder and might need adjustment based on exact implementation
            try {
                java.lang.reflect.Method getNestedCollectionsMethod =
                        document.getClass().getDeclaredMethod("getNestedCollections");
                getNestedCollectionsMethod.setAccessible(true);
                Map<String, Map<String, Document>> nestedCollections =
                        (Map<String, Map<String, Document>>) getNestedCollectionsMethod.invoke(document);

                for (Map.Entry<String, Map<String, Document>> collectionEntry :
                        nestedCollections.entrySet()) {
                    JsonObject collectionObject = new JsonObject();
                    for (Map.Entry<String, Document> docEntry :
                            collectionEntry.getValue().entrySet()) {
                        collectionObject.add(docEntry.getKey(),
                                context.serialize(docEntry.getValue()));
                    }
                    nestedCollectionsObject.add(collectionEntry.getKey(), collectionObject);
                }
            } catch (Exception e) {
                // Handle potential reflection errors
                System.err.println("Error serializing nested collections: " + e.getMessage());
            }

            jsonObject.add("nestedCollections", nestedCollectionsObject);

            return jsonObject;
        }

        @Override
        public Document deserialize(JsonElement json, Type type,
                                    JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Deserialize document ID
            String id = jsonObject.get("id").getAsString();
            Document document = new Document(id);

            // Deserialize document data
            if (jsonObject.has("data")) {
                JsonObject dataObject = jsonObject.getAsJsonObject("data");
                for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
                    document.set(entry.getKey(),
                            context.deserialize(entry.getValue(), Object.class));
                }
            }

            // Deserialize nested collections
            if (jsonObject.has("nestedCollections")) {
                JsonObject nestedCollectionsObject =
                        jsonObject.getAsJsonObject("nestedCollections");

                for (Map.Entry<String, JsonElement> collectionEntry :
                        nestedCollectionsObject.entrySet()) {
                    String collectionName = collectionEntry.getKey();
                    document.createNestedCollection(collectionName);

                    JsonObject collectionObject = collectionEntry.getValue().getAsJsonObject();
                    for (Map.Entry<String, JsonElement> docEntry :
                            collectionObject.entrySet()) {
                        Document nestedDoc = context.deserialize(docEntry.getValue(), Document.class);
                        document.getNestedDocuments(collectionName)
                                .put(nestedDoc.getId(), nestedDoc);
                    }
                }
            }

            return document;
        }
    }

    public Document getNestedDocument(String collectionName, String documentId,
                                      String nestedCollectionName, String nestedDocumentId) {
        Document parentDocument = getDocument(collectionName, documentId);
        if (parentDocument != null) {
            return parentDocument.getNestedDocument(nestedCollectionName, nestedDocumentId);
        }
        return null;
    }
}