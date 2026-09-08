package com.codewithkelvin.store.orders;

import com.codewithkelvin.store.carts.Cart;
import com.codewithkelvin.store.products.Product;
import com.codewithkelvin.store.users.Role;
import com.codewithkelvin.store.users.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private static User customer(long id) {
        return User.builder()
                .id(id)
                .name("Demo Shopper")
                .email("demo@store.dev")
                .password("hashed")
                .role(Role.USER)
                .build();
    }

    private static Product product(long id, String price) {
        return Product.builder()
                .id(id)
                .name("Product " + id)
                .description("A product")
                .price(new BigDecimal(price))
                .build();
    }

    @Test
    @DisplayName("an order built from a cart copies the lines and freezes the prices")
    void fromCartCopiesLinesAndTotals() {
        var cart = new Cart();
        cart.addItem(product(1L, "25.00"));
        cart.addItem(product(1L, "25.00"));
        cart.addItem(product(2L, "10.00"));

        var order = Order.fromCart(cart, customer(7L));

        assertThat(order.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(order.getCustomer().getId()).isEqualTo(7L);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalPrice()).isEqualByComparingTo("60.00");
    }

    @Test
    @DisplayName("a line item's total is unit price times quantity, held as BigDecimal")
    void lineItemTotalIsUnitPriceTimesQuantity() {
        var order = Order.fromCart(cartWith(product(1L, "19.99"), 3), customer(1L));
        var item = order.getItems().iterator().next();

        assertThat(item.getUnitPrice()).isEqualByComparingTo("19.99");
        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.getTotalPrice()).isEqualByComparingTo("59.97");
    }

    @Test
    void isPlacedByRecognisesTheOwner() {
        var order = Order.fromCart(cartWith(product(1L, "5.00"), 1), customer(42L));

        assertThat(order.isPlacedBy(customer(42L))).isTrue();
        assertThat(order.isPlacedBy(customer(43L))).isFalse();
    }

    private static Cart cartWith(Product product, int quantity) {
        var cart = new Cart();
        for (int i = 0; i < quantity; i++) {
            cart.addItem(product);
        }
        return cart;
    }
}
