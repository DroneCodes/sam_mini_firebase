package features;

import models.Document;
import samDatabase.SamDatabase;

import java.util.List;

public class Features {
    private final SamDatabase db;

    public Features(SamDatabase database) {
        this.db = database;
    }

    /**
     * Demonstration of the basic operations
     */

    public void demonstrationOfOperations() {
        // Create a users collection
        Document user1 = db.addDocument("users", "user1");
        user1.set("name", "John Doe");
        user1.set("age", 30);
        user1.set("email", "john@example.com");

        Document user2 = db.addDocument("users", "user2");
        user2.set("name", "Jane Smith");
        user2.set("age", 25);
        user2.set("email", "jane@example.com");

        //Retrieve and print a document
        Document retrieveUser = db.getDocument("users", "user1");
        System.out.println("Retrieved User: " + retrieveUser.getData());

        // Find documents by condition
        List<Document> youngUsers = db.findDocuments("users", "age", 25);
        System.out.println("Users aged 25: " + youngUsers.size());

        printAllUsers();
        deleteUserAndVerify("user1");
    }

    /**
     * Print all users in the database
     */
    private void printAllUsers() {
        List<Document> allUsers = db.getDocuments("users");
        System.out.println("All Users:");
        allUsers.forEach(user -> System.out.println(user));
    }

    /**
     * Delete a user and verify deletion
     * @param userId ID of the user to delete
     */

    private void deleteUserAndVerify(String userId) {
        boolean deleted = db.deleteDocument("users", userId);
        System.out.println("User " + userId + " deleted: " + deleted);

        Document deletedUser = db.getDocument("users", userId);
        System.out.println("Verify deletion - User exists: " + (deletedUser != null));
    }
}
