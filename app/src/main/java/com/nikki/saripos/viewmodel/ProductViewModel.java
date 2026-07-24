package com.nikki.saripos.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.nikki.saripos.model.Product;
import com.nikki.saripos.repository.ProductRepository;

public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository repository;
    private final LiveData<List<Product>> allProducts;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
        allProducts = repository.getAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public void insert(Product product) {
        repository.insert(product);
    }

    public void update(Product product) {
        repository.update(product);
    }

    public void delete(Product product) {
        repository.delete(product);
    }

    public Product getProductByBarcode(String barcode) {
        return repository.getProductByBarcode(barcode);
    }

    public void saveOrRestock(Product product, ProductRepository.SaveCallback callback) {
        repository.saveOrRestock(product, callback);
    }
    public void restockProduct(int productId, int addQuantity, ProductRepository.RestockCallback callback) {
        repository.restockProduct(productId, addQuantity, callback);
    }
    public void adjustStock(int productId, int newStockValue, ProductRepository.RestockCallback callback) {
        repository.adjustStock(productId, newStockValue, callback);
    }
}