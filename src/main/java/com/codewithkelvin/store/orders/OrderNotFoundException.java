package com.codewithkelvin.store.orders;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(){
        super("Order not found");
    }
}
