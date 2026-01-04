package com.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.app.dto.ProductDTO;
import com.app.dto.ProductResponse;
import com.app.model.Product;
import com.app.service.ProductService;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDTO = new ProductDTO(1L, "Phone", "img.jpg", "Nice phone", 10, 1000.0, 0.0, 1000.0);
    }

    @Test
    void addProduct_ShouldReturnCreatedProduct() {
        when(productService.addProduct(eq(2L), any(ProductDTO.class))).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.addProduct(productDTO, 2L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(productDTO, response.getBody());
    }

    @Test
    void getAllProducts_ShouldReturnResponse() {
        ProductResponse productResponse = new ProductResponse(List.of(productDTO), 0, 5, 1L, 1, true);
        when(productService.getAllProductsResponse(0, 5, "productId", "asc")).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getAllProducts(0, 5, "productId", "asc");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
    }

    @Test
    void getProductsByCategory_ShouldReturnResponse() {
        ProductResponse productResponse = new ProductResponse(List.of(productDTO), 0, 5, 1L, 1, true);
        when(productService.searchByCategory(3L, 0, 5, "productId", "asc")).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getProductsByCategory(3L, 0, 5, "productId", "asc");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
    }

    @Test
    void getProductsByKeyword_ShouldReturnResponse() {
        ProductResponse productResponse = new ProductResponse(List.of(productDTO), 0, 5, 1L, 1, true);
        when(productService.searchProductByKeyword("phone", 0, 5, "productId", "asc")).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getProductsByKeyword("phone", 0, 5, "productId", "asc");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.updateProduct(new Product(), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDTO, response.getBody());
    }

    @Test
    void updateProductImage_ShouldReturnUpdatedProduct() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(productService.updateProductImage(eq(1L), any())).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.updateProductImage(1L, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDTO, response.getBody());
    }

    @Test
    void deleteProduct_ShouldReturnStatus() {
        when(productService.deleteProduct(1L)).thenReturn("deleted");

        ResponseEntity<String> response = productController.deleteProductByCategory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("deleted", response.getBody());
    }
}
