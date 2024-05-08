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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;
import androidx.annotation.NonNull;

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

        // Get the firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference currentUserDocument = db.collection("users").document(uid);

        currentUserDocument.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get the name field from the document
                userName = documentSnapshot.getString("name");
            }
        });

        CollectionReference visitedRef = currentUserDocument.collection("visited");

        // Get friend posts
        currentUserDocument.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> friendUids = (List<String>) documentSnapshot.get("friend_uids");
                            if (friendUids != null) {
                                // Iterate over the list of friend UIDs
                                for (String friendUid : friendUids) {
                                    // Get a reference to the friend's visited posts
                                    CollectionReference friendVisitedRef = db.collection("users").document(friendUid).collection("visited");

                                    // Fetch and copy the friend's visited posts to the current user's visited posts
                                    friendVisitedRef.get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                        visitedRef.add(snapshot.getData());
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            });
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        Query feed = visitedRef.orderBy("timestamp", Query.Direction.DESCENDING);

        feed.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        placesList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access each document here
                            String documentId = document.getId();
                            String destination = document.getString("destination");
                            //String time = document.getString("timestamp");
                            String description = document.getString("description");
                            String visitedImage = document.getString("imageUuid");
                            int rating = 0;
                            if (document.contains("rating")) {
                                rating = document.getLong("rating").intValue();
                           }

                            placesList.add(new PlacesCards(userName, destination, description, rating, visitedImage, uid));

                            // notify adapter that data has been updated
                            adapter.notifyDataSetChanged();
                            // Process the document data as needed
                            // For example, you might want to create a model object or print the data
                        }
                    }
                });

    }
}
