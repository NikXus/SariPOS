package com.nikki.saripos.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.nikki.saripos.model.RestockRecommendation;
import com.nikki.saripos.repository.TransactionRepository;

public class RestockViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;

    public RestockViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
    }

    public LiveData<List<RestockRecommendation>> getRestockRecommendations() {
        return transactionRepository.getRestockRecommendations();
    }
}