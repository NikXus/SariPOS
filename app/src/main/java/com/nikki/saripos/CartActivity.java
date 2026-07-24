package com.nikki.saripos;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nikki.saripos.databinding.ActivityCartBinding;
import com.nikki.saripos.repository.CartManager;
import com.nikki.saripos.ui.CartAdapter;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new CartAdapter(CartManager.getInstance().getCartItems(), this::updateSubtotal);
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(adapter);

        updateSubtotal();

        binding.btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCartItems().isEmpty()) {
                android.widget.Toast.makeText(this, "Cart is empty", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new android.content.Intent(this, CheckoutActivity.class));
        });
    }

    private void updateSubtotal() {
        double subtotal = CartManager.getInstance().getSubtotal();
        binding.tvSubtotal.setText("Subtotal: ₱" + subtotal);
    }
}