package com.app.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Payment validBasePayment() {
        Payment payment = new Payment();
        payment.setPaymentId(1L);
        payment.setPaymentMethod("CARD");
        return payment;
    }


    @Test
    void paymentMethod_shouldAcceptValidValue() {
        Payment payment = validBasePayment();
        payment.setPaymentMethod("CARD");

        Set<ConstraintViolation<Payment>> violations =
                validator.validateProperty(payment, "paymentMethod");

        assertTrue(violations.isEmpty(), "Valid payment method should be accepted");
    }

    @Test
    void paymentMethod_shouldRejectTooShort() {
        Payment payment = validBasePayment();
        payment.setPaymentMethod("Pay");
        Set<ConstraintViolation<Payment>> violations =
                validator.validateProperty(payment, "paymentMethod");

        assertFalse(violations.isEmpty(), "Payment method shorter than 4 chars should be invalid");
    }

    @Test
    void paymentMethod_shouldRejectBlank() {
        Payment payment = validBasePayment();
        payment.setPaymentMethod("   ");

        Set<ConstraintViolation<Payment>> violations =
                validator.validateProperty(payment, "paymentMethod");

        assertFalse(violations.isEmpty(), "Blank payment method should be invalid");
    }


    @Test
    void equalsAndHashCodeShouldFollowContracts() {

        Order orderRed = new Order();
        orderRed.setOrderId(1L);
        orderRed.setPayment(null);

        Order orderBlack = new Order();
        orderBlack.setOrderId(2L);
        orderBlack.setPayment(null);

        EqualsVerifier.forClass(Payment.class)
                .withPrefabValues(Order.class, orderRed, orderBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }


    @Test
    void lombokGeneratedGettersAndSettersWork() {
        Payment payment = new Payment();
        payment.setPaymentId(5L);
        payment.setPaymentMethod("PAYPAL");

        assertEquals(5L, payment.getPaymentId());
        assertEquals("PAYPAL", payment.getPaymentMethod());
    }


    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNonNull() {
        Payment payment = new Payment();
        payment.setPaymentId(1L);

        // expected = 59*7 + Objects.hashCode(1L)
        int expected = 59 * 7 + Long.valueOf(1L).hashCode();

        assertEquals(expected, payment.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(paymentId)");
    }

    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNull() {
        Payment payment = new Payment();
        payment.setPaymentId(null);

        // Objects.hashCode(null) == 0
        int expected = 59 * 7 + 0;

        assertEquals(expected, payment.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(null)");
    }

    @Test
    void testToString_ShouldContainId() {
        Payment payment = new Payment();
        payment.setPaymentId(42L);

        String str = payment.toString();

        assertTrue(str.contains("42"), "toString() should contain the payment ID");
        assertEquals("id=42", str);
    }

    @Test
    void testAllArgsConstructor() {
        Order order = new Order();
        order.setOrderId(100L);

        Payment payment = new Payment(
                10L,
                order,
                "CARD"
        );

        assertEquals(10L, payment.getPaymentId());
        assertEquals(order, payment.getOrder());
        assertEquals("CARD", payment.getPaymentMethod());
    }
}
