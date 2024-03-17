package com.example.mapsproject.CustomAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mapsproject.R;
import com.google.android.libraries.places.api.model.Review;

public class CustomReviewAdapter extends RecyclerView.Adapter<CustomReviewAdapter.ViewHolder> {
    private final Review[] reviews;
    private Context context;
    public CustomReviewAdapter(Context context, int layoutToBeInflated, Review[] reviews) {
        this.reviews = reviews;
        this.context = context;
        Log.i("CustomReviewAdapter", reviews.length + "");
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("onCreateViewHolder",1 + "");

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review, parent, false);
        Log.i("onCreateViewHolder",2 + "");

        ViewHolder viewHolder = new ViewHolder(itemView);

        Log.i("onCreateViewHolder",3 + "");

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.i("onBindViewHolder",position + "");

        if (position >= 0 && position < reviews.length) {
            holder.name.setText(reviews[position].getAuthorAttribution().getName());
            holder.description.setText(reviews[position].getText());
            holder.time.setText(reviews[position].getPublishTime());
            holder.ratingBar.setRating(reviews[position].getRating().floatValue());

            Glide.with(context)
                    .load(reviews[position].getAuthorAttribution().getPhotoUri())
                    .into(holder.icon);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        TextView description;
        TextView time;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.photo);
            description = itemView.findViewById(R.id.reviewText);
            time = itemView.findViewById(R.id.time);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }

}
