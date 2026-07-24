package com.nikki.saripos.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nikki.saripos.repository.ProductRepository;
import com.nikki.saripos.repository.TransactionRepository;

public class DashboardViewModel extends AndroidViewModel {

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        transactionRepository = new TransactionRepository(application);
    }

    public LiveData<Double> getTodaySalesTotal() {
        return transactionRepository.getTodaySalesTotal();
    }

    public LiveData<Integer> getTodayTransactionCount() {
        return transactionRepository.getTodayTransactionCount();
    }

    public LiveData<Integer> getLowStockCount() {
        return productRepository.getLowStockCount();
    }
}