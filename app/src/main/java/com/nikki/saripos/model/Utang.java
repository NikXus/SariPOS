package com.nikki.saripos.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "utang")
public class Utang {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "customer_id")
    public int customerId;

    @ColumnInfo(name = "transaction_id")
    public int transactionId;

    public double amount;

    @ColumnInfo(name = "remaining_balance")
    public double remainingBalance;

    @ColumnInfo(name = "due_date")
    public Long dueDate; // nullable — Long (boxed) not long, since due date is optional

    public String status; // e.g. "UNPAID", "PARTIAL", "PAID"
}