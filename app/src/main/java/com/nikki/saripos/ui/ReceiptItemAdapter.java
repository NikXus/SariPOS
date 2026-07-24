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
import com.nikki.saripos.model.TransactionItem;

public class ReceiptItemAdapter extends RecyclerView.Adapter<ReceiptItemAdapter.LineViewHolder> {

    private List<TransactionItem> items = new ArrayList<>();

    public void setItems(List<TransactionItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipt_line, parent, false);
        return new LineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LineViewHolder holder, int position) {
        TransactionItem item = items.get(position);
        holder.tvName.setText(item.productName + " x" + item.quantity);
        holder.tvTotal.setText("₱" + item.total);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTotal;

        LineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvLineName);
            tvTotal = itemView.findViewById(R.id.tvLineTotal);
        }
    }
}