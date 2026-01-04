package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductDTOTest {

    @Test
    void allArgsConstructorShouldSetFields() {
        Long id = 1L;
        String name = "Product";
        String image = "img.jpg";
        String description = "Test product";
        Integer quantity = 10;
        double price = 100.0;
        double discount = 10.0;
        double specialPrice = 90.0;

        ProductDTO product = new ProductDTO(id, name, image, description, quantity, price, discount, specialPrice);

        assertAll(
                () -> assertEquals(id, product.getProductId()),
                () -> assertEquals(name, product.getName()),
                () -> assertEquals(image, product.getImage()),
                () -> assertEquals(description, product.getDescription()),
                () -> assertEquals(quantity, product.getQuantity()),
                () -> assertEquals(price, product.getPrice()),
                () -> assertEquals(discount, product.getDiscount()),
                () -> assertEquals(specialPrice, product.getSpecialPrice())
        );
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        ProductDTO product = new ProductDTO();

        Long id = 2L;
        String name = "Another Product";
        String image = "image.png";
        String description = "Description";
        Integer quantity = 5;
        double price = 50.0;
        double discount = 5.0;
        double specialPrice = 45.0;

        product.setProductId(id);
        product.setName(name);
        product.setImage(image);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setSpecialPrice(specialPrice);

        assertAll(
                () -> assertEquals(id, product.getProductId()),
                () -> assertEquals(name, product.getName()),
                () -> assertEquals(image, product.getImage()),
                () -> assertEquals(description, product.getDescription()),
                () -> assertEquals(quantity, product.getQuantity()),
                () -> assertEquals(price, product.getPrice()),
                () -> assertEquals(discount, product.getDiscount()),
                () -> assertEquals(specialPrice, product.getSpecialPrice())
        );
    }
}
