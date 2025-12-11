package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.app.model.Role;
import org.junit.jupiter.api.Test;

import java.util.Set;

class UserDTOTest {

    @Test
    void allArgsConstructorShouldSetFields() {
        AddressDTO address = new AddressDTO(
                1L, "Street", "Building", "City", "State", "Country", "12345"
        );

        CartDTO cart = new CartDTO(1L, 5.0, null);

        Set<Role> roles = Set.of(Role.ADMIN, Role.USER);

        Long userId = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String mobile = "1234567890";
        String email = "john@example.com";
        String password = "password";

        UserDTO user = new UserDTO(
                userId, firstName, lastName, mobile, email, password, roles, address, cart
        );

        assertAll(
                () -> assertEquals(userId, user.getUserId()),
                () -> assertEquals(firstName, user.getFirstName()),
                () -> assertEquals(lastName, user.getLastName()),
                () -> assertEquals(mobile, user.getMobileNumber()),
                () -> assertEquals(email, user.getEmail()),
                () -> assertEquals(password, user.getPassword()),
                () -> assertEquals(roles, user.getRoles()),
                () -> assertEquals(address, user.getAddress()),
                () -> assertEquals(cart, user.getCart())
        );
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        UserDTO user = new UserDTO();

        AddressDTO address = new AddressDTO(
                2L, "New St", "Block B", "LA", "CA", "USA", "90001"
        );

        CartDTO cart = new CartDTO(2L, 5.0, null);

        Set<Role> roles = Set.of(Role.USER);

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
        user.setRoles(roles);
        user.setAddress(address);
        user.setCart(cart);

        assertAll(
                () -> assertEquals(userId, user.getUserId()),
                () -> assertEquals(firstName, user.getFirstName()),
                () -> assertEquals(lastName, user.getLastName()),
                () -> assertEquals(mobile, user.getMobileNumber()),
                () -> assertEquals(email, user.getEmail()),
                () -> assertEquals(password, user.getPassword()),
                () -> assertEquals(roles, user.getRoles()),
                () -> assertEquals(address, user.getAddress()),
                () -> assertEquals(cart, user.getCart())
        );
    }
}
