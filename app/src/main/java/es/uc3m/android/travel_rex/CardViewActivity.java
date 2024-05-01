package es.uc3m.android.travel_rex;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private ArrayList<PlacesCards> placesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        InitializeCardView();
    }

    private void InitializeCardView(){
        recyclerView = recyclerView.findViewById(R.id.recyclerViewCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placesArrayList = new ArrayList<>();

        adapter = new CardAdapter(this, placesArrayList);
        recyclerView.setAdapter(adapter);
    }
}
