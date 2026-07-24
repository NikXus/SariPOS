package com.nikki.saripos;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nikki.saripos.databinding.ActivityCustomerBinding;
import com.nikki.saripos.model.Customer;
import com.nikki.saripos.ui.CustomerAdapter;
import com.nikki.saripos.viewmodel.CustomerViewModel;

public class CustomerActivity extends AppCompatActivity {

    private ActivityCustomerBinding binding;
    private CustomerViewModel customerViewModel;
    private CustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new CustomerAdapter();
        binding.rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCustomers.setAdapter(adapter);

        customerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        customerViewModel.getAllCustomers().observe(this, customers -> {
            adapter.setCustomers(customers);
        });

        binding.btnAddCustomer.setOnClickListener(v -> {
            String name = binding.etCustomerName.getText().toString().trim();
            String contact = binding.etCustomerContact.getText().toString().trim();
            String address = binding.etCustomerAddress.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Customer name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            Customer customer = new Customer();
            customer.name = name;
            customer.contactNumber = contact.isEmpty() ? null : contact;
            customer.address = address.isEmpty() ? null : address;

            customerViewModel.insert(customer);
            Toast.makeText(this, "Customer added", Toast.LENGTH_SHORT).show();

            binding.etCustomerName.setText("");
            binding.etCustomerContact.setText("");
            binding.etCustomerAddress.setText("");
        });
    }
}