package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrderDTOTest {

    @Test
    void allArgsConstructorShouldSetFields() {
        Long orderId = 1L;
        String email = "user@example.com";

        ProductDTO product = new ProductDTO(
                10L, "Test Product", "img.jpg", "Test description",
                5, 100.0, 10.0, 90.0
        );

        OrderItemDTO item = new OrderItemDTO(5L, product, 2, 5.0, 95.0);
        PaymentDTO payment = new PaymentDTO(3L, "CARD");
        LocalDate date = LocalDate.now();
        Double total = 200.0;
        String status = "PENDING";

        OrderDTO order = new OrderDTO(orderId, email, List.of(item), date, payment, total, status);

        assertAll(
                () -> assertEquals(orderId, order.getOrderId()),
                () -> assertEquals(email, order.getEmail()),
                () -> assertEquals(1, order.getOrderItems().size()),
                () -> assertEquals(item, order.getOrderItems().get(0)),
                () -> assertEquals(date, order.getOrderDate()),
                () -> assertEquals(payment, order.getPayment()),
                () -> assertEquals(total, order.getTotalAmount()),
                () -> assertEquals(status, order.getOrderStatus())
        );
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        OrderDTO order = new OrderDTO();

        Long orderId = 2L;
        String email = "abc@example.com";

        ProductDTO product = new ProductDTO(
                20L, "Another Product", "image.png", "Description",
                3, 50.0, 0.0, 50.0
        );

        OrderItemDTO item = new OrderItemDTO(6L, product, 1, 0.0, 50.0);
        PaymentDTO payment = new PaymentDTO(4L, "UPI");
        LocalDate date = LocalDate.now();
        Double total = 50.0;
        String status = "CONFIRMED";

        order.setOrderId(orderId);
        order.setEmail(email);
        order.setOrderItems(List.of(item));
        order.setOrderDate(date);
        order.setPayment(payment);
        order.setTotalAmount(total);
        order.setOrderStatus(status);

        assertAll(
                () -> assertEquals(orderId, order.getOrderId()),
                () -> assertEquals(email, order.getEmail()),
                () -> assertEquals(1, order.getOrderItems().size()),
                () -> assertEquals(item, order.getOrderItems().get(0)),
                () -> assertEquals(date, order.getOrderDate()),
                () -> assertEquals(payment, order.getPayment()),
                () -> assertEquals(total, order.getTotalAmount()),
                () -> assertEquals(status, order.getOrderStatus())
        );
    }

    @Test
    void defaultOrderItemsListShouldNotBeNull() {
        OrderDTO order = new OrderDTO();
        assertNotNull(order.getOrderItems());
        assertEquals(0, order.getOrderItems().size());
    }
}
