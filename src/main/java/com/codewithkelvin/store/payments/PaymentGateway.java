package com.codewithkelvin.store.services;

import com.codewithkelvin.store.entities.Order;
import com.codewithkelvin.store.payments.CheckoutSession;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);

    Optional<PaymentResult> parseWebhookRequest(WebhookRequest request);
}