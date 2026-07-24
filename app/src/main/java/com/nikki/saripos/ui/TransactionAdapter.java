package com.nikki.saripos.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.nikki.saripos.R;
import com.nikki.saripos.model.Transaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    private List<Transaction> transactionList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
    private final OnTransactionClickListener listener;

    public TransactionAdapter(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactionList = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.tvDate.setText(dateFormat.format(new Date(transaction.date)));
        holder.tvTotal.setText("₱" + transaction.total);
        holder.tvStatus.setText(transaction.status);

        holder.itemView.setOnClickListener(v -> listener.onTransactionClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTotal, tvStatus;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            tvTotal = itemView.findViewById(R.id.tvTransactionTotal);
            tvStatus = itemView.findViewById(R.id.tvTransactionStatus);
        }
    }
}