package es.uc3m.android.travel_rex;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Get a reference to the "visited" collection for the current user
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference visitedRef = db.collection("users").document(user.getUid()).collection("visited");

            // Check if the "visited" collection exists for the current user
            visitedRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    // If the collection exists and is not empty, replace the fragment
                    if (getChildFragmentManager().findFragmentById(R.id.homeContainer) == null) {
                        getChildFragmentManager().beginTransaction()
                                .replace(R.id.homeContainer, new HomeActivityFragment())
                                .commit();
                    }
                }
            });
        }

        return rootView;
    }
}