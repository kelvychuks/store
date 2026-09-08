package com.codewithkelvin.store;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.DockerClientFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boots the application against a real PostgreSQL started by Testcontainers.
 * <p>
 * Skipped automatically on machines without a Docker daemon, so
 * {@code mvn verify} still passes on a laptop that only has a JDK. CI has
 * Docker, so these always run before anything is merged.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@EnabledIf("dockerIsAvailable")
class StoreApplicationTests {

    static boolean dockerIsAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void migrationsCreateTheExpectedSchema() {
        var tables = jdbcTemplate.queryForList(
                "select table_name from information_schema.tables where table_schema = 'public'",
                String.class);

        assertThat(tables).contains(
                "users", "addresses", "profiles", "categories", "products",
                "wishlist", "carts", "cart_items", "orders", "order_items");
    }

    @Test
    void demoCatalogueIsSeeded() {
        var products = jdbcTemplate.queryForObject("select count(*) from products", Integer.class);
        assertThat(products).isEqualTo(10);
    }

    @Test
    void demoUsersAreSeededWithHashedPasswords() {
        var password = jdbcTemplate.queryForObject(
                "select password from users where email = 'demo@store.dev'", String.class);

        assertThat(password).isNotNull().startsWith("$2");
    }
}
