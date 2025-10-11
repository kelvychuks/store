package com.codewithkelvin.store.mappers;

import com.codewithkelvin.store.carts.CartDto;
import com.codewithkelvin.store.carts.CartItemDto;
import com.codewithkelvin.store.entities.Cart;
import com.codewithkelvin.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}