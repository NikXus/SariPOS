package com.nikki.saripos.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nikki.saripos.data.AppDatabase;
import com.nikki.saripos.data.ProductDao;
import com.nikki.saripos.data.InventoryLogDao;
import com.nikki.saripos.model.Product;
import com.nikki.saripos.model.InventoryLog;

public class ProductRepository {

    public interface SaveCallback {
        void onInserted();
        void onRestocked(String productName, int newStock);
    }

    public interface RestockCallback {
        void onRestocked(int newStock);
    }

    private final ProductDao productDao;
    private final InventoryLogDao inventoryLogDao;
    private final LiveData<List<Product>> allProducts;
    private final ExecutorService executorService;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        productDao = db.productDao();
        inventoryLogDao = db.inventoryLogDao();
        allProducts = productDao.getAllProducts();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<Integer> getLowStockCount() {
        return productDao.getLowStockCount();
    }

    public void insert(Product product) {
        executorService.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        executorService.execute(() -> productDao.update(product));
    }

    public void delete(Product product) {
        executorService.execute(() -> productDao.delete(product));
    }

    public Product getProductByBarcode(String barcode) {
        return productDao.getProductByBarcode(barcode);
    }

    /**
     * Inserts a new product, UNLESS a product with the same barcode already
     * exists — in that case, adds the new stock to the existing product's
     * stock instead of creating a duplicate row.
     */
    public void saveOrRestock(Product newProduct, SaveCallback callback) {
        executorService.execute(() -> {
            Product existing = (newProduct.barcode != null && !newProduct.barcode.isEmpty())
                    ? productDao.getProductByBarcode(newProduct.barcode)
                    : null;

            if (existing != null) {
                existing.stock += newProduct.stock;
                productDao.update(existing);
                int updatedStock = existing.stock;
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onRestocked(existing.name, updatedStock));
            } else {
                productDao.insert(newProduct);
                new Handler(Looper.getMainLooper()).post(callback::onInserted);
            }
        });
    }

    /**
     * Adds stock to an existing product (e.g. new delivery arrived).
     * Logs the change as a RESTOCK entry.
     */
    public void restockProduct(int productId, int addQuantity, RestockCallback callback) {
        executorService.execute(() -> {
            Product product = productDao.getProductById(productId);
            if (product == null) return;

            product.stock += addQuantity;
            productDao.update(product);

            InventoryLog log = new InventoryLog(productId, "RESTOCK", addQuantity, System.currentTimeMillis());
            inventoryLogDao.insert(log);

            int newStock = product.stock;
            new Handler(Looper.getMainLooper()).post(() -> callback.onRestocked(newStock));
        });
    }

    /**
     * Directly sets stock to an exact value (manual correction, e.g. after
     * a physical inventory count). Logs the difference as an ADJUSTMENT entry.
     */
    public void adjustStock(int productId, int newStockValue, RestockCallback callback) {
        executorService.execute(() -> {
            Product product = productDao.getProductById(productId);
            if (product == null) return;

            int difference = newStockValue - product.stock;
            product.stock = newStockValue;
            productDao.update(product);

            InventoryLog log = new InventoryLog(productId, "ADJUSTMENT", difference, System.currentTimeMillis());
            inventoryLogDao.insert(log);

            new Handler(Looper.getMainLooper()).post(() -> callback.onRestocked(newStockValue));
        });
    }
}