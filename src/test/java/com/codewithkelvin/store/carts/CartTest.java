package com.codewithkelvin.store.carts;

import com.codewithkelvin.store.products.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartTest {

    private static Product product(long id, String price) {
        return Product.builder()
                .id(id)
                .name("Product " + id)
                .description("A product")
                .price(new BigDecimal(price))
                .build();
    }

    @Test
    @DisplayName("adding the same product twice increments quantity instead of duplicating the line")
    void addingSameProductTwiceIncrementsQuantity() {
        var cart = new Cart();
        var laptop = product(1L, "999.99");

        cart.addItem(laptop);
        cart.addItem(laptop);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItem(1L).getQuantity()).isEqualTo(2);
    }

    @Test
    void totalPriceSumsEveryLine() {
        var cart = new Cart();
        cart.addItem(product(1L, "10.50"));
        cart.addItem(product(1L, "10.50"));
        cart.addItem(product(2L, "4.00"));

        assertThat(cart.getTotalPrice()).isEqualByComparingTo("25.00");
    }

    @Test
    void emptyCartTotalsZero() {
        assertThat(new Cart().getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(new Cart().isEmpty()).isTrue();
    }

    @Test
    void removingAnItemDetachesItFromTheCart() {
        var cart = new Cart();
        cart.addItem(product(1L, "10.00"));
        cart.addItem(product(2L, "20.00"));

        cart.removeItem(1L);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItem(1L)).isNull();
        assertThat(cart.getTotalPrice()).isEqualByComparingTo("20.00");
    }

    @Test
    void removingAProductThatIsNotInTheCartIsANoOp() {
        var cart = new Cart();
        cart.addItem(product(1L, "10.00"));

        cart.removeItem(99L);

        assertThat(cart.getItems()).hasSize(1);
    }

    @Test
    void clearEmptiesTheCart() {
        var cart = new Cart();
        cart.addItem(product(1L, "10.00"));

        cart.clear();

        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
