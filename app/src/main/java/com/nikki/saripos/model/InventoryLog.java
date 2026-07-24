package com.nikki.saripos.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "inventory_logs")
public class InventoryLog {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int productId;

    @NonNull
    public String type; // "SALE", "RESTOCK", "ADJUSTMENT"

    public int quantity; // negative for deductions (sales), positive for additions (restock)

    public long date; // System.currentTimeMillis()

    public InventoryLog(int productId, @NonNull String type, int quantity, long date) {
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
        this.date = date;
    }
}