package com.codewithkelvin.store.carts;

public class CartNotFoundException extends RuntimeException{
    public CartNotFoundException(){
        super("cart not found");
    }
}
