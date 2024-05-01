package es.uc3m.android.travel_rex;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private FirebaseUser user;
    private String displayName;
    private String location;

    private View mView;

    private RecyclerView visitedRecyclerView;
    private RecyclerView wantedRecyclerView;
    private List<String> wantedList;
    private List<String> visitedList;
    private VisitedAdapter visitedAdapter;
    private WantedAdapter wantedAdapter;


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
        mView.findViewById(R.id.profileIcon).setOnClickListener(this::uploadPhoto);
        fetchUserData();

        visitedRecyclerView = mView.findViewById(R.id.visitedRecyclerView);
        visitedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        wantedRecyclerView = mView.findViewById(R.id.wantedRecyclerView);
        wantedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        visitedList = new ArrayList<>();
        wantedList = new ArrayList<>();

        visitedAdapter = new VisitedAdapter(visitedList);
        visitedRecyclerView.setAdapter(visitedAdapter);

        wantedAdapter = new WantedAdapter(wantedList);
        wantedRecyclerView.setAdapter(wantedAdapter);

        return mView;
    }

    private void uploadPhoto(View view) {
        startActivity(new Intent(getActivity(), uploadPhoto.class));
    }

    private void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }




    private void fetchUserData() {
        if (user == null) {
            // Handle null user
            return;
        }

        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        CollectionReference visitedRef = userRef.collection("visited");

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                displayName = documentSnapshot.getString("name");
                location = documentSnapshot.getString("location");

                Map<String, Object> wantedMap = (Map<String, Object>) documentSnapshot.get("want_to_go");
                if (wantedMap != null) {
                    wantedList.clear();
                    // Add place names from the map keys to wantedList
                    for (String place : wantedMap.keySet()) {
                        wantedList.add(place);
                    }
                    wantedAdapter.notifyDataSetChanged();
                } else {
                    // Handle null map field
                }


                updateUserTextViews();
            }
            else {
                // Handle document not exists
                return;
            }
        }).addOnFailureListener(e -> {
            // Handle failure
        });



        Query visited = visitedRef.orderBy("timestamp", Query.Direction.DESCENDING);

        visited.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                visitedList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String destination = document.getString("destination");
                    visitedList.add(destination);
                }
                visitedAdapter.notifyDataSetChanged();
            } else {
                // Handle unsuccessful query
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