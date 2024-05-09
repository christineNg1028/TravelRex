package es.uc3m.android.travel_rex;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;

public class HomeActivityFragment extends Fragment {
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private ArrayList<PlacesCards> placesList;
    private String userName;

    public HomeActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_card_view, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerViewCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize your list of places (you need to populate this with your data)
        placesList = new ArrayList<>();

        // Initialize and set adapter
        adapter = new CardAdapter(requireContext(), placesList);
        recyclerView.setAdapter(adapter);
        fetchUserData();

        return rootView;
    }

    private void fetchUserData() {
        // Get the UID of the current user
        String uid = user.getUid();

        // Get the Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the current user's document
        DocumentReference currentUserDocument = db.collection("users").document(uid);

        // Fetch the current user's name
        currentUserDocument.get().addOnSuccessListener(documentSnapshot -> {
                userName = documentSnapshot.getString("name");
        });

        // Get a reference to the collection of visited places for the current user
        CollectionReference visitedRef = currentUserDocument.collection("visited");

        // Query to get friend UIDs
        currentUserDocument.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> friendUids = (List<String>) documentSnapshot.get("friend_uids");
                if (friendUids != null) {
                    // Iterate over the list of friend UIDs
                    for (String friendUid : friendUids) {
                        // Get a reference to the friend's document
                        DocumentReference friendDocument = db.collection("users").document(friendUid);

                        // Fetch the friend's user name
                        friendDocument.get().addOnSuccessListener(friendSnapshot -> {
                            String friendName = friendSnapshot.getString("name");

                            // Get a reference to the friend's visited places
                            CollectionReference friendVisitedRef = friendDocument.collection("visited");

                            // Fetch the friend's visited places
                            friendVisitedRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    // Access each friend's place data and add it to the placesList
                                    String destination = snapshot.getString("destination");
                                    String description = snapshot.getString("description");
                                    String visitedImage = snapshot.getString("imageUuid");
                                    int rating = snapshot.contains("rating") ? snapshot.getLong("rating").intValue() : 0;

                                    placesList.add(new PlacesCards(friendName, destination, description, rating, visitedImage, uid));
                                }
                                // Notify adapter that data has been updated
                                adapter.notifyDataSetChanged();
                            });
                        });
                    }
                }
            }
        });

        // Query to get the user's visited places and their friends' visited places, ordered by timestamp
        Query feed = visitedRef.orderBy("timestamp", Query.Direction.DESCENDING);

        // Fetch the data
        feed.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                placesList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Access each document here
                    String documentId = document.getId();
                    String destination = document.getString("destination");
                    String description = document.getString("description");
                    String visitedImage = document.getString("imageUuid");
                    int rating = document.contains("rating") ? document.getLong("rating").intValue() : 0;

                    placesList.add(new PlacesCards(userName, destination, description, rating, visitedImage, uid));
                }
                // Notify adapter that data has been updated
                adapter.notifyDataSetChanged();
            }
        });
    }
}
