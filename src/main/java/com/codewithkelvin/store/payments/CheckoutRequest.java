package com.codewithkelvin.store.payments;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CheckoutRequest {
    @NotNull(message = "cart Id is required")
    private UUID cartId;
}
