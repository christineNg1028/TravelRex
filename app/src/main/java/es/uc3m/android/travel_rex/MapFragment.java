package es.uc3m.android.travel_rex;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

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
        getUserVisited(googleMap);
    }

    private void getUserVisited(GoogleMap googleMap) {
        // Get the UID of the current user
        String uid = user.getUid();

        // Get the firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        CollectionReference visitedRef = db.collection("users").document(uid).collection("visited");

        visitedRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String destination = document.getString("destination");
                            String title = document.getString("title");

                            // Check if destination string is not null before making the API call
                            if (destination != null) {
                                fetchCoordinates(googleMap, destination, title, queue);
                            }
                        }
                    } else {
                        // Handle the error
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchCoordinates(GoogleMap googleMap, String destination, String title, RequestQueue queue) {
        String url = "https://geocode.xyz/" + destination.replace(" ", "%20") + "?json=1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        double lat = jsonObject.getDouble("latt");
                        double lng = jsonObject.getDouble("longt");

                        LatLng new_dest = new LatLng(lat, lng);
                        googleMap.addMarker(new MarkerOptions()
                                .position(new_dest)
                                .title(title));
                    } catch (JSONException e) {
                        Log.e("JSONError", "Failed to parse JSON", e);
                    }
                }, error -> {
            Log.e("VolleyError", "Error with Volley request", error);
        });

        queue.add(stringRequest);
    }
}