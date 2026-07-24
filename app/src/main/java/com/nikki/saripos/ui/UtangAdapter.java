package com.nikki.saripos.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.nikki.saripos.R;
import com.nikki.saripos.model.UtangWithCustomer;

public class UtangAdapter extends RecyclerView.Adapter<UtangAdapter.UtangViewHolder> {

    public interface OnUtangClickListener {
        void onUtangClick(UtangWithCustomer utang);
    }

    private List<UtangWithCustomer> utangList = new ArrayList<>();
    private final OnUtangClickListener listener;

    public UtangAdapter(OnUtangClickListener listener) {
        this.listener = listener;
    }

    public void setUtangList(List<UtangWithCustomer> utangList) {
        this.utangList = utangList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UtangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_utang, parent, false);
        return new UtangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UtangViewHolder holder, int position) {
        UtangWithCustomer utang = utangList.get(position);
        holder.tvCustomerName.setText(utang.customerName);
        holder.tvBalance.setText("₱" + utang.remainingBalance + " owed");
        holder.tvStatus.setText(utang.status);

        if (utang.dueDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            holder.tvDueDate.setText("Due: " + sdf.format(utang.dueDate));
        } else {
            holder.tvDueDate.setText("No due date");
        }

        holder.itemView.setOnClickListener(v -> listener.onUtangClick(utang));
    }

    @Override
    public int getItemCount() {
        return utangList.size();
    }

    static class UtangViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvBalance, tvStatus, tvDueDate;

        UtangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvUtangCustomerName);
            tvBalance = itemView.findViewById(R.id.tvUtangBalance);
            tvStatus = itemView.findViewById(R.id.tvUtangStatus);
            tvDueDate = itemView.findViewById(R.id.tvUtangDueDate);
        }
    }
}