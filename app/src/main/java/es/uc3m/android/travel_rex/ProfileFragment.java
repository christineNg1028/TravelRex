package es.uc3m.android.travel_rex;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Context;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private FirebaseUser user;
    private String displayName;
    private String location;

    private View mView;

    Context context;



    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize FirebaseUser and displayName in onCreate
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            displayName = user.getUid();

        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mView.findViewById(R.id.logout_button).setOnClickListener(this::logout);
        // Call updateUserNameTextView() after inflating the layout
        mView.findViewById(R.id.imageView).setOnClickListener(this::uploadPhoto);
        fetchUserData();

        return mView;
    }

    private void uploadPhoto(View view) {
        startActivity(new Intent(getActivity(), uploadPhoto.class));
    }

    private void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        context.startActivity(new Intent(context, LoginActivity.class));
    }


    private void fetchUserData() {
        // Get the UID of the current user
        String uid = user.getUid();

        // Get the firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        // Get the document
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // if document exists, update fields
                displayName = documentSnapshot.getString("name");
                location = documentSnapshot.getString("location");

                // Call method to update UI with user data
                updateUserTextViews();
            } else {
               // error message?
                return;
            }
        });
    }
    public void updateUserTextViews() {
        TextView userNameTextView = mView.findViewById(R.id.userNameTextView);
        TextView userLocationTextView = mView.findViewById(R.id.userLocation);
        if (displayName != null) {
            userNameTextView.setText(displayName);
        }
        if (location != null) {
            userLocationTextView.setText(location);
        }
        else {
            userNameTextView.setText("Display Name Not Available");
        }
    }
}