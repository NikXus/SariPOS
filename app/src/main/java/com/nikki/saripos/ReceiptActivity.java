package com.nikki.saripos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.nikki.saripos.databinding.ActivityReceiptBinding;
import com.nikki.saripos.model.Transaction;
import com.nikki.saripos.model.TransactionItem;
import com.nikki.saripos.repository.TransactionRepository;
import com.nikki.saripos.ui.ReceiptItemAdapter;

public class ReceiptActivity extends AppCompatActivity {

    private ActivityReceiptBinding binding;
    private TransactionRepository transactionRepository;
    private ReceiptItemAdapter adapter;

    private Transaction currentTransaction;
    private List<TransactionItem> currentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        transactionRepository = new TransactionRepository(getApplication());

        adapter = new ReceiptItemAdapter();
        binding.rvReceiptItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReceiptItems.setAdapter(adapter);

        int transactionId = getIntent().getIntExtra("transaction_id", -1);

        transactionRepository.getReceiptData(transactionId, (transaction, items) -> {
            currentTransaction = transaction;
            currentItems = items;
            displayReceipt(transaction, items);
        });

        binding.btnShareReceipt.setOnClickListener(v -> shareReceipt());
    }

    private void displayReceipt(Transaction transaction, List<TransactionItem> items) {
        if (transaction == null) return;

        binding.tvReceiptTitle.setText("Transaction #" + transaction.id);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        binding.tvReceiptDate.setText(sdf.format(new Date(transaction.date)));

        adapter.setItems(items);

        binding.tvReceiptTotal.setText(String.format(Locale.getDefault(), "Total: ₱%.2f", transaction.total));

        if ("UTANG".equals(transaction.paymentMethod)) {
            binding.tvReceiptPaymentMethod.setText("Payment Method: Utang");
            binding.tvReceiptCash.setText("");
            binding.tvReceiptChange.setText("");
        } else {
            binding.tvReceiptPaymentMethod.setText("Payment Method: Cash");
            binding.tvReceiptCash.setText(String.format(Locale.getDefault(), "Cash: ₱%.2f", transaction.cashReceived));
            binding.tvReceiptChange.setText(String.format(Locale.getDefault(), "Change: ₱%.2f", transaction.change));
        }
    }

    private void shareReceipt() {
        if (currentTransaction == null || currentItems == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Transaction #").append(currentTransaction.id).append("\n");

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        sb.append(sdf.format(new Date(currentTransaction.date))).append("\n\n");

        for (TransactionItem item : currentItems) {
            sb.append(item.productName).append(" x").append(item.quantity)
                    .append(" — ₱").append(item.total).append("\n");
        }

        sb.append("\nTotal: ₱").append(String.format(Locale.getDefault(), "%.2f", currentTransaction.total));

        if ("UTANG".equals(currentTransaction.paymentMethod)) {
            sb.append("\nPayment Method: Utang");
        } else {
            sb.append("\nCash: ₱").append(String.format(Locale.getDefault(), "%.2f", currentTransaction.cashReceived));
            sb.append("\nChange: ₱").append(String.format(Locale.getDefault(), "%.2f", currentTransaction.change));
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(shareIntent, "Share Receipt"));
    }
}