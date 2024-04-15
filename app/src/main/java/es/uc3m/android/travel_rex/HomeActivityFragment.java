package es.uc3m.android.travel_rex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private ArrayList<PlacesCards> placesList;

    public HomeActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_card_view, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerViewCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize your list of places (you need to populate this with your data)
        placesList = new ArrayList<>();

        placesList.add(new PlacesCards("Paris", "good trip", 7));
        placesList.add(new PlacesCards("Amsterdam", "bad trip", 2));
        placesList.add(new PlacesCards("Barcelona", "best trip", 10));

        // Initialize and set adapter
        adapter = new CardAdapter(requireContext(), placesList);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
