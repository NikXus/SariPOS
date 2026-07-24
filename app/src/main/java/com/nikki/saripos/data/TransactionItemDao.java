package com.nikki.saripos.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.nikki.saripos.model.TransactionItem;
import com.nikki.saripos.model.BestSellerResult;

@Dao
public interface TransactionItemDao {

    @Insert
    void insert(TransactionItem item);

    @Insert
    void insertAll(List<TransactionItem> items);

    @Query("SELECT * FROM transaction_items WHERE transaction_id = :transactionId")
    List<TransactionItem> getItemsForTransaction(int transactionId);

    @Query("SELECT productName, SUM(quantity) as totalSold FROM transaction_items " +
            "GROUP BY productName ORDER BY totalSold DESC LIMIT 10")
    LiveData<List<BestSellerResult>> getBestSellingProducts();

    @Query("SELECT COALESCE(SUM(ti.quantity), 0) FROM transaction_items ti " +
            "INNER JOIN transactions t ON ti.transaction_id = t.id " +
            "WHERE ti.product_id = :productId AND t.date >= :sinceDate")
    int getTotalSoldSince(int productId, long sinceDate);
}