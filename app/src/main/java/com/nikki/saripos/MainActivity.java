package com.nikki.saripos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nikki.saripos.databinding.ActivityMainBinding;
import com.nikki.saripos.model.Product;
import com.nikki.saripos.repository.ProductRepository;
import com.nikki.saripos.ui.ProductAdapter;
import com.nikki.saripos.viewmodel.ProductViewModel;
import com.nikki.saripos.viewmodel.DashboardViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProductViewModel productViewModel;
    private DashboardViewModel dashboardViewModel;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new ProductAdapter(
                this::showRestockDialog,
                this::showAdjustStockDialog,
                this::showEditDialog,
                this::confirmDelete
        );
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProducts.setAdapter(adapter);
        binding.rvProducts.setNestedScrollingEnabled(false);
        binding.btnViewCustomers.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, CustomerActivity.class));
        });
        binding.btnViewUtang.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, UtangActivity.class));
        });

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        productViewModel.getAllProducts().observe(this, products -> {
            adapter.setProducts(products);
        });

        binding.etSearchProducts.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Dashboard metrics
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        dashboardViewModel.getTodaySalesTotal().observe(this, total -> {
            binding.tvTodaySalesValue.setText(String.format("₱%.2f", total));
        });

        dashboardViewModel.getTodayTransactionCount().observe(this, count -> {
            binding.tvTodayTransactionsValue.setText(String.valueOf(count));
        });

        dashboardViewModel.getLowStockCount().observe(this, count -> {
            binding.tvLowStockValue.setText(String.valueOf(count));
        });

        binding.btnAddProduct.setOnClickListener(v -> {
            String name = binding.etProductName.getText().toString().trim();
            String priceStr = binding.etProductPrice.getText().toString().trim();
            String stockStr = binding.etProductStock.getText().toString().trim();
            String barcode = binding.etProductBarcode.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                return;
            }

            Product product = new Product();
            product.name = name;
            product.sellingPrice = Double.parseDouble(priceStr);
            product.stock = Integer.parseInt(stockStr);
            product.barcode = barcode.isEmpty() ? null : barcode;

            productViewModel.saveOrRestock(product, new ProductRepository.SaveCallback() {
                @Override
                public void onInserted() {
                    Toast.makeText(MainActivity.this, "Product added", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRestocked(String productName, int newStock) {
                    Toast.makeText(MainActivity.this,
                            productName + " restocked — new stock: " + newStock,
                            Toast.LENGTH_SHORT).show();
                }
            });

            binding.etProductName.setText("");
            binding.etProductPrice.setText("");
            binding.etProductStock.setText("");
            binding.etProductBarcode.setText("");
        });

        binding.btnViewCart.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, CartActivity.class));
        });

        binding.btnViewHistory.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, HistoryActivity.class));
        });

        binding.btnScanBarcode.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, ScannerActivity.class));
        });

        binding.btnViewAnalytics.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, AnalyticsActivity.class));
        });

        binding.btnViewRestockSuggestions.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, RestockActivity.class));
        });
    }

    private void showRestockDialog(Product product) {
        EditText input = new EditText(this);
        input.setHint("Quantity to add");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("Restock — " + product.name)
                .setMessage("Current stock: " + product.stock)
                .setView(input)
                .setPositiveButton("Add Stock", (dialog, which) -> {
                    String qtyStr = input.getText().toString().trim();
                    if (qtyStr.isEmpty()) {
                        Toast.makeText(this, "Enter a quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int addQty = Integer.parseInt(qtyStr);
                    if (addQty <= 0) {
                        Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    productViewModel.restockProduct(product.id, addQty, newStock ->
                            Toast.makeText(MainActivity.this,
                                    product.name + " restocked — new stock: " + newStock,
                                    Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAdjustStockDialog(Product product) {
        EditText input = new EditText(this);
        input.setHint("Actual stock count");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("Adjust Stock — " + product.name)
                .setMessage("Current stock: " + product.stock + "\nEnter the correct count after a physical check.")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String valStr = input.getText().toString().trim();
                    if (valStr.isEmpty()) {
                        Toast.makeText(this, "Enter a value", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int newStock = Integer.parseInt(valStr);
                    if (newStock < 0) {
                        Toast.makeText(this, "Stock can't be negative", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    productViewModel.adjustStock(product.id, newStock, updatedStock ->
                            Toast.makeText(MainActivity.this,
                                    product.name + " stock set to " + updatedStock,
                                    Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Product product) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_product, null);

        EditText etName = dialogView.findViewById(R.id.etEditProductName);
        EditText etPrice = dialogView.findViewById(R.id.etEditProductPrice);
        EditText etBarcode = dialogView.findViewById(R.id.etEditProductBarcode);
        EditText etMinStock = dialogView.findViewById(R.id.etEditProductMinStock);

        etName.setText(product.name);
        etPrice.setText(String.valueOf(product.sellingPrice));
        etBarcode.setText(product.barcode != null ? product.barcode : "");
        etMinStock.setText(String.valueOf(product.minimumStock));

        new AlertDialog.Builder(this)
                .setTitle("Edit Product")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String barcode = etBarcode.getText().toString().trim();
                    String minStockStr = etMinStock.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Name and price are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    product.name = name;
                    product.sellingPrice = Double.parseDouble(priceStr);
                    product.barcode = barcode.isEmpty() ? null : barcode;
                    product.minimumStock = minStockStr.isEmpty() ? 0 : Integer.parseInt(minStockStr);

                    productViewModel.update(product);
                    Toast.makeText(this, product.name + " updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Delete \"" + product.name + "\"? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    productViewModel.delete(product);
                    Toast.makeText(this, product.name + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}