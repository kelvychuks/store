package com.codewithkelvin.store.mappers;

import com.codewithkelvin.store.dtos.OrderDto;
import com.codewithkelvin.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
