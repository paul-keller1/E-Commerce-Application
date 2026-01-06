package com.app.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest<T extends Product>  {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Product validBaseProduct() {
        Product product = new Product();
        product.setProductId(1L);
        product.setName("Phone");
        product.setDescription("Nice phone");
        product.setQuantity(10);
        product.setPrice(100.0);
        product.setDiscount(10.0);
        product.setSpecialPrice(90.0);
        product.setImage("default.png");
        return product;
    }


    @Test
    void name_shouldAcceptValidName() {
        Product product = validBaseProduct();
        product.setName("Phone");

        Set<ConstraintViolation<Product>> violations =
                validator.validateProperty(product, "Name");

        assertTrue(violations.isEmpty(), "Name with >=3 letters should be valid");
    }

    @Test
    void name_shouldRejectTooShort() {
        Product product = validBaseProduct();
        product.setName("AB"); // < 3 chars

        Set<ConstraintViolation<Product>> violations =
                validator.validateProperty(product, "Name");

        assertFalse(violations.isEmpty(), "Name shorter than 3 characters should be invalid");
    }

    @Test
    void name_shouldRejectBlank() {
        Product product = validBaseProduct();
        product.setName("   "); // blank

        Set<ConstraintViolation<Product>> violations =
                validator.validateProperty(product, "Name");

        assertFalse(violations.isEmpty(), "Blank name should be invalid");
    }

    @Test
    void description_shouldAcceptValidDescription() {
        Product product = validBaseProduct();
        product.setDescription("A great phone");

        Set<ConstraintViolation<Product>> violations =
                validator.validateProperty(product, "description");

        assertTrue(violations.isEmpty(), "Description with >=6 chars should be valid");
    }

    @Test
    void description_shouldRejectTooShort() {
        Product product = validBaseProduct();
        product.setDescription("short"); // 5 chars

        Set<ConstraintViolation<Product>> violations =
                validator.validateProperty(product, "description");

        assertFalse(violations.isEmpty(), "Too-short description should be invalid");
    }

    @Test
    void description_shouldRejectBlank() {
        Product product = validBaseProduct();
        product.setDescription("   ");

        Set<ConstraintViolation<Product>> violations =
                validator.validateProperty(product, "description");

        assertFalse(violations.isEmpty(), "Blank description should be invalid");
    }


    @Test
    void equalsAndHashCodeShouldFollowContracts() {
        Category catRed = new Category();
        catRed.setCategoryId(1L);
        catRed.setProducts(new ArrayList<>());

        Category catBlack = new Category();
        catBlack.setCategoryId(2L);
        catBlack.setProducts(new ArrayList<>());

        CartItem ciRed = new CartItem();
        ciRed.setCartItemId(1L);
        ciRed.setProduct(null);

        CartItem ciBlack = new CartItem();
        ciBlack.setCartItemId(2L);
        ciBlack.setProduct(null);

        OrderItem oiRed = new OrderItem();
        oiRed.setOrderItemId(1L);
        oiRed.setProduct(null);

        OrderItem oiBlack = new OrderItem();
        oiBlack.setOrderItemId(2L);
        oiBlack.setProduct(null);

        EqualsVerifier.forClass(Product.class)
                .withPrefabValues(Category.class, catRed, catBlack)
                .withPrefabValues(CartItem.class, ciRed, ciBlack)
                .withPrefabValues(OrderItem.class, oiRed, oiBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }


    @Test
    void lombokGeneratedGettersAndSettersWork() {
        Product product = new Product();
        product.setName("Phone");
        product.setDescription("A decent phone");
        product.setPrice(199.99);

        assertEquals("Phone", product.getName());
        assertEquals("A decent phone", product.getDescription());
        assertEquals(199.99, product.getPrice());
    }



    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNonNull() {
        Product product = new Product();
        product.setProductId(1L);

        // expected = 59*7 + Objects.hashCode(1L)
        int expected = 59 * 7 + Long.valueOf(1L).hashCode();

        assertEquals(expected, product.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(productId)");
    }

    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNull() {
        Product product = new Product();
        product.setProductId(null);

        // Objects.hashCode(null) == 0
        int expected = 59 * 7 + 0;

        assertEquals(expected, product.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(null)");
    }


    @Test
    void testToString_ShouldContainId() {
        Product product = new Product();
        product.setProductId(42L);

        String str = product.toString();

        assertTrue(str.contains("42"), "toString() should contain the product ID");
        assertEquals("id=42", str);
    }

    @Test
    void testAllArgsConstructor() {
        Category category = new Category();
        CartItem cartItem = new CartItem();
        OrderItem orderItem = new OrderItem();

        List<CartItem> cartItems = new ArrayList<>(Collections.singletonList(cartItem));
        List<OrderItem> orderItems = new ArrayList<>(Collections.singletonList(orderItem));

        Product product = new Product(
                10L,
                "Phone",
                "image.png",
                "Good phone",
                5,
                299.99,
                10.0,
                269.99,
                category,
                cartItems,
                orderItems
        );

        assertEquals(10L, product.getProductId());
        assertEquals("Phone", product.getName());
        assertEquals("image.png", product.getImage());
        assertEquals("Good phone", product.getDescription());
        assertEquals(5, product.getQuantity());
        assertEquals(299.99, product.getPrice());
        assertEquals(10.0, product.getDiscount());
        assertEquals(269.99, product.getSpecialPrice());
        assertEquals(category, product.getCategory());
        assertEquals(1, product.getProducts().size());
        assertEquals(1, product.getOrderItems().size());
    }





}
