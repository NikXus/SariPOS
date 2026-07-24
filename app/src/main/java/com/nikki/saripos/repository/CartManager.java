package com.nikki.saripos.repository;

import java.util.ArrayList;
import java.util.List;

import com.nikki.saripos.model.CartItem;
import com.nikki.saripos.model.Product;

public class CartManager {

    private static CartManager instance;
    private List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Product product) {
        for (CartItem item : cartItems) {
            if (item.product.id == product.id) {
                item.quantity++;
                return;
            }
        }
        cartItems.add(new CartItem(product, 1));
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
    }

    public void increaseQuantity(CartItem item) {
        item.quantity++;
    }

    public void decreaseQuantity(CartItem item) {
        if (item.quantity > 1) {
            item.quantity--;
        } else {
            cartItems.remove(item);
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotal();
        }
        return subtotal;
    }

    public void clearCart() {
        cartItems.clear();
    }
}