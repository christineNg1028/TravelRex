// Source code:
package es.uc3m.android.travel_rex;

import android.os.Bundle;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

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
        getUserVisited(googleMap);
    }

    private void getUserVisited(GoogleMap googleMap) {
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
}