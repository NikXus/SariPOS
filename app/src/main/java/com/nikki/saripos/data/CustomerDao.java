package com.nikki.saripos.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.nikki.saripos.model.Customer;

@Dao
public interface CustomerDao {

    @Insert
    long insert(Customer customer);

    @Update
    void update(Customer customer);

    @Delete
    void delete(Customer customer);

    @Query("SELECT * FROM customers ORDER BY name ASC")
    LiveData<List<Customer>> getAllCustomers();

    @Query("SELECT * FROM customers WHERE id = :id")
    Customer getCustomerById(int id);
}