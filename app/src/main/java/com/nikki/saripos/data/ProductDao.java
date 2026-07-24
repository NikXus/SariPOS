package com.nikki.saripos.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.nikki.saripos.model.Product;

@Dao
public interface ProductDao {

    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products ORDER BY name ASC")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    Product getProductByBarcode(String barcode);

    @Query("SELECT * FROM products WHERE id = :id")
    Product getProductById(int id);

    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :id")
    void reduceStock(int id, int quantity);

    @Query("SELECT COUNT(*) FROM products WHERE stock <= minimum_stock")
    LiveData<Integer> getLowStockCount();

    @Query("SELECT * FROM products")
    List<Product> getAllProductsSync();
}