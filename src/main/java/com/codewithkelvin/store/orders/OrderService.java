package com.codewithkelvin.store.orders;

import com.codewithkelvin.store.auth.Authservice;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final Authservice authservice;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

     public List<OrderDto> getAllOrders() {
         var user = authservice.getCurrentUser();
         var orders = orderRepository.getOrdersByCustomer(user);
         return orders.stream().map(orderMapper::toDto).toList();
     }

     public OrderDto getOrder(Long orderId) {
        var order = orderRepository.getOrderWithItems(orderId).orElseThrow(
                OrderNotFoundException::new);
        var user = authservice.getCurrentUser();
        if(!order.isPlacedBy(user )){
            throw new AccessDeniedException("Access denied");
        }
        return orderMapper.toDto(order);
     }
}
