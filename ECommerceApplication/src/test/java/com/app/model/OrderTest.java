package com.app.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Order validBaseOrder() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setEmail("customer@example.com");
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(100.0);
        order.setOrderStatus("NEW");
        return order;
    }


    @Test
    void email_shouldAcceptValidEmail() {
        Order order = validBaseOrder();
        order.setEmail("customer@example.com");

        Set<ConstraintViolation<Order>> violations =
                validator.validateProperty(order, "email");

        assertTrue(violations.isEmpty(), "Valid email should be accepted");
    }

    @Test
    void email_shouldRejectInvalidEmail() {
        Order order = validBaseOrder();
        order.setEmail("not-an-email");

        Set<ConstraintViolation<Order>> violations =
                validator.validateProperty(order, "email");

        assertFalse(violations.isEmpty(), "Invalid email should be rejected");
    }

    @Test
    void email_shouldRejectNull() {
        Order order = validBaseOrder();
        order.setEmail(null);

        Set<ConstraintViolation<Order>> violations =
                validator.validate(order);

        assertFalse(violations.isEmpty(), "Null email (nullable = false) should be rejected");
    }


    @Test
    void equalsAndHashCodeShouldFollowContracts() {
        Payment payRed = new Payment();
        payRed.setPaymentId(1L);
        payRed.setOrder(null);

        Payment payBlack = new Payment();
        payBlack.setPaymentId(2L);
        payBlack.setOrder(null);

        OrderItem oiRed = new OrderItem();
        oiRed.setOrderItemId(1L);
        oiRed.setOrder(null);

        OrderItem oiBlack = new OrderItem();
        oiBlack.setOrderItemId(2L);
        oiBlack.setOrder(null);

        EqualsVerifier.forClass(Order.class)
                .withPrefabValues(Payment.class, payRed, payBlack)
                .withPrefabValues(OrderItem.class, oiRed, oiBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }


    @Test
    void lombokGeneratedGettersAndSettersWork() {
        Order order = new Order();
        order.setOrderId(5L);
        order.setEmail("test@example.com");
        order.setOrderDate(LocalDate.of(2024, 1, 1));
        order.setTotalAmount(123.45);
        order.setOrderStatus("PAID");

        assertEquals(5L, order.getOrderId());
        assertEquals("test@example.com", order.getEmail());
        assertEquals(LocalDate.of(2024, 1, 1), order.getOrderDate());
        assertEquals(123.45, order.getTotalAmount());
        assertEquals("PAID", order.getOrderStatus());
    }


    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNonNull() {
        Order order = new Order();
        order.setOrderId(1L);

        // expected = 59*7 + Objects.hashCode(1L)
        int expected = 59 * 7 + Long.valueOf(1L).hashCode();

        assertEquals(expected, order.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(orderId)");
    }

    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNull() {
        Order order = new Order();
        order.setOrderId(null);

        // Objects.hashCode(null) == 0
        int expected = 59 * 7 + 0;

        assertEquals(expected, order.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(null)");
    }

    @Test
    void testToString_ShouldContainId() {
        Order order = new Order();
        order.setOrderId(42L);

        String str = order.toString();

        assertTrue(str.contains("42"), "toString() should contain the order ID");
        assertEquals("id=42", str);
    }

    @Test
    void testAllArgsConstructor() {
        Payment payment = new Payment();
        payment.setPaymentId(10L);

        OrderItem item = new OrderItem();
        item.setOrderItemId(100L);
        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        LocalDate date = LocalDate.of(2024, 2, 2);

        Order order = new Order(
                1L,                // orderId
                "customer@example.com",
                items,
                date,
                payment,
                250.0,
                "NEW"
        );

        assertEquals(1L, order.getOrderId());
        assertEquals("customer@example.com", order.getEmail());
        assertEquals(items, order.getOrderItems());
        assertEquals(date, order.getOrderDate());
        assertEquals(payment, order.getPayment());
        assertEquals(250.0, order.getTotalAmount());
        assertEquals("NEW", order.getOrderStatus());
    }
}
