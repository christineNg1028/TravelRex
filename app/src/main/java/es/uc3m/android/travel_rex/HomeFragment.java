package es.uc3m.android.travel_rex;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        // Check if the fragment_container is empty (to avoid overlapping fragments)
        if (getChildFragmentManager().findFragmentById(R.id.homeContainer) == null) {
            // Display CardViewFragment within fragment_container if it's not already added
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.homeContainer, new HomeActivityFragment())
                    .commit();
        }

        Log.d("HomeFragment", "onCreateView: Checking if HomeActivityFragment is already added");
        if (getChildFragmentManager().findFragmentById(R.id.homeContainer) == null) {
            Log.d("HomeFragment", "onCreateView: HomeActivityFragment is not added, adding now");
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.homeContainer, new HomeActivityFragment())
                    .commit();
        } else {
            Log.d("HomeFragment", "onCreateView: HomeActivityFragment already added, skipping");
        }

        return rootView;
    }
}