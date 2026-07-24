package com.nikki.saripos.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.nikki.saripos.model.UtangWithCustomer;
import com.nikki.saripos.repository.UtangRepository;

public class UtangViewModel extends AndroidViewModel {

    private final UtangRepository repository;

    public UtangViewModel(@NonNull Application application) {
        super(application);
        repository = new UtangRepository(application);
    }

    public LiveData<List<UtangWithCustomer>> getOutstandingUtang() {
        return repository.getOutstandingUtang();
    }

    public LiveData<Double> getTotalOutstandingBalance() {
        return repository.getTotalOutstandingBalance();
    }

    public void recordPayment(int utangId, double paymentAmount, UtangRepository.PaymentCallback callback) {
        repository.recordPayment(utangId, paymentAmount, callback);
    }
}