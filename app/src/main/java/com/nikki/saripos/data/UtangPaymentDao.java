package com.nikki.saripos.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.nikki.saripos.model.UtangPayment;

@Dao
public interface UtangPaymentDao {

    @Insert
    void insert(UtangPayment payment);

    @Query("SELECT * FROM utang_payments WHERE utang_id = :utangId ORDER BY payment_date DESC")
    LiveData<List<UtangPayment>> getPaymentsForUtang(int utangId);
}