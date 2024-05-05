// Source code: https://firebase.google.com/docs/firestore/manage-data/add-data#java_4
package es.uc3m.android.travel_rex;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;



import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddFragment extends Fragment {
    private View mView;
    private String imageUuid;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_add, container, false);

        // Add text changed listeners to the EditText fields
        EditText newPostTitle = mView.findViewById(R.id.new_post_title);
        EditText newPostDescription = mView.findViewById(R.id.new_post_description);
        EditText searchForDestination = mView.findViewById(R.id.search_for_destination);
        EditText newPostRating = mView.findViewById(R.id.new_post_rating);
        Button postButton = mView.findViewById(R.id.post_button);

        // Create FormListeners for Visited and Want to Go with the Button and EditText fields
        FormListener formListenerVisited = new FormListener(postButton, newPostTitle, newPostDescription, searchForDestination, newPostRating);
        FormListener formListenerWantToGo = new FormListener(postButton, searchForDestination);

        // Add the FormListener to each EditText field
        newPostTitle.addTextChangedListener(formListenerVisited);
        newPostDescription.addTextChangedListener(formListenerVisited);
        searchForDestination.addTextChangedListener(formListenerVisited);
        newPostRating.addTextChangedListener(formListenerVisited);

        // Set default function call to post
        mView.findViewById(R.id.post_button).setOnClickListener(v -> post());
        mView.findViewById(R.id.upload_photo).setOnClickListener(this::uploadPhoto);


        // Find the RadioGroup
        RadioGroup radioGroup = mView.findViewById(R.id.radioGroup);

        // Set a listener for RadioButton selection changes
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which RadioButton is checked
                if (checkedId == R.id.radioButton1) {
                    // Show all EditText fields
                    setEditTextVisibility(View.VISIBLE);
                    searchForDestination.addTextChangedListener(formListenerVisited);
                    mView.findViewById(R.id.post_button).setOnClickListener(v -> post());
                } else if (checkedId == R.id.radioButton2) {
                    // Hide all EditText fields except search_for_destination
                    setEditTextVisibility(View.GONE);
                    mView.findViewById(R.id.search_for_destination).setVisibility(View.VISIBLE);
                    searchForDestination.addTextChangedListener(formListenerWantToGo);
                    mView.findViewById(R.id.post_button).setOnClickListener(v -> addToWantToGo());
                } else if (checkedId == R.id.radioButton3) {
                    setEditTextVisibility(View.GONE);
                    mView.findViewById(R.id.find_friends).setVisibility(View.VISIBLE);
                    mView.findViewById(R.id.search_friend_button).setVisibility(View.VISIBLE);
                    mView.findViewById(R.id.search_friend_button).setOnClickListener(v -> findFriends());
                }
            }
        });
        return mView;
    }

    private void uploadPhoto(View view) {
        imageUuid = UUID.randomUUID().toString();
        Intent intent = new Intent(getActivity(), uploadPhoto.class);
        intent.putExtra("uuid", imageUuid);
        intent.putExtra("type", "visited");
        startActivity(intent);
    }

    private void setEditTextVisibility(int visibility) {
        mView.findViewById(R.id.new_post_title).setVisibility(visibility);
        mView.findViewById(R.id.new_post_description).setVisibility(visibility);
        mView.findViewById(R.id.search_for_destination).setVisibility(visibility);
        mView.findViewById(R.id.new_post_rating).setVisibility(visibility);
    }
    private void post() {
        EditText searchForDestination = mView.findViewById(R.id.search_for_destination);
        EditText newTitle = mView.findViewById(R.id.new_post_title);
        EditText newDescription = mView.findViewById(R.id.new_post_description);
        EditText newPostRating = mView.findViewById(R.id.new_post_rating);

        String destination = searchForDestination.getText().toString();
        String title = newTitle.getText().toString();
        String description = newDescription.getText().toString();
        Integer rating = Integer.parseInt(newPostRating.getText().toString());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        // If destination is found in user's Want to Go's, remove it
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Check if the want_to_go field exists in the document
                        if (documentSnapshot.contains("want_to_go")) {
                            // Retrieve the want_to_go field
                            Object wantToGoField = documentSnapshot.get("want_to_go");
                            if (wantToGoField instanceof Map) {
                                // Cast wantToGoField to a Map<String, Object>
                                Map<String, Object> wantToGoMap = (Map<String, Object>) wantToGoField;
                                // Check if the destination exists in want_to_go map
                                if (wantToGoMap.containsKey(destination)) {
                                    // Remove the destination from want_to_go map
                                    wantToGoMap.remove(destination);
                                    // Update the document with the modified want_to_go map
                                    db.collection("users")
                                            .document(user.getUid())
                                            .update("want_to_go", wantToGoMap);
                                }
                            }
                        }
                    }
                });

        // Post object to save to db
        Map<String, Object> postDetails = new HashMap<>();
        postDetails.put("destination", destination);
        postDetails.put("title", title);
        postDetails.put("description", description);
        postDetails.put("rating", rating);
        postDetails.put("timestamp", FieldValue.serverTimestamp());
        postDetails.put("imageUuid", imageUuid);

        // Add post to visited collection for user
        db.collection("users")
                .document(user.getUid())
                .collection("visited")
                .document()
                .set(postDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Post operation successful
                        Toast.makeText(getContext(), "Post successful", Toast.LENGTH_SHORT).show();
                        // Navigate back to the HomeFragment
                        MainActivity activity = (MainActivity) getActivity();
                        if (activity != null) {
                            activity.replaceFragment(new HomeFragment());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Toast.makeText(getContext(), "Post failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToWantToGo() {
        EditText searchForDestination = mView.findViewById(R.id.search_for_destination);
        String destination = searchForDestination.getText().toString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> data = new HashMap<>();
        data.put("want_to_go." + destination, null);

        db.collection("users")
                .document(user.getUid())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Post operation successful
                        Toast.makeText(getContext(), destination + " added to Want to Go list!", Toast.LENGTH_SHORT).show();
                        // Clear EditText fields
                        searchForDestination.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Toast.makeText(getContext(), "Could not add " + destination + " to Want to Go list." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    };

    private void findFriends() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        EditText findFriends = mView.findViewById(R.id.find_friends);
        String friendEmail = findFriends.getText().toString();

        // Use UserSearchHelper to search for the user with the specified email
        UserSearchHelper userSearchHelper = new UserSearchHelper();
        Task<QuerySnapshot> searchTask = userSearchHelper.searchUserByEmail(friendEmail);

        // Handle the result of the search task
        searchTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Get the first document from the QuerySnapshot
                    DocumentSnapshot friendDocument = querySnapshot.getDocuments().get(0);

                    db.collection("users")
                            .document(user.getUid())
                            .update("friend_uids", FieldValue.arrayUnion(friendDocument.getId()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Post operation successful
                                    Toast.makeText(getContext(), friendDocument.get("name") + " added to Friends!", Toast.LENGTH_SHORT).show();
                                    // Clear EditText fields
                                    findFriends.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure
                                    Toast.makeText(getContext(), "Could not add " + friendDocument.get("name") + " to Friends." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // No user found with the specified email
                    // Handle this case
                }
            } else {
                // Error occurred while searching
                Exception e = task.getException();
                // Handle the error
            }
        });

    }
}
