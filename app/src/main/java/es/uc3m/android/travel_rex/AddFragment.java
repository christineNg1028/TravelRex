// Source code: https://firebase.google.com/docs/firestore/manage-data/add-data#java_4
package es.uc3m.android.travel_rex;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class AddFragment extends Fragment {
    private View mView;

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

                    // Create a FormListener with the Button and EditText fields
                    FormListener formListenerVisited = new FormListener(postButton, newPostTitle, newPostDescription, searchForDestination, newPostRating);

                    // Add the FormListener to each EditText field
                    newPostTitle.addTextChangedListener(formListenerVisited);
                    newPostDescription.addTextChangedListener(formListenerVisited);
                    searchForDestination.addTextChangedListener(formListenerVisited);
                    newPostRating.addTextChangedListener(formListenerVisited);

                    mView.findViewById(R.id.post_button).setOnClickListener(v -> post());
                } else if (checkedId == R.id.radioButton2) {
                    // Hide all EditText fields except search_for_destination
                    setEditTextVisibility(View.GONE);
                    mView.findViewById(R.id.search_for_destination).setVisibility(View.VISIBLE);

                    FormListener formListenerWantToGo = new FormListener(postButton, searchForDestination);
                    searchForDestination.addTextChangedListener(formListenerWantToGo);

                    mView.findViewById(R.id.post_button).setOnClickListener(v -> addToWantToGo());
                }
            }
        });
        return mView;
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

        // Post object to save to db
        Map<String, Object> postDetails = new HashMap<>();
        postDetails.put("destination", destination);
        postDetails.put("title", title);
        postDetails.put("description", description);
        postDetails.put("rating", rating);
        db.collection("users").document(user.getUid()).collection("visited").document().set(postDetails);
        postDetails.put("timestamp", FieldValue.serverTimestamp());

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
                        // Clear EditText fields
                        searchForDestination.setText("");
                        newTitle.setText("");
                        newDescription.setText("");
                        newPostRating.setText("");
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

        db.collection("users")
                .document(user.getUid())
                .update("want_to_go", FieldValue.arrayUnion(destination))
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
}
