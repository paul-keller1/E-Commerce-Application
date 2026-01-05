package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserCreateDTOTest {

    @Test
    void allArgsConstructorShouldSetFields() {
        AddressDTO address = new AddressDTO(
                1L, "Street", "Building", "City", "State", "Country", "12345"
        );

        Long userId = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String mobile = "1234567890";
        String email = "john@example.com";
        String password = "password";

        UserCreateDTO user = new UserCreateDTO(
                userId, firstName, lastName, mobile, email, password, address
        );

        assertAll(
                () -> assertEquals(userId, user.getUserId()),
                () -> assertEquals(firstName, user.getFirstName()),
                () -> assertEquals(lastName, user.getLastName()),
                () -> assertEquals(mobile, user.getMobileNumber()),
                () -> assertEquals(email, user.getEmail()),
                () -> assertEquals(password, user.getPassword()),
                () -> assertEquals(address, user.getAddress())
        );
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        UserCreateDTO user = new UserCreateDTO();

        AddressDTO address = new AddressDTO(
                2L, "New St", "Tower", "LA", "CA", "USA", "90001"
        );

        Long userId = 2L;
        String firstName = "Jane";
        String lastName = "Smith";
        String mobile = "9876543210";
        String email = "jane@example.com";
        String password = "securepass";

        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMobileNumber(mobile);
        user.setEmail(email);
        user.setPassword(password);
        user.setAddress(address);

        assertAll(
                () -> assertEquals(userId, user.getUserId()),
                () -> assertEquals(firstName, user.getFirstName()),
                () -> assertEquals(lastName, user.getLastName()),
                () -> assertEquals(mobile, user.getMobileNumber()),
                () -> assertEquals(email, user.getEmail()),
                () -> assertEquals(password, user.getPassword()),
                () -> assertEquals(address, user.getAddress())
        );
    }
}
