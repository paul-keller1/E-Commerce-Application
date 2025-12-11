package com.app.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {


    @Test
    void equalsAndHashCodeShouldFollowContracts() {
        Cart cartRed = new Cart();
        cartRed.setCartId(1L);
        cartRed.setCartItems(new ArrayList<>());
        cartRed.setUser(null);

        Cart cartBlack = new Cart();
        cartBlack.setCartId(2L);
        cartBlack.setCartItems(new ArrayList<>());
        cartBlack.setUser(null);

        Product prodRed = new Product();
        prodRed.setProductId(1L);
        prodRed.setCategory(null);
        prodRed.setOrderItems(new ArrayList<>());
        prodRed.setProducts(new ArrayList<>());

        Product prodBlack = new Product();
        prodBlack.setProductId(2L);
        prodBlack.setCategory(null);
        prodBlack.setOrderItems(new ArrayList<>());
        prodBlack.setProducts(new ArrayList<>());

        EqualsVerifier.forClass(CartItem.class)
                .withPrefabValues(Cart.class, cartRed, cartBlack)
                .withPrefabValues(Product.class, prodRed, prodBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }


    @Test
    void lombokGeneratedGettersAndSettersWork() {
        Cart cart = new Cart();
        cart.setCartId(10L);

        Product product = new Product();
        product.setProductId(20L);

        CartItem item = new CartItem();
        item.setCartItemId(5L);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(3);
        item.setDiscount(15.0);
        item.setProductPrice(99.99);

        assertEquals(5L, item.getCartItemId());
        assertEquals(cart, item.getCart());
        assertEquals(product, item.getProduct());
        assertEquals(3, item.getQuantity());
        assertEquals(15.0, item.getDiscount());
        assertEquals(99.99, item.getProductPrice());
    }

    @Test
    void testToString_ShouldContainId() {
        CartItem item = new CartItem();
        item.setCartItemId(42L);

        String str = item.toString();

        assertTrue(str.contains("42"), "toString() should contain the cartItemId");
        assertEquals("id=42", str);
    }


    @Test
    void testAllArgsConstructor() {
        Cart cart = new Cart();
        cart.setCartId(1L);

        Product product = new Product();
        product.setProductId(2L);

        CartItem item = new CartItem(
                100L,
                cart,
                product,
                4,
                5.0,
                120.0
        );

        assertEquals(100L, item.getCartItemId());
        assertEquals(cart, item.getCart());
        assertEquals(product, item.getProduct());
        assertEquals(4, item.getQuantity());
        assertEquals(5.0, item.getDiscount());
        assertEquals(120.0, item.getProductPrice());
    }
}
