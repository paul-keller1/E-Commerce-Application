package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PaymentDTOTest {

    @Test
    void allArgsConstructorShouldSetFields() {
        Long id = 1L;
        String method = "CARD";

        PaymentDTO payment = new PaymentDTO(id, method);

        assertAll(
                () -> assertEquals(id, payment.getPaymentId()),
                () -> assertEquals(method, payment.getPaymentMethod())
        );
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        PaymentDTO payment = new PaymentDTO();

        Long id = 2L;
        String method = "UPI";

        payment.setPaymentId(id);
        payment.setPaymentMethod(method);

        assertAll(
                () -> assertEquals(id, payment.getPaymentId()),
                () -> assertEquals(method, payment.getPaymentMethod())
        );
    }
}
