package es.uc3m.android.travel_rex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.PlaceHolder>{

    // card adapter class
    private Context context;
    private ArrayList<PlacesCards> places;

    // constructor:
    public CardAdapter (Context context, ArrayList<PlacesCards> places) {
        this.context = context;
        this.places = places;
    }

        @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        return new PlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder holder, int position) {
        PlacesCards place = places.get(position);
        holder.setDetails(place);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    // view holder
    class PlaceHolder extends RecyclerView.ViewHolder {
        private TextView txtNameCard, txtDescriptionCard, txtRatingCard;

        PlaceHolder(View itemView){
            super(itemView);
            txtNameCard = itemView.findViewById(R.id.txtNameCard);
            txtDescriptionCard = itemView.findViewById(R.id.txtDescriptionCard);
            txtRatingCard = itemView.findViewById(R.id.txtRatingCard);
        }

        void setDetails (PlacesCards place) {
            txtNameCard.setText(place.getPlaceName());
            txtDescriptionCard.setText(String.format(place.getPlaceDescription()));
            txtRatingCard.setText(String.format(place.getPlaceDescription()));
        }
    }
}
