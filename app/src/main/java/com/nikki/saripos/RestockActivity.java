package com.nikki.saripos;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nikki.saripos.databinding.ActivityRestockBinding;
import com.nikki.saripos.ui.RestockAdapter;
import com.nikki.saripos.viewmodel.RestockViewModel;

public class RestockActivity extends AppCompatActivity {

    private ActivityRestockBinding binding;
    private RestockViewModel restockViewModel;
    private RestockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        restockViewModel = new ViewModelProvider(this).get(RestockViewModel.class);

        adapter = new RestockAdapter();
        binding.rvRestockSuggestions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRestockSuggestions.setAdapter(adapter);

        restockViewModel.getRestockRecommendations().observe(this, recommendations -> {
            if (recommendations == null || recommendations.isEmpty()) {
                binding.tvNoData.setVisibility(View.VISIBLE);
            } else {
                binding.tvNoData.setVisibility(View.GONE);
            }
            adapter.setRecommendations(recommendations);
        });
    }
}