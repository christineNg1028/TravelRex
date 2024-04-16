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
        mView.findViewById(R.id.post_button).setOnClickListener(v -> post());
        return mView;
    }

    private void post() {

        EditText searchForDestination = mView.findViewById(R.id.search_for_destination);
        EditText newTitle = mView.findViewById(R.id.new_post_title);
        EditText newDescription = mView.findViewById(R.id.new_post_description);

        String destination = searchForDestination.getText().toString();
        String title = newTitle.getText().toString();
        String description = newDescription.getText().toString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        // Post object to save to db
        Map<String, Object> postDetails = new HashMap<>();
        postDetails.put("destination", destination);
        postDetails.put("title", title);
        postDetails.put("description", description);
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
}
