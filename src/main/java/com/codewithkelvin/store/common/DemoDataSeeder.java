package com.codewithkelvin.store.common;

import com.codewithkelvin.store.users.Role;
import com.codewithkelvin.store.users.User;
import com.codewithkelvin.store.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the two logins documented in the README so anyone opening the
 * deployed demo can sign in without registering first.
 * <p>
 * The passwords are hashed here rather than in a migration because the hash has
 * to come from the same {@link PasswordEncoder} the login flow verifies with —
 * a hard-coded hash in SQL silently stops working the day the encoder changes.
 * Disabled by setting {@code SEED_DEMO_USERS=false}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo.seed-users", havingValue = "true", matchIfMissing = false)
public class DemoDataSeeder implements ApplicationRunner {

    private static final String DEMO_EMAIL = "demo@store.dev";
    private static final String DEMO_PASSWORD = "Demo1234!";
    private static final String ADMIN_EMAIL = "admin@store.dev";
    private static final String ADMIN_PASSWORD = "Admin1234!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seed("Demo Shopper", DEMO_EMAIL, DEMO_PASSWORD, Role.USER);
        seed("Demo Admin", ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
    }

    private void seed(String name, String email, String rawPassword, Role role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        var user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();

        userRepository.save(user);
        log.info("Seeded demo account {} with role {}", email, role);
    }
}
