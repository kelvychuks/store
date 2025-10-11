package com.codewithkelvin.store.services;

import com.codewithkelvin.store.payments.CheckoutRequest;
import com.codewithkelvin.store.payments.CheckoutResponse;
import com.codewithkelvin.store.entities.Order;
import com.codewithkelvin.store.exceptions.CartEmptyEXception;
import com.codewithkelvin.store.exceptions.CartNotFoundException;
import com.codewithkelvin.store.payments.PaymentException;
import com.codewithkelvin.store.repositories.CartRepository;
import com.codewithkelvin.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final Authservice authservice;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        if (cart.isEmpty()) {
            throw new CartEmptyEXception();
        }
        var order = Order.fromCart(cart, authservice.getCurrentUser());

        orderRepository.save(order);

        try {
            var session = paymentGateway.createCheckoutSession(order);
            cartService.clearCart(cart.getId());
            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        } catch (PaymentException ex) {
            orderRepository.delete(order);
            throw ex;
        }

    }

    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway
                .parseWebhookRequest(request)
                .ifPresent(paymentresult -> {
                    var order = orderRepository.findById(paymentresult.getOrderId()).orElseThrow();
                    order.setStatus(paymentresult.getPaymentStatus());
                    orderRepository.save(order);
                    System.out.println("Received webhook: " + request.getPayload());

                });
    }

}

