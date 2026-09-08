package com.codewithkelvin.store.carts;

import com.codewithkelvin.store.products.Product;
import com.codewithkelvin.store.products.ProductNotFoundException;
import com.codewithkelvin.store.products.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    private static final UUID CART_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartMapper cartMapper;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private static Product product() {
        return Product.builder()
                .id(5L)
                .name("Cork Yoga Mat")
                .description("Natural cork surface")
                .price(new BigDecimal("74.90"))
                .build();
    }

    @Test
    void addingToAnUnknownCartFailsWithoutTouchingTheCatalogue() {
        when(cartRepository.getCartWithItems(CART_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(CART_ID, 5L))
                .isInstanceOf(CartNotFoundException.class);

        verify(productRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addingAnUnknownProductFailsAndLeavesTheCartUnsaved() {
        when(cartRepository.getCartWithItems(CART_ID)).thenReturn(Optional.of(new Cart()));
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(CART_ID, 404L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(cartRepository, never()).save(any());
    }

    @Test
    void addingAProductPersistsTheCartWithTheNewLine() {
        var cart = new Cart();
        when(cartRepository.getCartWithItems(CART_ID)).thenReturn(Optional.of(cart));
        when(productRepository.findById(5L)).thenReturn(Optional.of(product()));
        when(cartMapper.toDto(any(CartItem.class))).thenReturn(new CartItemDto());

        cartService.addToCart(CART_ID, 5L);

        var saved = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(saved.capture());
        assertThat(saved.getValue().getItems()).hasSize(1);
        assertThat(saved.getValue().getTotalPrice()).isEqualByComparingTo("74.90");
    }

    @Test
    void updatingAProductThatIsNotInTheCartFails() {
        when(cartRepository.getCartWithItems(CART_ID)).thenReturn(Optional.of(new Cart()));

        assertThatThrownBy(() -> cartService.updatItem(CART_ID, 5L, 3))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void updatingQuantityRecalculatesTheLineTotal() {
        var cart = new Cart();
        cart.addItem(product());
        when(cartRepository.getCartWithItems(CART_ID)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(any(CartItem.class))).thenReturn(new CartItemDto());

        cartService.updatItem(CART_ID, 5L, 3);

        assertThat(cart.getItem(5L).getQuantity()).isEqualTo(3);
        assertThat(cart.getTotalPrice()).isEqualByComparingTo("224.70");
        verify(cartRepository).save(cart);
    }

    @Test
    void clearingACartEmptiesItAndSaves() {
        var cart = new Cart();
        cart.addItem(product());
        when(cartRepository.getCartWithItems(CART_ID)).thenReturn(Optional.of(cart));

        cartService.clearCart(CART_ID);

        assertThat(cart.isEmpty()).isTrue();
        verify(cartRepository).save(cart);
    }
}
