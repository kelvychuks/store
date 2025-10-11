package com.codewithkelvin.store.exceptions;

public class CartNotFoundException extends RuntimeException{
    public CartNotFoundException(){
        super("cart not found");
    }
}
