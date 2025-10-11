package com.codewithkelvin.store.controllers;

import com.codewithkelvin.store.dtos.CheckoutRequest;
import com.codewithkelvin.store.dtos.CheckoutResponse;
import com.codewithkelvin.store.dtos.ErrorDto;
import com.codewithkelvin.store.exceptions.CartEmptyEXception;
import com.codewithkelvin.store.exceptions.CartNotFoundException;
import com.codewithkelvin.store.exceptions.PaymentException;
import com.codewithkelvin.store.repositories.OrderRepository;
import com.codewithkelvin.store.services.CheckoutService;
import com.codewithkelvin.store.services.WebhookRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;


    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request);
    }

    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload
    ){
        checkoutService.handleWebhookEvent(new WebhookRequest(headers, payload));

    }


    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session"));
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyEXception.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

}
