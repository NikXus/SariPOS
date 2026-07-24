package com.nikki.saripos.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.nikki.saripos.model.BestSellerResult;
import com.nikki.saripos.repository.TransactionRepository;

public class AnalyticsViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;

    public AnalyticsViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
    }

    public LiveData<Double> getTodaySalesTotal() {
        return transactionRepository.getTodaySalesTotal();
    }

    public LiveData<Double> getWeekSalesTotal() {
        return transactionRepository.getWeekSalesTotal();
    }

    public LiveData<Double> getMonthSalesTotal() {
        return transactionRepository.getMonthSalesTotal();
    }

    public LiveData<Double> getYearSalesTotal() {
        return transactionRepository.getYearSalesTotal();
    }

    public LiveData<Integer> getTodayTransactionCount() {
        return transactionRepository.getTodayTransactionCount();
    }

    public LiveData<Integer> getWeekTransactionCount() {
        return transactionRepository.getWeekTransactionCount();
    }

    public LiveData<Integer> getMonthTransactionCount() {
        return transactionRepository.getMonthTransactionCount();
    }

    public LiveData<List<BestSellerResult>> getBestSellingProducts() {
        return transactionRepository.getBestSellingProducts();
    }
}