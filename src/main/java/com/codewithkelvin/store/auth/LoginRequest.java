package com.codewithkelvin.store.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = " A valid email is required ")
    private String email;

    @NotBlank(message = "password is required")
    private String password;
}
