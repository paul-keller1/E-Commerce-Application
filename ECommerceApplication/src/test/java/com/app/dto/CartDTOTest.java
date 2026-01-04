package com.app.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartDTOTest {

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        CartDTO dto = new CartDTO();

        dto.setCartId(1L);
        dto.setTotalPrice(50.0);

        List<ProductDTO> products = new ArrayList<>();
        products.add(new ProductDTO(
                1L,
                "Item",
                "img.jpg",
                "Desc",
                2,
                10.0,
                1.0,
                9.0
        ));
        dto.setProducts(products);

        assertEquals(1L, dto.getCartId());
        assertEquals(50.0, dto.getTotalPrice());
        assertEquals(1, dto.getProducts().size());
    }

    @Test
    void allArgsConstructorShouldSetAllFields() {
        List<ProductDTO> list = List.of(
                new ProductDTO(
                        1L,
                        "Item",
                        "img.jpg",
                        "Desc",
                        2,
                        10.0,
                        1.0,
                        9.0
                )
        );

        CartDTO dto = new CartDTO(1L, 100.0, list);

        assertEquals(1L, dto.getCartId());
        assertEquals(100.0, dto.getTotalPrice());
        assertEquals(list, dto.getProducts());
    }

    @Test
    void equalsAndHashCodeShouldWorkForIdenticalObjects() {
        List<ProductDTO> list1 = List.of(
                new ProductDTO(
                        1L,
                        "Item",
                        "img.jpg",
                        "Desc",
                        2,
                        10.0,
                        1.0,
                        9.0
                )
        );

        List<ProductDTO> list2 = List.of(
                new ProductDTO(
                        1L,
                        "Item",
                        "img.jpg",
                        "Desc",
                        2,
                        10.0,
                        1.0,
                        9.0
                )
        );

        CartDTO c1 = new CartDTO(1L, 100.0, list1);
        CartDTO c2 = new CartDTO(1L, 100.0, list2);
        CartDTO c3 = new CartDTO(2L, 50.0, List.of());

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
    }

    @Test
    void equalsShouldHandleNullAndDifferentType() {
        CartDTO dto = new CartDTO(1L, 100.0, List.of());

        assertEquals(dto, dto);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "string");
    }

    @Test
    void toStringShouldContainClassNameAndFields() {
        CartDTO dto = new CartDTO(1L, 100.0, List.of());

        String s = dto.toString();

        assertNotNull(s);
        assertTrue(s.contains("CartDTO"));
        assertTrue(s.contains("1"));
        assertTrue(s.contains("100.0"));
    }
}
