package com.nikki.saripos.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customers")
public class Customer {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    @ColumnInfo(name = "contact_number")
    public String contactNumber;

    public String address;
}