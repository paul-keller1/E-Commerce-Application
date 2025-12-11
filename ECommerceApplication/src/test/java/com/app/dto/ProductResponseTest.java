package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProductResponseTest {

    @Test
    void allArgsConstructorShouldSetFields() {
        ProductDTO p1 = new ProductDTO(1L, "A", "img1.jpg", "desc1", 5, 100.0, 10.0, 90.0);
        ProductDTO p2 = new ProductDTO(2L, "B", "img2.jpg", "desc2", 3, 200.0, 20.0, 180.0);
        List<ProductDTO> content = List.of(p1, p2);

        Integer pageNumber = 1;
        Integer pageSize = 10;
        Long totalElements = 50L;
        Integer totalPages = 5;
        boolean lastPage = false;

        ProductResponse response = new ProductResponse(content, pageNumber, pageSize, totalElements, totalPages, lastPage);

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
        ProductResponse response = new ProductResponse();

        ProductDTO p = new ProductDTO(3L, "C", "img3.jpg", "desc3", 2, 150.0, 0.0, 150.0);
        List<ProductDTO> content = List.of(p);

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
