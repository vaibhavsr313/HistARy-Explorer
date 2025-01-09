package com.example.a;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LikedPlacesAdapter extends RecyclerView.Adapter<LikedPlacesAdapter.ViewHolder> {

    private List<LikedPlace> likedPlaces;
    private Context context;

    public LikedPlacesAdapter(Context context, List<LikedPlace> likedPlaces) {
        this.context = context;
        this.likedPlaces = likedPlaces;
    }

    @NonNull
    @Override
    public LikedPlacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_liked_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedPlacesAdapter.ViewHolder holder, int position) {

        LikedPlace place = likedPlaces.get(position);
        holder.nameTextView.setText(place.getName());
        Glide.with(context).load(place.getImageUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            // Handle click event if needed
        });
    }

    @Override
    public int getItemCount() {
        return likedPlaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoinrc);
            nameTextView = itemView.findViewById(R.id.nameinrc);
        }
    }
}
