package com.nikki.saripos.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.nikki.saripos.model.Transaction;

@Dao
public interface TransactionDao {

    @Insert
    long insert(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getTransactionById(int id);

    @Query("SELECT COALESCE(SUM(total), 0) FROM transactions WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Double> getTodaySalesTotal(long startOfDay, long endOfDay);

    @Query("SELECT COUNT(*) FROM transactions WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Integer> getTodayTransactionCount(long startOfDay, long endOfDay);

    @Query("SELECT COALESCE(SUM(total), 0) FROM transactions WHERE date BETWEEN :start AND :end")
    LiveData<Double> getSalesTotalBetween(long start, long end);

    @Query("SELECT COUNT(*) FROM transactions WHERE date BETWEEN :start AND :end")
    LiveData<Integer> getTransactionCountBetween(long start, long end);
}