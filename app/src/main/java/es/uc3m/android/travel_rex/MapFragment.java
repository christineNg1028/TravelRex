// Source code:
package es.uc3m.android.travel_rex;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private View mView;
    private FirebaseUser user;
    private ArrayList<String[]> destinations;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        user = FirebaseAuth.getInstance().getCurrentUser();
        mView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return mView;
    }
    public void onMapReady(GoogleMap googleMap) {
        // Get posts for current user long and lat
//        LatLng sydney = new LatLng(-33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        getCurrentUserVisited(googleMap);
        getFriendsVisited(googleMap);
    }

    private void getCurrentUserVisited(GoogleMap googleMap) {
        // Get the UID of the current user
        String uid = user.getUid();

        // Get the firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference visitedRef = db.collection("users").document(uid).collection("visited");

        visitedRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double lng = document.getDouble("long");
                            Double lat = document.getDouble("lat");
                            String title = document.getString("title");

                            // Check if lat and lng are not null before creating LatLng object
                            if (lat != null && lng != null) {
                                LatLng new_dest = new LatLng(lat, lng);
                                // Now you can use 'new_dest' safely
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new_dest)
                                        .title(title));
                            } else {
                            }
                        }
                    }
                });
    }

    private void getFriendsVisited(GoogleMap googleMap) {
        // Get the UID of the current user
        String uid = user.getUid();

        // Get the firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create an array to store all visited posts
        List<DocumentSnapshot> allVisitedPosts = new ArrayList<>();

        db.collection("users").document(uid).get()
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

                                    // Fetch and copy the friend's visited posts to the allVisitedPosts list
                                    friendVisitedRef.get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    // Add all visited posts to the allVisitedPosts list
                                                    allVisitedPosts.addAll(queryDocumentSnapshots.getDocuments());

                                                    // After fetching all visited posts for all friends, add markers to the map
                                                    addMarkersToMap(googleMap, allVisitedPosts);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle failure
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
                        // Handle failure
                    }
                });
    }

    //TODO: call this in the getCurrentUserVisited function
    private void addMarkersToMap(GoogleMap googleMap, List<DocumentSnapshot> visitedPosts) {
        for (DocumentSnapshot document : visitedPosts) {
            Double lng = document.getDouble("long");
            Double lat = document.getDouble("lat");
            String title = document.getString("title");

            // Check if lat and lng are not null before creating LatLng object
            if (lat != null && lng != null) {
                LatLng new_dest = new LatLng(lat, lng);
                // Now you can use 'new_dest' safely
                googleMap.addMarker(new MarkerOptions()
                        .position(new_dest)
                        .title(title));
            }
        }
    }

}