package com.app.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    // --- Equals & HashCode with EqualsVerifier ---

    @Test
    void equalsAndHashCodeShouldFollowContracts() {
        // Prefab User (break Cart <-> User cycle)
        User userRed = new User();
        userRed.setUserId(1L);
        userRed.setAddresses(new ArrayList<>());
        userRed.setCart(null);

        User userBlack = new User();
        userBlack.setUserId(2L);
        userBlack.setAddresses(new ArrayList<>());
        userBlack.setCart(null);

        // Prefab CartItem (break Cart <-> CartItem cycle)
        CartItem ciRed = new CartItem();
        ciRed.setCartItemId(1L);
        ciRed.setCart(null);
        ciRed.setProduct(null);

        CartItem ciBlack = new CartItem();
        ciBlack.setCartItemId(2L);
        ciBlack.setCart(null);
        ciBlack.setProduct(null);

        EqualsVerifier.forClass(Cart.class)
                .withPrefabValues(User.class, userRed, userBlack)
                .withPrefabValues(CartItem.class, ciRed, ciBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    // --- Lombok getters & setters sanity test ---

    @Test
    void lombokGeneratedGettersAndSettersWork() {
        User user = new User();
        user.setUserId(10L);

        CartItem item1 = new CartItem();
        item1.setCartItemId(101L);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(item1);

        Cart cart = new Cart();
        cart.setCartId(5L);
        cart.setUser(user);
        cart.setCartItems(cartItems);
        cart.setTotalPrice(199.99);

        assertEquals(5L, cart.getCartId());
        assertEquals(user, cart.getUser());
        assertEquals(cartItems, cart.getCartItems());
        assertEquals(199.99, cart.getTotalPrice());
    }

    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNonNull() {
        Cart cart = new Cart();
        cart.setCartId(1L);

        // expected = 59*7 + Objects.hashCode(1L)
        int expected = 59 * 7 + Long.valueOf(1L).hashCode();

        assertEquals(expected, cart.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(cartId)");
    }

    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNull() {
        Cart cart = new Cart();
        cart.setCartId(null);

        // Objects.hashCode(null) == 0
        int expected = 59 * 7 + 0;

        assertEquals(expected, cart.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(null)");
    }



    // --- toString() test ---

    @Test
    void testToString_ShouldContainId() {
        Cart cart = new Cart();
        cart.setCartId(42L);

        String str = cart.toString();

        assertTrue(str.contains("42"), "toString() should contain the cartId");
        assertEquals("id=42", str);
    }

    // --- All-args constructor ---

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setUserId(11L);

        CartItem item = new CartItem();
        item.setCartItemId(99L);

        List<CartItem> items = new ArrayList<>();
        items.add(item);

        Cart cart = new Cart(
                100L,      // cartId
                user,      // user
                items,     // cartItems
                250.75     // totalPrice
        );

        assertEquals(100L, cart.getCartId());
        assertEquals(user, cart.getUser());
        assertEquals(1, cart.getCartItems().size());
        assertEquals(item, cart.getCartItems().get(0));
        assertEquals(250.75, cart.getTotalPrice());
    }
}
