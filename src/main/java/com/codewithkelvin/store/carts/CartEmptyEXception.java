package com.codewithkelvin.store.exceptions;

public class CartEmptyEXception extends RuntimeException {
    public CartEmptyEXception(){
        super("cart is empty");
    }
}
