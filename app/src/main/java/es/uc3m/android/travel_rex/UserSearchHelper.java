package es.uc3m.android.travel_rex;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class UserSearchHelper {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    public UserSearchHelper() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
    }

    public Task<QuerySnapshot> searchUserByEmail(String email) {
        // Create a query to search for users with the specified email
        Query query = usersCollection.whereEqualTo("email", email);

        // Execute the query and return the result
        return query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Check if any documents were found
            if (!queryDocumentSnapshots.isEmpty()) {
                // Get the first document (assuming email is unique)
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                // Handle the documentSnapshot
            } else {
                // No user found with the specified email
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });
    }
}
