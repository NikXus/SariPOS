package com.nikki.saripos.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.nikki.saripos.model.InventoryLog;

@Dao
public interface InventoryLogDao {

    @Insert
    void insert(InventoryLog log);

    @Query("SELECT * FROM inventory_logs ORDER BY date DESC")
    LiveData<List<InventoryLog>> getAllLogs();

    @Query("SELECT * FROM inventory_logs WHERE productId = :productId ORDER BY date DESC")
    LiveData<List<InventoryLog>> getLogsForProduct(int productId);

    @Query("SELECT SUM(quantity) FROM inventory_logs WHERE productId = :productId AND type = 'SALE' AND date >= :sinceDate")
    Integer getTotalSoldSince(int productId, long sinceDate);
}