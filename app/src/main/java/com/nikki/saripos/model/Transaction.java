package com.nikki.saripos.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public long date;

    public double subtotal;
    public double discount;
    public double total;

    @ColumnInfo(name = "cash_received")
    public double cashReceived;

    public double change;

    @ColumnInfo(name = "payment_method")
    public String paymentMethod;

    public String status;
}