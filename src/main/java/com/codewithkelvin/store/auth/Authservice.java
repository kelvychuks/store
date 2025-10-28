package com.codewithkelvin.store.auth;

import com.codewithkelvin.store.users.User;
import com.codewithkelvin.store.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class Authservice {

    private final UserRepository userRepository;

    public User getCurrentUser(){
        var authentication =  SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }
}
