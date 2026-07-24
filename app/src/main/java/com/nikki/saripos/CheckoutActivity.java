package com.nikki.saripos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.nikki.saripos.databinding.ActivityCheckoutBinding;
import com.nikki.saripos.model.CartItem;
import com.nikki.saripos.model.Customer;
import com.nikki.saripos.repository.CartManager;
import com.nikki.saripos.repository.TransactionRepository;
import com.nikki.saripos.viewmodel.CustomerViewModel;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private TransactionRepository transactionRepository;
    private CustomerViewModel customerViewModel;
    private double subtotal;

    private List<Customer> customerList = new ArrayList<>();
    private Long selectedDueDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        transactionRepository = new TransactionRepository(getApplication());
        customerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        subtotal = CartManager.getInstance().getSubtotal();
        binding.tvTotal.setText("Total: ₱" + subtotal);

        binding.rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCash) {
                binding.layoutCash.setVisibility(View.VISIBLE);
                binding.layoutUtang.setVisibility(View.GONE);
            } else {
                binding.layoutCash.setVisibility(View.GONE);
                binding.layoutUtang.setVisibility(View.VISIBLE);
            }
        });

        customerViewModel.getAllCustomers().observe(this, customers -> {
            customerList = customers;
            List<String> names = new ArrayList<>();
            for (Customer c : customers) {
                names.add(c.name);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
            binding.spinnerCustomer.setAdapter(adapter);
        });

        binding.btnPickDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar picked = Calendar.getInstance();
                picked.set(year, month, dayOfMonth, 0, 0, 0);
                selectedDueDate = picked.getTimeInMillis();
                binding.tvDueDate.setText("Due: " + (month + 1) + "/" + dayOfMonth + "/" + year);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        binding.etCashReceived.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateChange();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        binding.btnConfirmPayment.setOnClickListener(v -> {
            if (binding.rbUtang.isChecked()) {
                confirmUtangCheckout();
            } else {
                confirmCashCheckout();
            }
        });
    }

    private void confirmCashCheckout() {
        String cashStr = binding.etCashReceived.getText().toString().trim();

        if (cashStr.isEmpty()) {
            Toast.makeText(this, "Enter cash received", Toast.LENGTH_SHORT).show();
            return;
        }

        double cashReceived = Double.parseDouble(cashStr);

        if (cashReceived < subtotal) {
            Toast.makeText(this, "Insufficient cash", Toast.LENGTH_SHORT).show();
            return;
        }

        double change = cashReceived - subtotal;
        List<CartItem> itemsToCheckout = new ArrayList<>(CartManager.getInstance().getCartItems());

        binding.btnConfirmPayment.setEnabled(false);

        transactionRepository.checkout(
                itemsToCheckout,
                subtotal,
                cashReceived,
                change,
                new TransactionRepository.CheckoutCallback() {
                    @Override
                    public void onSuccess(int transactionId) {
                        CartManager.getInstance().clearCart();
                        goToReceipt(transactionId);
                    }

                    @Override
                    public void onInsufficientStock(String productName, int available, int requested) {
                        binding.btnConfirmPayment.setEnabled(true);
                        Toast.makeText(CheckoutActivity.this,
                                "Not enough stock for " + productName + " (available: " + available + ", requested: " + requested + ")",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void confirmUtangCheckout() {
        if (customerList.isEmpty()) {
            Toast.makeText(this, "Add a customer first", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = binding.spinnerCustomer.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= customerList.size()) {
            Toast.makeText(this, "Select a customer", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer selectedCustomer = customerList.get(selectedPosition);
        List<CartItem> itemsToCheckout = new ArrayList<>(CartManager.getInstance().getCartItems());

        binding.btnConfirmPayment.setEnabled(false);

        transactionRepository.checkoutUtang(
                itemsToCheckout,
                subtotal,
                selectedCustomer.id,
                selectedDueDate,
                new TransactionRepository.CheckoutCallback() {
                    @Override
                    public void onSuccess(int transactionId) {
                        CartManager.getInstance().clearCart();
                        goToReceipt(transactionId);
                    }

                    @Override
                    public void onInsufficientStock(String productName, int available, int requested) {
                        binding.btnConfirmPayment.setEnabled(true);
                        Toast.makeText(CheckoutActivity.this,
                                "Not enough stock for " + productName + " (available: " + available + ", requested: " + requested + ")",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void goToReceipt(int transactionId) {
        Intent intent = new Intent(CheckoutActivity.this, ReceiptActivity.class);
        intent.putExtra("transaction_id", transactionId);
        startActivity(intent);
        finish();
    }

    private void calculateChange() {
        String cashStr = binding.etCashReceived.getText().toString().trim();
        if (cashStr.isEmpty()) {
            binding.tvChange.setText("");
            return;
        }
        try {
            double cashReceived = Double.parseDouble(cashStr);
            double change = cashReceived - subtotal;
            binding.tvChange.setText("Change: ₱" + (change >= 0 ? change : 0));
        } catch (NumberFormatException e) {
            binding.tvChange.setText("");
        }
    }
}