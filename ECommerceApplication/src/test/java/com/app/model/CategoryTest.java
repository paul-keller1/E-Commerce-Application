package com.app.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Category validBaseCategory() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");
        category.setProducts(new ArrayList<>());
        return category;
    }


    @Test
    void categoryName_shouldAcceptValidName() {
        Category category = validBaseCategory();
        category.setCategoryName("TV");

        Set<ConstraintViolation<Category>> violations =
                validator.validateProperty(category, "categoryName");

        assertTrue(violations.isEmpty(), "Valid category name should be accepted");
    }

    @Test
    void categoryName_shouldRejectTooShort() {
        Category category = validBaseCategory();
        category.setCategoryName("A");

        Set<ConstraintViolation<Category>> violations =
                validator.validateProperty(category, "categoryName");

        assertFalse(violations.isEmpty(), "Category name shorter than 2 chars should be invalid");
    }

    @Test
    void categoryName_shouldRejectBlank() {
        Category category = validBaseCategory();
        category.setCategoryName("   ");

        Set<ConstraintViolation<Category>> violations =
                validator.validateProperty(category, "categoryName");

        assertFalse(violations.isEmpty(), "Blank category name should be invalid");
    }


    @Test
    void equalsAndHashCodeShouldFollowContracts() {
        Product prodRed = new Product();
        prodRed.setProductId(1L);
        prodRed.setCategory(null);
        prodRed.setOrderItems(new ArrayList<>());
        prodRed.setProducts(new ArrayList<>());

        Product prodBlack = new Product();
        prodBlack.setProductId(2L);
        prodBlack.setCategory(null);
        prodBlack.setOrderItems(new ArrayList<>());
        prodBlack.setProducts(new ArrayList<>());

        EqualsVerifier.forClass(Category.class)
                .withPrefabValues(Product.class, prodRed, prodBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }


    @Test
    void lombokGeneratedGettersAndSettersWork() {
        Category category = new Category();
        category.setCategoryId(5L);
        category.setCategoryName("Books");

        assertEquals(5L, category.getCategoryId());
        assertEquals("Books", category.getCategoryName());
    }

    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNonNull() {
        Category category = new Category();
        category.setCategoryId(1L);

        // expected = 59*7 + Objects.hashCode(1L)
        int expected = 59 * 7 + Long.valueOf(1L).hashCode();

        assertEquals(expected, category.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(categoryId)");
    }

    // prevents operator swapping
    @Test
    void hashCode_shouldMatchFormula_whenIdIsNull() {
        Category category = new Category();
        category.setCategoryId(null);

        // Objects.hashCode(null) == 0
        int expected = 59 * 7 + 0;

        assertEquals(expected, category.hashCode(),
                "hashCode() should follow: 59*7 + Objects.hashCode(null)");
    }



    @Test
    void testToString_ShouldContainId() {
        Category category = new Category();
        category.setCategoryId(42L);

        String str = category.toString();

        assertTrue(str.contains("42"), "toString() should contain the categoryId");
        assertEquals("id=42", str);
    }


    @Test
    void testAllArgsConstructor() {
        Product p1 = new Product();
        p1.setProductId(100L);

        List<Product> products = new ArrayList<>();
        products.add(p1);

        Category category = new Category(
                10L,
                "Sports",
                products
        );

        assertEquals(10L, category.getCategoryId());
        assertEquals("Sports", category.getCategoryName());
        assertEquals(1, category.getProducts().size());
        assertEquals(p1, category.getProducts().get(0));
    }
}
