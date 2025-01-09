package com.example.a;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FactsAdapter extends RecyclerView.Adapter<FactsAdapter.FactViewHolder> {
    private List<String> facts;

    public FactsAdapter(List<String> facts) {
        this.facts = facts;
        Log.d("FactsAdapter", "Facts received: " + facts.toString());
    }

    @NonNull
    @Override
    public FactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facts_item, parent, false);
        return new FactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FactViewHolder holder, int position) {
        Log.d("FactsAdapter", "Setting fact: " + facts.get(position));

        // Set the index and the fact text
        holder.factIndex.setText(String.valueOf(position + 1) + ".");  // +1 for human-friendly indexing
        holder.factText.setText(facts.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d("FactsAdapter", "Facts count: " + facts.size());
        return facts.size();
    }

    public static class FactViewHolder extends RecyclerView.ViewHolder {
        TextView factText;
        TextView factIndex;

        public FactViewHolder(@NonNull View itemView) {
            super(itemView);
            factText = itemView.findViewById(R.id.fact_text);
            factIndex = itemView.findViewById(R.id.fact_index); // Access the index TextView
        }
    }
}
