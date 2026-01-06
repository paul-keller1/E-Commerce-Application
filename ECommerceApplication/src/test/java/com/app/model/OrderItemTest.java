package com.app.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    // --- Equals & hashCode with EqualsVerifier ---

    @Test
    void equalsAndHashCodeShouldFollowContracts() {
        Product prodRed = new Product();
        prodRed.setProductId(1L);
        prodRed.setOrderItems(new ArrayList<>());

        Product prodBlack = new Product();
        prodBlack.setProductId(2L);
        prodBlack.setOrderItems(new ArrayList<>());

        Order orderRed = new Order();
        orderRed.setOrderId(1L);
        orderRed.setOrderItems(new ArrayList<>());

        Order orderBlack = new Order();
        orderBlack.setOrderId(2L);
        orderBlack.setOrderItems(new ArrayList<>());

        EqualsVerifier.forClass(OrderItem.class)
                .withPrefabValues(Product.class, prodRed, prodBlack)
                .withPrefabValues(Order.class, orderRed, orderBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }


    @Test
    void lombokGeneratedGettersAndSettersWork() {
        Product product = new Product();
        product.setProductId(10L);

        Order order = new Order();
        order.setOrderId(20L);

        OrderItem item = new OrderItem();
        item.setOrderItemId(5L);
        item.setProduct(product);
        item.setOrder(order);
        item.setQuantity(3);
        item.setDiscount(15.0);
        item.setOrderedProductPrice(100.0);

        assertEquals(5L, item.getOrderItemId());
        assertEquals(product, item.getProduct());
        assertEquals(order, item.getOrder());
        assertEquals(3, item.getQuantity());
        assertEquals(15.0, item.getDiscount());
        assertEquals(100.0, item.getOrderedProductPrice());
    }


    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNonNull() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(1L);

        // expected = 59*7 + Objects.hashCode(1L)
        int expected = 59 * 7 + Long.valueOf(1L).hashCode();

        assertEquals(expected, orderItem.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(orderItemId)");
    }

    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNull() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(null);

        // Objects.hashCode(null) == 0
        int expected = 59 * 7 + 0;

        assertEquals(expected, orderItem.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(null)");
    }



    @Test
    void testToString_ShouldContainId() {
        OrderItem item = new OrderItem();
        item.setOrderItemId(42L);

        String str = item.toString();

        assertTrue(str.contains("42"), "toString() should contain the orderItemId");
        assertEquals("id=42", str);
    }


    @Test
    void testAllArgsConstructor() {
        Product product = new Product();
        product.setProductId(10L);

        Order order = new Order();
        order.setOrderId(20L);

        OrderItem item = new OrderItem(
                1L,
                product,
                order,
                2,
                5.0,
                50.0
        );

        assertEquals(1L, item.getOrderItemId());
        assertEquals(product, item.getProduct());
        assertEquals(order, item.getOrder());
        assertEquals(2, item.getQuantity());
        assertEquals(5.0, item.getDiscount());
        assertEquals(50.0, item.getOrderedProductPrice());
    }
}
