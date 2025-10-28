package com.codewithkelvin.store.carts;

public class CartEmptyEXception extends RuntimeException {
    public CartEmptyEXception(){
        super("cart is empty");
    }
}
