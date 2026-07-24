package com.nikki.saripos.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.nikki.saripos.model.Customer;
import com.nikki.saripos.repository.CustomerRepository;

public class CustomerViewModel extends AndroidViewModel {

    private final CustomerRepository repository;

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        repository = new CustomerRepository(application);
    }

    public LiveData<List<Customer>> getAllCustomers() {
        return repository.getAllCustomers();
    }

    public void insert(Customer customer) {
        repository.insert(customer);
    }

    public void update(Customer customer) {
        repository.update(customer);
    }

    public void delete(Customer customer) {
        repository.delete(customer);
    }
}