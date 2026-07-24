package com.nikki.saripos.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.nikki.saripos.R;
import com.nikki.saripos.model.BestSellerResult;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.BestSellerViewHolder> {

    private List<BestSellerResult> bestSellers = new ArrayList<>();

    public void setBestSellers(List<BestSellerResult> bestSellers) {
        this.bestSellers = bestSellers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BestSellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_best_seller, parent, false);
        return new BestSellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestSellerViewHolder holder, int position) {
        BestSellerResult item = bestSellers.get(position);
        holder.tvName.setText((position + 1) + ". " + item.productName);
        holder.tvCount.setText(item.totalSold + " sold");
    }

    @Override
    public int getItemCount() {
        return bestSellers.size();
    }

    static class BestSellerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCount;

        BestSellerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBestSellerName);
            tvCount = itemView.findViewById(R.id.tvBestSellerCount);
        }
    }
}