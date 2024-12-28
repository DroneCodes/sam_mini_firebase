import models.Document;
import samDatabase.SamDatabase;

import java.util.List;
import java.util.Scanner;

public class Main {
    private SamDatabase db;
    private Scanner scanner;

    public Main() {
        this.db = new SamDatabase();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            printMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addDocument();
                    break;
                case 2:
                    retrieveDocument();
                    break;
                case 3:
                    listDocuments();
                    break;
                case 4:
                    deleteDocument();
                    break;
                case 5:
                    findDocuments();
                    break;
                case 6:
                    updateDocument();
                    break;
                case 7:
                    manageNestedDocuments();
                    break;
                case 0:
                    System.out.println("Exiting Sam's Mini Firebase Terminal Interface. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n---Sam's Mini Firebase Terminal Interface ---");
        System.out.println("1. Add Document");
        System.out.println("2. Retrieve Document");
        System.out.println("3. List Documents in Collection");
        System.out.println("4. Delete Document");
        System.out.println("5. Find Documents");
        System.out.println("6. Update Document");
        System.out.println("7. Manage Nested Documents");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Get user's menu choice
     *
     * @return Selected menu option
     */
    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Add a new document to a collection
     */
    private void addDocument() {
        System.out.print("Enter collection name: ");
        String collectionName = scanner.nextLine();

        System.out.print("Would you like to generate an ID automatically? (y/n): ");
        String choice = scanner.nextLine();

        Document document;
        String documentId; // Declare documentId outside the if/else block

        if (choice.toLowerCase().startsWith("y")) {
            document = db.addDocumentWithAutoId(collectionName);
            documentId = document.getId(); // Store the generated ID
            System.out.println("Generated document ID: " + documentId);
        } else {
            System.out.print("Enter document ID: ");
            documentId = scanner.nextLine(); // Store the manually entered ID
            document = db.addDocument(collectionName, documentId);
        }

        while (true) {
            System.out.print("Enter field name (or 'done' to finish): ");
            String fieldName = scanner.nextLine();

            if ("done".equalsIgnoreCase(fieldName)) {
                break;
            }

            System.out.print("Enter value for " + fieldName + ": ");
            String fieldValue = scanner.nextLine();

            // Try to parse numeric values
            try {
                int intValue = Integer.parseInt(fieldValue);
                db.updateDocumentField(collectionName, documentId, fieldName, intValue);
            } catch (NumberFormatException e) {
                db.updateDocumentField(collectionName, documentId, fieldName, fieldValue);
            }
        }

        System.out.println("Document added successfully!");
    }

    /**
     * Retrieve a specific document
     */
    private void retrieveDocument() {
        System.out.print("Enter collection name: ");
        String collectionName = scanner.nextLine();

        System.out.print("Enter document ID: ");
        String documentId = scanner.nextLine();

        Document document = db.getDocument(collectionName, documentId);

        if (document != null) {
            System.out.println("Document found:");
            System.out.println(document);
        } else {
            System.out.println("Document not found.");
        }
    }

    /**
     * List all documents in a collection
     */
    private void listDocuments() {
        System.out.print("Enter collection name: ");
        String collectionName = scanner.nextLine();

        List<Document> documents = db.getDocuments(collectionName);

        if (documents.isEmpty()) {
            System.out.println("No documents found in the collection.");
        } else {
            System.out.println("Documents in collection:");
            documents.forEach(System.out::println);
        }
    }

    /**
     * Delete a document from a collection
     */
    private void deleteDocument() {
        System.out.print("Enter collection name: ");
        String collectionName = scanner.nextLine();

        System.out.print("Enter document ID: ");
        String documentId = scanner.nextLine();

        boolean deleted = db.deleteDocument(collectionName, documentId);

        if (deleted) {
            System.out.println("Document deleted successfully!");
        } else {
            System.out.println("Document not found or deletion failed.");
        }
    }

    /**
     * Find documents based on a field condition.
     */
    private void findDocuments() {
        System.out.print("Enter collection name: ");
        String collectionName = scanner.nextLine();

        System.out.print("Enter field to search: ");
        String fieldName = scanner.nextLine();

        System.out.print("Enter value to match: ");
        String fieldValue = scanner.nextLine();

        // Try to parse as integer if possible
        Object searchValue;
        try {
            searchValue = Integer.parseInt(fieldValue);
        } catch (NumberFormatException e) {
            searchValue = fieldValue;
        }

        List<Document> foundDocuments = db.findDocuments(collectionName, fieldName, searchValue);

        if (foundDocuments.isEmpty()) {
            System.out.println("No documents found matching the condition.");
        } else {
            System.out.println("Matching documents:");
            foundDocuments.forEach(System.out::println);
        }
    }

    /**
     * Update an existing document
     */
    private void updateDocument() {
        System.out.print("Enter collection name: ");
        String collectionName = scanner.nextLine();

        System.out.print("Enter document ID: ");
        String documentId = scanner.nextLine();

        Document document = db.getDocument(collectionName, documentId);

        if (document != null) {
            while (true) {
                System.out.print("Enter field name to update (or 'done' to finish): ");
                String fieldName = scanner.nextLine();

                if ("done".equalsIgnoreCase(fieldName)) {
                    break;
                }

                System.out.print("Enter new value for " + fieldName + ": ");
                String fieldValue = scanner.nextLine();

                // Try to parse numeric values
                try {
                    int intValue = Integer.parseInt(fieldValue);
                    document.set(fieldName, intValue);
                } catch (NumberFormatException e) {
                    document.set(fieldName, fieldValue);
                }
            }

            System.out.println("Document updated successfully!");
        } else {
            System.out.println("Document not found.");
        }
    }

    private void manageNestedDocuments() {
        System.out.println("\nNested Documents Management:");
        System.out.println("1. Add Nested Document");
        System.out.println("2. Retrieve Nested Document");
        System.out.print("Enter your choice: ");

        int choice = getUserChoice();

        switch (choice) {
            case 1:
                addNestedDocument();
                break;
            case 2:
                retrieveNestedDocument();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void addNestedDocument() {
        System.out.print("Enter parent collection name: ");
        String parentCollectionName = scanner.nextLine();

        System.out.print("Enter parent document ID: ");
        String parentDocumentId = scanner.nextLine();

        Document parentDocument = db.getDocument(parentCollectionName, parentDocumentId);

        if (parentDocument == null) {
            System.out.println("Parent document not found.");
            return;
        }

        System.out.print("Enter nested collection name: ");
        String nestedCollectionName = scanner.nextLine();

        System.out.print("Enter nested document ID: ");
        String nestedDocumentId = scanner.nextLine();

        Document nestedDocument = parentDocument.addNestedDocument(nestedCollectionName, nestedDocumentId);

        while (true) {
            System.out.print("Enter nested document field name (or 'done' to finish): ");
            String fieldName = scanner.nextLine();

            if ("done".equalsIgnoreCase(fieldName)) {
                break;
            }

            System.out.print("Enter value for " + fieldName + ": ");
            String fieldValue = scanner.nextLine();

            // Try to parse numeric values
            try {
                int intValue = Integer.parseInt(fieldValue);
                nestedDocument.set(fieldName, intValue);
            } catch (NumberFormatException e) {
                nestedDocument.set(fieldName, fieldValue);
            }
        }

        System.out.println("Nested document added successfully!");
    }

    private void retrieveNestedDocument() {
        System.out.print("Enter parent collection name: ");
        String parentCollectionName = scanner.nextLine();

        System.out.print("Enter parent document ID: ");
        String parentDocumentId = scanner.nextLine();

        System.out.print("Enter nested collection name: ");
        String nestedCollectionName = scanner.nextLine();

        System.out.print("Enter nested document ID: ");
        String nestedDocumentId = scanner.nextLine();

        Document nestedDocument = db.getNestedDocument(
                parentCollectionName, parentDocumentId,
                nestedCollectionName, nestedDocumentId
        );

        if (nestedDocument != null) {
            System.out.println("Nested Document found:");
            System.out.println(nestedDocument);
        } else {
            System.out.println("Nested document not found.");
        }
    }

    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Sam's Mini Firebase Terminal Interface!");
        Main mainApp = new Main();
        mainApp.start();
    }
}