package com.nikki.saripos.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.nikki.saripos.R;
import com.nikki.saripos.model.RestockRecommendation;

public class RestockAdapter extends RecyclerView.Adapter<RestockAdapter.RestockViewHolder> {

    private List<RestockRecommendation> recommendations = new ArrayList<>();

    public void setRecommendations(List<RestockRecommendation> recommendations) {
        this.recommendations = recommendations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restock, parent, false);
        return new RestockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestockViewHolder holder, int position) {
        RestockRecommendation rec = recommendations.get(position);

        holder.tvName.setText(rec.productName);
        holder.tvPriority.setText(rec.priority);

        switch (rec.priority) {
            case "HIGH":
                holder.tvPriority.setBackgroundColor(Color.parseColor("#D32F2F"));
                holder.tvPriority.setTextColor(Color.WHITE);
                break;
            case "MEDIUM":
                holder.tvPriority.setBackgroundColor(Color.parseColor("#F9A825"));
                holder.tvPriority.setTextColor(Color.BLACK);
                break;
            default:
                holder.tvPriority.setBackgroundColor(Color.parseColor("#388E3C"));
                holder.tvPriority.setTextColor(Color.WHITE);
                break;
        }

        holder.tvAvgSales.setText(String.format(Locale.getDefault(),
                "Average sales: %.1f/day", rec.averageDailySales));

        holder.tvStock.setText("Stock remaining: " + rec.currentStock);

        if (rec.daysUntilStockout == Double.MAX_VALUE) {
            holder.tvEstimate.setText("No recent sales data");
        } else if (rec.daysUntilStockout < 1) {
            holder.tvEstimate.setText("Estimated to run out today");
        } else {
            holder.tvEstimate.setText(String.format(Locale.getDefault(),
                    "Estimated to run out in %.0f day(s)", rec.daysUntilStockout));
        }

        if (rec.recommendedReorderQty > 0) {
            holder.tvRecommendation.setText("Recommended reorder: " + rec.recommendedReorderQty + " units");
        } else {
            holder.tvRecommendation.setText("Do not reorder yet");
        }
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    static class RestockViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPriority, tvAvgSales, tvStock, tvEstimate, tvRecommendation;

        RestockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestockProductName);
            tvPriority = itemView.findViewById(R.id.tvRestockPriority);
            tvAvgSales = itemView.findViewById(R.id.tvRestockAvgSales);
            tvStock = itemView.findViewById(R.id.tvRestockStock);
            tvEstimate = itemView.findViewById(R.id.tvRestockEstimate);
            tvRecommendation = itemView.findViewById(R.id.tvRestockRecommendation);
        }
    }
}