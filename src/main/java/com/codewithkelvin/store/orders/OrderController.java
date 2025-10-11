package com.codewithkelvin.store.controllers;

import com.codewithkelvin.store.dtos.ErrorDto;
import com.codewithkelvin.store.dtos.OrderDto;
import com.codewithkelvin.store.exceptions.OrderNotFoundException;
import com.codewithkelvin.store.mappers.OrderMapper;
import com.codewithkelvin.store.repositories.OrderRepository;
import com.codewithkelvin.store.services.Authservice;
import com.codewithkelvin.store.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrder(@PathVariable("orderId") Long orderId){
        return orderService.getOrder(orderId);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> handleOrderNotFound(){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(){
        return  ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorDto("Access Denied"));
    }

}
