package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrderResponseTest {

    @Test
    void allArgsConstructorShouldSetFields() {
        OrderDTO order1 = new OrderDTO(1L, "a@example.com", List.of(), LocalDate.now(), null, 100.0, "PENDING");
        OrderDTO order2 = new OrderDTO(2L, "b@example.com", List.of(), LocalDate.now(), null, 200.0, "CONFIRMED");
        List<OrderDTO> content = List.of(order1, order2);

        Integer pageNumber = 1;
        Integer pageSize = 10;
        Long totalElements = 50L;
        Integer totalPages = 5;
        boolean lastPage = false;

        OrderResponse response = new OrderResponse(content, pageNumber, pageSize, totalElements, totalPages, lastPage);

        assertAll(
                () -> assertEquals(content, response.getContent()),
                () -> assertEquals(pageNumber, response.getPageNumber()),
                () -> assertEquals(pageSize, response.getPageSize()),
                () -> assertEquals(totalElements, response.getTotalElements()),
                () -> assertEquals(totalPages, response.getTotalPages()),
                () -> assertFalse(response.isLastPage())
        );
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        OrderResponse response = new OrderResponse();

        OrderDTO order = new OrderDTO(3L, "c@example.com", List.of(), LocalDate.now(), null, 150.0, "SHIPPED");
        List<OrderDTO> content = List.of(order);

        response.setContent(content);
        response.setPageNumber(2);
        response.setPageSize(20);
        response.setTotalElements(100L);
        response.setTotalPages(10);
        response.setLastPage(true);

        assertAll(
                () -> assertEquals(content, response.getContent()),
                () -> assertEquals(2, response.getPageNumber()),
                () -> assertEquals(20, response.getPageSize()),
                () -> assertEquals(100L, response.getTotalElements()),
                () -> assertEquals(10, response.getTotalPages()),
                () -> assertTrue(response.isLastPage())
        );
    }
}
