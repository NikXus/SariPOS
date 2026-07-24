package com.nikki.saripos.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transaction_items")
public class TransactionItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "transaction_id")
    public int transactionId;

    @ColumnInfo(name = "product_id")
    public int productId;

    public String productName;

    public int quantity;
    public double price;
    public double total;
}