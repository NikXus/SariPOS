package com.nikki.saripos.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nikki.saripos.data.AppDatabase;
import com.nikki.saripos.data.ProductDao;
import com.nikki.saripos.data.TransactionDao;
import com.nikki.saripos.data.TransactionItemDao;
import com.nikki.saripos.data.UtangDao;
import com.nikki.saripos.model.CartItem;
import com.nikki.saripos.model.Product;
import com.nikki.saripos.model.RestockRecommendation;
import com.nikki.saripos.model.Transaction;
import com.nikki.saripos.model.TransactionItem;
import com.nikki.saripos.model.Utang;
import com.nikki.saripos.util.DateUtils;

public class TransactionRepository {

    public interface CheckoutCallback {
        void onSuccess(int transactionId);
        void onInsufficientStock(String productName, int available, int requested);
    }

    public interface ReceiptCallback {
        void onLoaded(Transaction transaction, List<TransactionItem> items);
    }

    private static final int LOOKBACK_DAYS = 30;
    private static final int REORDER_COVERAGE_DAYS = 14;

    private final TransactionDao transactionDao;
    private final TransactionItemDao transactionItemDao;
    private final ProductDao productDao;
    private final UtangDao utangDao;
    private final ExecutorService executorService;

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        transactionDao = db.transactionDao();
        transactionItemDao = db.transactionItemDao();
        productDao = db.productDao();
        utangDao = db.utangDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void checkout(List<CartItem> cartItems, double subtotal, double cashReceived, double change, CheckoutCallback callback) {
        executorService.execute(() -> {
            for (CartItem cartItem : cartItems) {
                Product currentProduct = productDao.getProductById(cartItem.product.id);
                if (currentProduct == null || currentProduct.stock < cartItem.quantity) {
                    int available = currentProduct != null ? currentProduct.stock : 0;
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onInsufficientStock(cartItem.product.name, available, cartItem.quantity));
                    return;
                }
            }

            Transaction transaction = new Transaction();
            transaction.date = System.currentTimeMillis();
            transaction.subtotal = subtotal;
            transaction.discount = 0;
            transaction.total = subtotal;
            transaction.cashReceived = cashReceived;
            transaction.change = change;
            transaction.paymentMethod = "CASH";
            transaction.status = "COMPLETED";

            long transactionId = transactionDao.insert(transaction);

            List<TransactionItem> items = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                TransactionItem item = new TransactionItem();
                item.transactionId = (int) transactionId;
                item.productId = cartItem.product.id;
                item.productName = cartItem.product.name;
                item.quantity = cartItem.quantity;
                item.price = cartItem.product.sellingPrice;
                item.total = cartItem.getTotal();
                items.add(item);

                productDao.reduceStock(cartItem.product.id, cartItem.quantity);
            }

            transactionItemDao.insertAll(items);

            int finalTransactionId = (int) transactionId;
            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(finalTransactionId));
        });
    }

    public void checkoutUtang(List<CartItem> cartItems, double subtotal, int customerId, Long dueDate, CheckoutCallback callback) {
        executorService.execute(() -> {
            for (CartItem cartItem : cartItems) {
                Product currentProduct = productDao.getProductById(cartItem.product.id);
                if (currentProduct == null || currentProduct.stock < cartItem.quantity) {
                    int available = currentProduct != null ? currentProduct.stock : 0;
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onInsufficientStock(cartItem.product.name, available, cartItem.quantity));
                    return;
                }
            }

            Transaction transaction = new Transaction();
            transaction.date = System.currentTimeMillis();
            transaction.subtotal = subtotal;
            transaction.discount = 0;
            transaction.total = subtotal;
            transaction.cashReceived = 0;
            transaction.change = 0;
            transaction.paymentMethod = "UTANG";
            transaction.status = "COMPLETED";

            long transactionId = transactionDao.insert(transaction);

            List<TransactionItem> items = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                TransactionItem item = new TransactionItem();
                item.transactionId = (int) transactionId;
                item.productId = cartItem.product.id;
                item.productName = cartItem.product.name;
                item.quantity = cartItem.quantity;
                item.price = cartItem.product.sellingPrice;
                item.total = cartItem.getTotal();
                items.add(item);

                productDao.reduceStock(cartItem.product.id, cartItem.quantity);
            }

            transactionItemDao.insertAll(items);

            Utang utang = new Utang();
            utang.customerId = customerId;
            utang.transactionId = (int) transactionId;
            utang.amount = subtotal;
            utang.remainingBalance = subtotal;
            utang.dueDate = dueDate;
            utang.status = "UNPAID";

            utangDao.insert(utang);

            int finalTransactionId = (int) transactionId;
            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(finalTransactionId));
        });
    }

    public void getReceiptData(int transactionId, ReceiptCallback callback) {
        executorService.execute(() -> {
            Transaction transaction = transactionDao.getTransactionById(transactionId);
            List<TransactionItem> items = transactionItemDao.getItemsForTransaction(transactionId);
            new Handler(Looper.getMainLooper()).post(() -> callback.onLoaded(transaction, items));
        });
    }

    public LiveData<Double> getTodaySalesTotal() {
        return transactionDao.getTodaySalesTotal(DateUtils.getStartOfToday(), DateUtils.getEndOfToday());
    }

    public LiveData<Integer> getTodayTransactionCount() {
        return transactionDao.getTodayTransactionCount(DateUtils.getStartOfToday(), DateUtils.getEndOfToday());
    }

    public LiveData<List<com.nikki.saripos.model.BestSellerResult>> getBestSellingProducts() {
        return transactionItemDao.getBestSellingProducts();
    }

    public LiveData<Double> getWeekSalesTotal() {
        return transactionDao.getSalesTotalBetween(DateUtils.getStartOfWeek(), DateUtils.getEndOfToday());
    }

    public LiveData<Double> getMonthSalesTotal() {
        return transactionDao.getSalesTotalBetween(DateUtils.getStartOfMonth(), DateUtils.getEndOfToday());
    }

    public LiveData<Double> getYearSalesTotal() {
        return transactionDao.getSalesTotalBetween(DateUtils.getStartOfYear(), DateUtils.getEndOfToday());
    }

    public LiveData<Integer> getWeekTransactionCount() {
        return transactionDao.getTransactionCountBetween(DateUtils.getStartOfWeek(), DateUtils.getEndOfToday());
    }

    public LiveData<Integer> getMonthTransactionCount() {
        return transactionDao.getTransactionCountBetween(DateUtils.getStartOfMonth(), DateUtils.getEndOfToday());
    }

    public LiveData<List<RestockRecommendation>> getRestockRecommendations() {
        MutableLiveData<List<RestockRecommendation>> result = new MutableLiveData<>();

        executorService.execute(() -> {
            long sinceDate = System.currentTimeMillis() - (LOOKBACK_DAYS * 24L * 60 * 60 * 1000);
            List<Product> products = productDao.getAllProductsSync();
            List<RestockRecommendation> recommendations = new ArrayList<>();

            for (Product product : products) {
                int totalSold = transactionItemDao.getTotalSoldSince(product.id, sinceDate);
                double avgDailySales = totalSold / (double) LOOKBACK_DAYS;

                RestockRecommendation rec = new RestockRecommendation();
                rec.productId = product.id;
                rec.productName = product.name;
                rec.currentStock = product.stock;
                rec.averageDailySales = avgDailySales;

                if (avgDailySales <= 0) {
                    rec.daysUntilStockout = Double.MAX_VALUE;
                    rec.recommendedReorderQty = 0;
                    rec.priority = "LOW";
                } else {
                    rec.daysUntilStockout = product.stock / avgDailySales;

                    int targetStock = (int) Math.ceil(avgDailySales * REORDER_COVERAGE_DAYS);
                    rec.recommendedReorderQty = Math.max(0, targetStock - product.stock);

                    if (rec.daysUntilStockout <= 3) {
                        rec.priority = "HIGH";
                    } else if (rec.daysUntilStockout <= 7) {
                        rec.priority = "MEDIUM";
                    } else {
                        rec.priority = "LOW";
                    }
                }

                recommendations.add(rec);
            }

            List<RestockRecommendation> filtered = new ArrayList<>();
            for (RestockRecommendation rec : recommendations) {
                if (rec.averageDailySales > 0) {
                    filtered.add(rec);
                }
            }

            filtered.sort((a, b) -> Double.compare(a.daysUntilStockout, b.daysUntilStockout));

            result.postValue(filtered);
        });

        return result;
    }
}