package com.nikki.saripos;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nikki.saripos.databinding.ActivityAnalyticsBinding;
import com.nikki.saripos.ui.BestSellerAdapter;
import com.nikki.saripos.viewmodel.AnalyticsViewModel;

public class AnalyticsActivity extends AppCompatActivity {

    private ActivityAnalyticsBinding binding;
    private AnalyticsViewModel analyticsViewModel;
    private BestSellerAdapter bestSellerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        analyticsViewModel = new ViewModelProvider(this).get(AnalyticsViewModel.class);

        bestSellerAdapter = new BestSellerAdapter();
        binding.rvBestSellers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBestSellers.setAdapter(bestSellerAdapter);

        analyticsViewModel.getTodaySalesTotal().observe(this, total ->
                binding.tvTodaySales.setText(String.format("₱%.2f", total)));

        analyticsViewModel.getTodayTransactionCount().observe(this, count ->
                binding.tvTodayTransactions.setText(count + " transactions"));

        analyticsViewModel.getWeekSalesTotal().observe(this, total ->
                binding.tvWeekSales.setText(String.format("₱%.2f", total)));

        analyticsViewModel.getWeekTransactionCount().observe(this, count ->
                binding.tvWeekTransactions.setText(count + " transactions"));

        analyticsViewModel.getMonthSalesTotal().observe(this, total ->
                binding.tvMonthSales.setText(String.format("₱%.2f", total)));

        analyticsViewModel.getMonthTransactionCount().observe(this, count ->
                binding.tvMonthTransactions.setText(count + " transactions"));

        analyticsViewModel.getYearSalesTotal().observe(this, total ->
                binding.tvYearSales.setText(String.format("₱%.2f", total)));

        analyticsViewModel.getBestSellingProducts().observe(this, bestSellers ->
                bestSellerAdapter.setBestSellers(bestSellers));
    }
}