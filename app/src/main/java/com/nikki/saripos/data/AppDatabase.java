package com.nikki.saripos.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nikki.saripos.model.Category;
import com.nikki.saripos.model.Customer;
import com.nikki.saripos.model.InventoryLog;
import com.nikki.saripos.model.Product;
import com.nikki.saripos.model.Transaction;
import com.nikki.saripos.model.TransactionItem;
import com.nikki.saripos.model.Utang;
import com.nikki.saripos.model.UtangPayment;

@Database(entities = {Product.class, Category.class, Transaction.class, TransactionItem.class, InventoryLog.class, Customer.class, Utang.class, UtangPayment.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
    public abstract TransactionItemDao transactionItemDao();
    public abstract InventoryLogDao inventoryLogDao();
    public abstract CustomerDao customerDao();
    public abstract UtangDao utangDao();
    public abstract UtangPaymentDao utangPaymentDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "saripos_database"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }
}