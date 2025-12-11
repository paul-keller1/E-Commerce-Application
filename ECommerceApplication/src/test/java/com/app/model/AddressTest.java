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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Address validBaseAddress() {
        Address address = new Address();
        address.setAddressId(1L);
        address.setStreet("Main Street");
        address.setBuildingName("Building A");
        address.setCity("City");
        address.setState("ST");
        address.setCountry("Country");
        address.setPincode("12345");
        address.setUsers(new ArrayList<>());
        return address;
    }


    @Test
    void street_shouldAcceptValidValue() {
        Address address = validBaseAddress();
        address.setStreet("Baker Street");

        Set<ConstraintViolation<Address>> violations =
                validator.validateProperty(address, "street");

        assertTrue(violations.isEmpty(), "Valid street should be accepted");
    }

    @Test
    void street_shouldRejectTooShort() {
        Address address = validBaseAddress();
        address.setStreet("Abc");

        Set<ConstraintViolation<Address>> violations =
                validator.validateProperty(address, "street");

        assertFalse(violations.isEmpty(), "Street shorter than 5 chars should be invalid");
    }

    @Test
    void buildingName_shouldAcceptValidValue() {
        Address address = validBaseAddress();
        address.setBuildingName("House 12");

        Set<ConstraintViolation<Address>> violations =
                validator.validateProperty(address, "buildingName");

        assertTrue(violations.isEmpty(), "Valid buildingName should be accepted");
    }

    @Test
    void city_shouldRejectTooShort() {
        Address address = validBaseAddress();
        address.setCity("Abc");

        Set<ConstraintViolation<Address>> violations =
                validator.validateProperty(address, "city");

        assertFalse(violations.isEmpty(), "City shorter than 4 chars should be invalid");
    }

    @Test
    void state_shouldRejectTooShort() {
        Address address = validBaseAddress();
        address.setState("A");

        Set<ConstraintViolation<Address>> violations =
                validator.validateProperty(address, "state");

        assertFalse(violations.isEmpty(), "State shorter than 2 chars should be invalid");
    }

    @Test
    void country_shouldRejectTooShort() {
        Address address = validBaseAddress();
        address.setCountry("A");

        Set<ConstraintViolation<Address>> violations =
                validator.validateProperty(address, "country");

        assertFalse(violations.isEmpty(), "Country shorter than 2 chars should be invalid");
    }

    @Test
    void pincode_shouldRejectTooShort() {
        Address address = validBaseAddress();
        address.setPincode("1234");

        Set<ConstraintViolation<Address>> violations =
                validator.validateProperty(address, "pincode");

        assertFalse(violations.isEmpty(), "Pincode shorter than 5 chars should be invalid");
    }

    @Test
    void pincode_shouldRejectTooLong() {
        Address address = validBaseAddress();
        address.setPincode("12345678901");

        Set<ConstraintViolation<Address>> violations =
                validator.validateProperty(address, "pincode");

        assertFalse(violations.isEmpty(), "Pincode longer than 10 chars should be invalid");
    }


    @Test
    void equalsAndHashCodeShouldFollowContracts() {
        User userRed = new User();
        userRed.setUserId(1L);
        userRed.setAddresses(new ArrayList<>());

        User userBlack = new User();
        userBlack.setUserId(2L);
        userBlack.setAddresses(new ArrayList<>());

        EqualsVerifier.forClass(Address.class)
                .withPrefabValues(User.class, userRed, userBlack)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }


    @Test
    void lombokGeneratedGettersAndSettersWork() {
        Address address = new Address();
        address.setAddressId(10L);
        address.setStreet("Main Street");
        address.setBuildingName("Building A");
        address.setCity("City");
        address.setState("State");
        address.setCountry("Country");
        address.setPincode("12345");

        assertEquals(10L, address.getAddressId());
        assertEquals("Main Street", address.getStreet());
        assertEquals("Building A", address.getBuildingName());
        assertEquals("City", address.getCity());
        assertEquals("State", address.getState());
        assertEquals("Country", address.getCountry());
        assertEquals("12345", address.getPincode());
    }


    @Test
    void testToString_ShouldContainId() {
        Address address = new Address();
        address.setAddressId(42L);

        String str = address.toString();

        assertTrue(str.contains("42"), "toString() should contain the addressId");
        assertEquals("id=42", str);
    }


    @Test
    void testCustomConstructorWithoutIdAndUsers() {
        Address address = new Address(
                "Austria",
                "Tyrol",
                "Innsbruck",
                "6020",
                "Main St",
                "Building A"
        );

        assertNull(address.getAddressId());
        assertEquals("Austria", address.getCountry());
        assertEquals("Tyrol", address.getState());
        assertEquals("Innsbruck", address.getCity());
        assertEquals("6020", address.getPincode());
        assertEquals("Main St", address.getStreet());
        assertEquals("Building A", address.getBuildingName());
        assertNotNull(address.getUsers());
        assertTrue(address.getUsers().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setUserId(99L);

        Address address = new Address(
                5L,
                "Main Street",
                "Building A",
                "City",
                "State",
                "Country",
                "12345",
                new ArrayList<>(java.util.List.of(user))
        );

        assertEquals(5L, address.getAddressId());
        assertEquals("Main Street", address.getStreet());
        assertEquals("Building A", address.getBuildingName());
        assertEquals("City", address.getCity());
        assertEquals("State", address.getState());
        assertEquals("Country", address.getCountry());
        assertEquals("12345", address.getPincode());
        assertEquals(1, address.getUsers().size());
        assertEquals(user, address.getUsers().get(0));
    }
}
