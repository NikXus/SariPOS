package com.nikki.saripos.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nikki.saripos.data.AppDatabase;
import com.nikki.saripos.data.UtangDao;
import com.nikki.saripos.data.UtangPaymentDao;
import com.nikki.saripos.model.Utang;
import com.nikki.saripos.model.UtangPayment;
import com.nikki.saripos.model.UtangWithCustomer;

public class UtangRepository {

    public interface PaymentCallback {
        void onSuccess(double amountApplied, double change, double newRemainingBalance);
    }

    private final UtangDao utangDao;
    private final UtangPaymentDao utangPaymentDao;
    private final ExecutorService executorService;

    public UtangRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        utangDao = db.utangDao();
        utangPaymentDao = db.utangPaymentDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<UtangWithCustomer>> getOutstandingUtang() {
        return utangDao.getOutstandingUtangWithCustomerNames();
    }

    public LiveData<Double> getTotalOutstandingBalance() {
        return utangDao.getTotalOutstandingBalance();
    }

    public void recordPayment(int utangId, double cashReceived, PaymentCallback callback) {
        executorService.execute(() -> {
            Utang utang = utangDao.getUtangById(utangId);

            if (utang == null) {
                return; // shouldn't happen, but guard anyway
            }

            double currentBalance = utang.remainingBalance;

            // Never apply more to the debt than what's actually owed
            double amountApplied = Math.min(cashReceived, currentBalance);
            double change = Math.max(0, cashReceived - currentBalance);
            double newRemaining = Math.max(0, currentBalance - cashReceived);

            UtangPayment payment = new UtangPayment();
            payment.utangId = utangId;
            payment.payment = amountApplied; // only the amount that actually paid off debt
            payment.paymentDate = System.currentTimeMillis();
            utangPaymentDao.insert(payment);

            utang.remainingBalance = newRemaining;
            utang.status = (newRemaining <= 0) ? "PAID" : "PARTIAL";
            utangDao.update(utang);

            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onSuccess(amountApplied, change, newRemaining));
        });
    }
}