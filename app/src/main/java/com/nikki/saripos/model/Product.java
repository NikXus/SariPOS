package com.nikki.saripos.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String barcode;
    public String name;
    public int categoryId;

    @ColumnInfo(name = "selling_price")
    public double sellingPrice;

    @ColumnInfo(name = "cost_price")
    public double costPrice;

    public int stock;

    @ColumnInfo(name = "minimum_stock")
    public int minimumStock;

    public String image;
}