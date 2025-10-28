package com.codewithkelvin.store.carts;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "quantity must be provided")
    @Min(value = 1, message = "quantity must be greater than zero")
    @Max(value = 1000, message = "quantity cannot be more than 1000")
    private Integer quantity;
}
