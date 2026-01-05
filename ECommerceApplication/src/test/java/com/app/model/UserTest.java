package com.app.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;



import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;


import static org.junit.jupiter.api.Assertions.*;


public class UserTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User validBaseUser() {
        User user = new User();
        user.setUserId(1L);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setMobileNumber("1234567890");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        return user;
    }

    @Test
    void firstName_shouldAcceptLettersOnly() {
        User user = validBaseUser();
        user.setFirstName("Alice");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "firstName");

        assertTrue(violations.isEmpty(), "First name with letters only should be valid");
    }

    @Test
    void firstName_shouldRejectDigits() {
        User user = validBaseUser();
        user.setFirstName("Al1ce");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "firstName");

        assertFalse(violations.isEmpty(), "First name with digits should be invalid");
    }

    @Test
    void lastName_shouldRejectDigits() {
        User user = validBaseUser();
        user.setLastName("Sm1th");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "lastName");

        assertFalse(violations.isEmpty(), "Last name with digits should be invalid");
    }

    @Test
    void mobileNumber_shouldAcceptPlainDigits() {
        User user = validBaseUser();
        user.setMobileNumber("1234567890");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "mobileNumber");

        assertTrue(violations.isEmpty(), "10-digit mobile number should be valid");
    }

    @Test
    void mobileNumber_shouldAcceptPlusAndDigits() {
        User user = validBaseUser();
        user.setMobileNumber("+12345678901");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "mobileNumber");

        assertTrue(violations.isEmpty(), "Mobile number with leading + and digits should be valid");
    }

    @Test
    void mobileNumber_shouldRejectLetters() {
        User user = validBaseUser();
        user.setMobileNumber("+12345abcde");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "mobileNumber");

        assertFalse(violations.isEmpty(), "Mobile number with letters should be invalid");
    }

    @Test
    void mobileNumber_shouldRejectTooShort() {
        User user = validBaseUser();
        user.setMobileNumber("12345");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "mobileNumber");

        assertFalse(violations.isEmpty(), "Too-short mobile number should be invalid");
    }

    @Test
    void mobileNumber_shouldRejectTooLong() {
        User user = validBaseUser();
        user.setMobileNumber("1234567890123456");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "mobileNumber");

        assertFalse(violations.isEmpty(), "Too-long mobile number should be invalid");
    }

    @Test
    void equalsAndHashCodeShouldFollowContracts() {
        Address addressRed = new Address();
        addressRed.setAddressId(1L);
        addressRed.setStreet("Main Street");
        addressRed.setBuildingName("Building A");
        addressRed.setCity("Innsbruck");
        addressRed.setState("Tyrol");
        addressRed.setCountry("Austria");
        addressRed.setPincode("6020");
        addressRed.setUsers(new ArrayList<>());

        Address addressBlack = new Address();
        addressBlack.setAddressId(2L);
        addressBlack.setStreet("Second Street");
        addressBlack.setBuildingName("Building B");
        addressBlack.setCity("Vienna");
        addressBlack.setState("Vienna");
        addressBlack.setCountry("Austria");
        addressBlack.setPincode("1010");
        addressBlack.setUsers(new ArrayList<>());

        Cart cartRed = new Cart();
        cartRed.setCartId(1L);
        cartRed.setUser(null);
        cartRed.setCartItems(new ArrayList<>());

        Cart cartBlack = new Cart();
        cartBlack.setCartId(2L);
        cartBlack.setUser(null);
        cartBlack.setCartItems(new ArrayList<>());

        EqualsVerifier.forClass(User.class)
                .withPrefabValues(Address.class, addressRed, addressBlack)
                .withPrefabValues(Cart.class, cartRed, cartBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    void lombokGeneratedGettersAndSettersWork() {
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setEmail("alice@example.com");

        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("alice@example.com", user.getEmail());


    }



    @Test
    void testToString_ShouldContainId() {
        User user = new User();
        user.setUserId(42L);

        String str = user.toString();

        assertTrue(str.contains("42"), "toString() should contain the user ID");
        assertEquals("id=42", str);
    }

    @Test
    void testAllArgsConstructor() {
        Address a1 = new Address();
        Cart c1 = new Cart();

        User user = new User(
                10L,
                "John",
                "Doe",
                "+1234567890",
                "test@example.com",
                "secret",
                Set.of(Role.USER),
                List.of(a1),
                c1
        );

        assertEquals(10L, user.getUserId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("+1234567890", user.getMobileNumber());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertEquals(1, user.getRoles().size());
        assertEquals(1, user.getAddresses().size());
        assertEquals(c1, user.getCart());
    }
}
