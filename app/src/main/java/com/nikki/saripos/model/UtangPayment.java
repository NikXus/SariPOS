package com.nikki.saripos.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "utang_payments")
public class UtangPayment {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "utang_id")
    public int utangId;

    public double payment;

    @ColumnInfo(name = "payment_date")
    public long paymentDate;
}