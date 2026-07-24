package com.nikki.saripos.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.nikki.saripos.R;
import com.nikki.saripos.model.Customer;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customers = new ArrayList<>();

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.tvName.setText(customer.name);
        holder.tvContact.setText(customer.contactNumber != null ? customer.contactNumber : "No contact");
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvContact;

        CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvContact = itemView.findViewById(R.id.tvCustomerContact);
        }
    }
}