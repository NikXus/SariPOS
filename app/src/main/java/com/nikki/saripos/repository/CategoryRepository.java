package com.nikki.saripos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nikki.saripos.data.AppDatabase;
import com.nikki.saripos.data.CategoryDao;
import com.nikki.saripos.model.Category;

public class CategoryRepository {

    private final CategoryDao categoryDao;
    private final LiveData<List<Category>> allCategories;
    private final ExecutorService executorService;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
        allCategories = categoryDao.getAllCategories();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Category category) {
        executorService.execute(() -> categoryDao.insert(category));
    }

    public void delete(Category category) {
        executorService.execute(() -> categoryDao.delete(category));
    }
}