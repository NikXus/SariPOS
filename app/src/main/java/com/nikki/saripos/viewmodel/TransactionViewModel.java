package com.nikki.saripos.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.nikki.saripos.data.AppDatabase;
import com.nikki.saripos.data.TransactionDao;
import com.nikki.saripos.model.Transaction;

public class TransactionViewModel extends AndroidViewModel {

    private final LiveData<List<Transaction>> allTransactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        TransactionDao dao = AppDatabase.getInstance(application).transactionDao();
        allTransactions = dao.getAllTransactions();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }
}