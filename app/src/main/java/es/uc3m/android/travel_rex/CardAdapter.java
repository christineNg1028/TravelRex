package es.uc3m.android.travel_rex;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;


import java.util.ArrayList;
import java.util.Locale;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.PlaceHolder>{

    private StorageReference storageReference;

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
        storageReference = FirebaseStorage.getInstance().getReference();

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
        private TextView txtNameCard, txtDescriptionCard, userName;
        private ImageView visitedImageCard;
        private RatingBar ratingBar;

        PlaceHolder(View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.postUser);
            txtNameCard = itemView.findViewById(R.id.txtNameCard);
            txtDescriptionCard = itemView.findViewById(R.id.txtDescriptionCard);
            visitedImageCard = itemView.findViewById(R.id.visitedImage);
            ratingBar = itemView.findViewById(R.id.txtRatingCard);
        }

        void setDetails (PlacesCards place) {
            txtNameCard.setText(place.getPlaceName());
            txtDescriptionCard.setText(String.format(place.getPlaceDescription()));
            ratingBar.setRating(place.getPlaceRating());
            userName.setText(place.getUserName());

            if (place.getVisitedImage() != null) {
                StorageReference imageRef = storageReference.child("visited_images/"+place.getVisitedImage().toString());
                fetchVisitedPic(imageRef,visitedImageCard);
            }
        }
    }

    public void fetchVisitedPic(StorageReference imageRef, ImageView visitedImageCard) {
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Load image into ImageView
                RequestOptions requestOptions = new RequestOptions();
                Glide.with(visitedImageCard.getContext())
                        .load(uri)
                        .apply(requestOptions)
                        .into(visitedImageCard);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
            }
        });
    }
}
