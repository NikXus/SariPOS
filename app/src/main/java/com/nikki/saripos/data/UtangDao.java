package com.nikki.saripos.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.nikki.saripos.model.Utang;
import com.nikki.saripos.model.UtangWithCustomer;

@Dao
public interface UtangDao {

    @Insert
    long insert(Utang utang);

    @Update
    void update(Utang utang);

    @Query("SELECT * FROM utang WHERE customer_id = :customerId ORDER BY id DESC")
    LiveData<List<Utang>> getUtangByCustomer(int customerId);

    @Query("SELECT * FROM utang WHERE status != 'PAID' ORDER BY due_date ASC")
    LiveData<List<Utang>> getAllOutstandingUtang();

    @Query("SELECT COALESCE(SUM(remaining_balance), 0) FROM utang WHERE status != 'PAID'")
    LiveData<Double> getTotalOutstandingBalance();

    @Query("SELECT utang.id, utang.customer_id AS customerId, customers.name AS customerName, " +
            "utang.amount, utang.remaining_balance AS remainingBalance, utang.due_date AS dueDate, utang.status " +
            "FROM utang " +
            "INNER JOIN customers ON utang.customer_id = customers.id " +
            "WHERE utang.status != 'PAID' " +
            "ORDER BY utang.due_date ASC")
    LiveData<List<UtangWithCustomer>> getOutstandingUtangWithCustomerNames();

    @Query("SELECT * FROM utang WHERE id = :id")
    Utang getUtangById(int id);
}