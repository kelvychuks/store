package com.codewithkelvin.store.users;

import com.codewithkelvin.store.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "name cannot be blank")
    @Size(max = 255, min = 2)
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "email must be valid")
    @Lowercase(message = "email must be lowercase")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 4, max = 255)
    private String password;
}
