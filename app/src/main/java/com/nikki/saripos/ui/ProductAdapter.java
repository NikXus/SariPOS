package com.nikki.saripos.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.nikki.saripos.R;
import com.nikki.saripos.model.Product;
import com.nikki.saripos.repository.CartManager;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnRestockClickListener {
        void onRestockClick(Product product);
    }

    public interface OnAdjustClickListener {
        void onAdjustClick(Product product);
    }

    public interface OnEditClickListener {
        void onEditClick(Product product);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Product product);
    }

    private List<Product> fullProductList = new ArrayList<>();
    private List<Product> displayedProductList = new ArrayList<>();
    private final OnRestockClickListener restockListener;
    private final OnAdjustClickListener adjustListener;
    private final OnEditClickListener editListener;
    private final OnDeleteClickListener deleteListener;

    public ProductAdapter(OnRestockClickListener restockListener,
                          OnAdjustClickListener adjustListener,
                          OnEditClickListener editListener,
                          OnDeleteClickListener deleteListener) {
        this.restockListener = restockListener;
        this.adjustListener = adjustListener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public void setProducts(List<Product> products) {
        this.fullProductList = products;
        this.displayedProductList = new ArrayList<>(products);
        notifyDataSetChanged();
    }

    /**
     * Filters the currently loaded product list by name or barcode (case-insensitive).
     * Call this from a TextWatcher on the search field. Does NOT re-query the database —
     * filters client-side against the list already loaded via setProducts().
     */
    public void filter(String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());

        if (normalizedQuery.isEmpty()) {
            displayedProductList = new ArrayList<>(fullProductList);
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product product : fullProductList) {
                boolean nameMatches = product.name != null &&
                        product.name.toLowerCase(Locale.getDefault()).contains(normalizedQuery);
                boolean barcodeMatches = product.barcode != null &&
                        product.barcode.toLowerCase(Locale.getDefault()).contains(normalizedQuery);
                if (nameMatches || barcodeMatches) {
                    filtered.add(product);
                }
            }
            displayedProductList = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = displayedProductList.get(position);
        holder.tvProductName.setText(product.name);
        holder.tvProductPrice.setText("₱" + product.sellingPrice);
        holder.tvProductStock.setText("Stock: " + product.stock);

        holder.btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addProduct(product);
            android.widget.Toast.makeText(v.getContext(), product.name + " added to cart", android.widget.Toast.LENGTH_SHORT).show();
        });

        holder.btnRestock.setOnClickListener(v -> restockListener.onRestockClick(product));

        holder.itemView.setOnLongClickListener(v -> {
            adjustListener.onAdjustClick(product);
            return true;
        });

        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add(0, 1, 0, "Edit");
            popup.getMenu().add(0, 2, 1, "Delete");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    editListener.onEditClick(product);
                    return true;
                } else if (item.getItemId() == 2) {
                    deleteListener.onDeleteClick(product);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return displayedProductList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductStock;
        Button btnAddToCart, btnRestock, btnMore;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductStock = itemView.findViewById(R.id.tvProductStock);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnRestock = itemView.findViewById(R.id.btnRestock);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}