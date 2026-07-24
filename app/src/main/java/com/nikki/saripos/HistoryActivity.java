package com.nikki.saripos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nikki.saripos.ui.TransactionAdapter;
import com.nikki.saripos.databinding.ActivityHistoryBinding;
import com.nikki.saripos.viewmodel.TransactionViewModel;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new TransactionAdapter(transaction -> {
            Intent intent = new Intent(HistoryActivity.this, ReceiptActivity.class);
            intent.putExtra("transaction_id", transaction.id);
            startActivity(intent);
        });
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTransactions.setAdapter(adapter);

        TransactionViewModel viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        viewModel.getAllTransactions().observe(this, transactions -> {
            adapter.setTransactions(transactions);
        });
    }
}