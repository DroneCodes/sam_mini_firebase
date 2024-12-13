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

    /**
     * Start the interactive terminal interface
     */
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
                case 0:
                    System.out.println("Exiting Mini Firebase Terminal Interface. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Print the menu of available operations
     */
    private void printMenu() {
        System.out.println("\n--- Mini Firebase Terminal Interface ---");
        System.out.println("1. Add Document");
        System.out.println("2. Retrieve Document");
        System.out.println("3. List Documents in Collection");
        System.out.println("4. Delete Document");
        System.out.println("5. Find Documents");
        System.out.println("6. Update Document");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Get user's menu choice
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

        System.out.print("Enter document ID: ");
        String documentId = scanner.nextLine();

        Document document = db.addDocument(collectionName, documentId);

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
                document.set(fieldName, intValue);
            } catch (NumberFormatException e) {
                document.set(fieldName, fieldValue);
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
     * Find documents based on a field condition
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

    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Mini Firebase Terminal Interface!");
        Main mainApp = new Main();
        mainApp.start();
    }
}