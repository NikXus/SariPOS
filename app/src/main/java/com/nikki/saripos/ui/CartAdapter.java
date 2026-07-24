package com.nikki.saripos.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.nikki.saripos.R;
import com.nikki.saripos.model.CartItem;
import com.nikki.saripos.repository.CartManager;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Runnable onCartChanged;

    public CartAdapter(List<CartItem> cartItems, Runnable onCartChanged) {
        this.cartItems = cartItems;
        this.onCartChanged = onCartChanged;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvCartProductName.setText(item.product.name);
        holder.tvQuantity.setText(String.valueOf(item.quantity));

        holder.btnIncrease.setOnClickListener(v -> {
            CartManager.getInstance().increaseQuantity(item);
            notifyDataSetChanged();
            onCartChanged.run();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            CartManager.getInstance().decreaseQuantity(item);
            notifyDataSetChanged();
            onCartChanged.run();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvCartProductName, tvQuantity;
        Button btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCartProductName = itemView.findViewById(R.id.tvCartProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}