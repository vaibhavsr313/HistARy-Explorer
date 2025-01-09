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

public class VisitedPlacesAdapter extends RecyclerView.Adapter<VisitedPlacesAdapter.ViewHolder>{

    private List<VisitedPlace> visitedPlaces;
    private Context context;

    public VisitedPlacesAdapter(Context context, List<VisitedPlace> visitedPlaces) {
        this.context = context;
        this.visitedPlaces = visitedPlaces;
    }


    @NonNull
    @Override
    public VisitedPlacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_visited_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitedPlacesAdapter.ViewHolder holder, int position) {

        VisitedPlace place = visitedPlaces.get(position);
        holder.nameTextView.setText(place.getName());
        Glide.with(context).load(place.getImageUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            // Handle click event if needed
        });

    }

    @Override
    public int getItemCount() {
        return visitedPlaces.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoinvisit);
            nameTextView = itemView.findViewById(R.id.nameinvisit);
        }
    }
}
