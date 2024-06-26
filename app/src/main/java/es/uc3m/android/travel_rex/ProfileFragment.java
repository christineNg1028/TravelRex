package es.uc3m.android.travel_rex;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;import com.bumptech.glide.Glide;

import java.util.Map;

import com.google.firebase.storage.StorageReference;
import android.widget.ImageView;
import com.google.firebase.storage.FirebaseStorage;
import android.net.Uri;
import com.google.android.gms.tasks.OnSuccessListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnFailureListener;
import java.util.AbstractMap;

// Chat GPT used to debug recycler views

public class ProfileFragment extends Fragment {
    private ImageView profileImageView;
    private StorageReference storageReference;
    private FirebaseUser user;
    private String displayName;
    private String location;

    private View mView;

    private RecyclerView visitedRecyclerView;
    private RecyclerView wantedRecyclerView;
    private List<String> wantedList;
    private List<Map.Entry<String, String>> visitedList;
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

        storageReference = FirebaseStorage.getInstance().getReference();
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mView.findViewById(R.id.logout_button).setOnClickListener(this::logout);
        // Call updateUserNameTextView() after inflating the layout
        mView.findViewById(R.id.profileIcon).setOnClickListener(this::uploadPhoto);
        fetchProfilePic();
        fetchUserData();

        profileImageView = mView.findViewById(R.id.profileIcon);

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
        Intent intent = new Intent(getActivity(), uploadPhoto.class);
        intent.putExtra("type", "profile");
        startActivity(intent);
    }

    private void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    private void fetchProfilePic(){
        String uid = user.getUid();
        StorageReference imageRef = storageReference.child("profile_images/"+uid);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Load image into ImageView
                RequestOptions requestOptions = new RequestOptions()
                        .transform(new CircleCrop());
                Glide.with(requireContext())
                        .load(uri)
                        .apply(requestOptions)
                        .into(profileImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
            }
        });
    };


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
                    // Extract place names from the map keys in reverse order and add them to wantedList
                    List<String> places = new ArrayList<>(wantedMap.keySet());
                    for (int i = places.size() - 1; i >= 0; i--) {
                        wantedList.add(places.get(i));
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
                    String imageUuid = document.getString("imageUuid");
                    visitedList.add(new AbstractMap.SimpleEntry<>(destination, imageUuid));
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