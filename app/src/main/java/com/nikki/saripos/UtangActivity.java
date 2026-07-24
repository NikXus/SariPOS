package com.nikki.saripos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nikki.saripos.databinding.ActivityUtangBinding;
import com.nikki.saripos.ui.UtangAdapter;
import com.nikki.saripos.viewmodel.UtangViewModel;

public class UtangActivity extends AppCompatActivity {

    private ActivityUtangBinding binding;
    private UtangViewModel utangViewModel;
    private UtangAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUtangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        utangViewModel = new ViewModelProvider(this).get(UtangViewModel.class);

        adapter = new UtangAdapter(utang -> showPaymentDialog(utang.id, utang.customerName, utang.remainingBalance));
        binding.rvUtang.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUtang.setAdapter(adapter);

        utangViewModel.getOutstandingUtang().observe(this, utangList -> {
            adapter.setUtangList(utangList);
        });

        utangViewModel.getTotalOutstandingBalance().observe(this, total -> {
            binding.tvTotalOutstanding.setText("Total Outstanding: ₱" + total);
        });
    }

    private void showPaymentDialog(int utangId, String customerName, double currentBalance) {
        EditText input = new EditText(this);
        input.setHint("Cash received");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("Record Payment — " + customerName)
                .setMessage("Current balance: ₱" + currentBalance)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String amountStr = input.getText().toString().trim();
                    if (amountStr.isEmpty()) {
                        Toast.makeText(this, "Enter an amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double amount = Double.parseDouble(amountStr);

                    utangViewModel.recordPayment(utangId, amount, (amountApplied, change, newRemainingBalance) -> {
                        String message;
                        if (change > 0) {
                            message = "Debt fully paid. Change: ₱" + String.format("%.2f", change);
                        } else {
                            message = "Payment recorded. Remaining balance: ₱" + String.format("%.2f", newRemainingBalance);
                        }
                        Toast.makeText(UtangActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}