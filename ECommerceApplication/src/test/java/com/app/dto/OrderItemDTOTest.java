package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OrderItemDTOTest {

    @Test
    void allArgsConstructorShouldSetFields() {
        ProductDTO product = new ProductDTO(
                10L, "Test Product", "img.jpg", "Description",
                5, 100.0, 10.0, 90.0
        );

        Long orderItemId = 1L;
        Integer quantity = 3;
        double discount = 5.0;
        double orderedProductPrice = 95.0;

        OrderItemDTO item = new OrderItemDTO(orderItemId, product, quantity, discount, orderedProductPrice);

        assertAll(
                () -> assertEquals(orderItemId, item.getOrderItemId()),
                () -> assertEquals(product, item.getProduct()),
                () -> assertEquals(quantity, item.getQuantity()),
                () -> assertEquals(discount, item.getDiscount()),
                () -> assertEquals(orderedProductPrice, item.getOrderedProductPrice())
        );
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        OrderItemDTO item = new OrderItemDTO();

        ProductDTO product = new ProductDTO(
                20L, "Another Product", "image.png", "Desc",
                2, 50.0, 0.0, 50.0
        );

        Long orderItemId = 2L;
        Integer quantity = 1;
        double discount = 0.0;
        double orderedProductPrice = 50.0;

        item.setOrderItemId(orderItemId);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setDiscount(discount);
        item.setOrderedProductPrice(orderedProductPrice);

        assertAll(
                () -> assertEquals(orderItemId, item.getOrderItemId()),
                () -> assertEquals(product, item.getProduct()),
                () -> assertEquals(quantity, item.getQuantity()),
                () -> assertEquals(discount, item.getDiscount()),
                () -> assertEquals(orderedProductPrice, item.getOrderedProductPrice())
        );
    }
}
