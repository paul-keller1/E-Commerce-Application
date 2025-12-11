package com.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.app.dto.CartDTO;
import com.app.service.CartService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CartContollerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private CartDTO cartDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartDTO = new CartDTO(1L, 0.0, List.of());
    }

    @Test
    void addProductToCart_ShouldReturnCreatedCart() {
        when(cartService.addProductToCart(1L, 2L, 3)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.addProductToCart(1L, 2L, 3);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
    }

    @Test
    void getCarts_ShouldReturnList() {
        when(cartService.getAllCarts()).thenReturn(List.of(cartDTO));

        ResponseEntity<List<CartDTO>> response = cartController.getCarts();

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getCartById_ShouldReturnCart() {
        when(cartService.getCart("email@test.com", 1L)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.getCartById("email@test.com", 1L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
    }

    @Test
    void updateCartProduct_ShouldReturnUpdatedCart() {
        when(cartService.updateProductQuantityInCart(1L, 2L, 4)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.updateCartProduct(1L, 2L, 4);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartDTO, response.getBody());
    }

    @Test
    void deleteProductFromCart_ShouldReturnStatus() {
        when(cartService.deleteProductFromCart(1L, 2L)).thenReturn("removed");

        ResponseEntity<String> response = cartController.deleteProductFromCart(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("removed", response.getBody());
    }
}
