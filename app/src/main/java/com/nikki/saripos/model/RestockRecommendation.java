package com.nikki.saripos.model;

public class RestockRecommendation {
    public int productId;
    public String productName;
    public int currentStock;
    public double averageDailySales;
    public double daysUntilStockout; // Double.MAX_VALUE if no sales data
    public int recommendedReorderQty;
    public String priority; // "HIGH", "MEDIUM", "LOW"
}