package com.app.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemDTOTest {

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        CartItemDTO dto = new CartItemDTO();

        CartDTO cart = new CartDTO(1L, 100.0, null);
        ProductDTO product = new ProductDTO(
                1L,
                "Item",
                "img.jpg",
                "Desc",
                2,
                10.0,
                1.0,
                9.0
        );

        dto.setCartItemId(1L);
        dto.setCart(cart);
        dto.setProduct(product);
        dto.setQuantity(3);
        dto.setDiscount(1.5);
        dto.setProductPrice(9.0);

        assertEquals(1L, dto.getCartItemId());
        assertEquals(cart, dto.getCart());
        assertEquals(product, dto.getProduct());
        assertEquals(3, dto.getQuantity());
        assertEquals(1.5, dto.getDiscount());
        assertEquals(9.0, dto.getProductPrice());
    }

    @Test
    void allArgsConstructorShouldSetAllFields() {
        CartDTO cart = new CartDTO(1L, 100.0, null);
        ProductDTO product = new ProductDTO(
                1L,
                "Item",
                "img.jpg",
                "Desc",
                2,
                10.0,
                1.0,
                9.0
        );

        CartItemDTO dto = new CartItemDTO(
                1L,
                cart,
                product,
                3,
                1.5,
                9.0
        );

        assertEquals(1L, dto.getCartItemId());
        assertEquals(cart, dto.getCart());
        assertEquals(product, dto.getProduct());
        assertEquals(3, dto.getQuantity());
        assertEquals(1.5, dto.getDiscount());
        assertEquals(9.0, dto.getProductPrice());
    }

    @Test
    void equalsAndHashCodeShouldWorkForIdenticalObjects() {
        CartDTO cart1 = new CartDTO(1L, 100.0, null);
        ProductDTO product1 = new ProductDTO(
                1L,
                "Item",
                "img.jpg",
                "Desc",
                2,
                10.0,
                1.0,
                9.0
        );

        CartDTO cart2 = new CartDTO(1L, 100.0, null);
        ProductDTO product2 = new ProductDTO(
                1L,
                "Item",
                "img.jpg",
                "Desc",
                2,
                10.0,
                1.0,
                9.0
        );

        CartItemDTO c1 = new CartItemDTO(1L, cart1, product1, 3, 1.5, 9.0);
        CartItemDTO c2 = new CartItemDTO(1L, cart2, product2, 3, 1.5, 9.0);
        CartItemDTO c3 = new CartItemDTO(2L, null, null, 1, 0.0, 0.0);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
    }

    @Test
    void equalsShouldHandleNullAndDifferentType() {
        CartItemDTO dto = new CartItemDTO(1L, null, null, 1, 0.0, 0.0);

        assertEquals(dto, dto);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "string");
    }

    @Test
    void toStringShouldContainClassNameAndFields() {
        CartItemDTO dto = new CartItemDTO(1L, null, null, 1, 0.0, 0.0);

        String s = dto.toString();

        assertNotNull(s);
        assertTrue(s.contains("CartItemDTO"));
        assertTrue(s.contains("1"));
    }
}
