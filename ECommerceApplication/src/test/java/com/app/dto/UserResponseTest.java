package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.app.model.Role;

class UserResponseTest {

    private UserDTO buildUser(Long id, String firstName, String lastName, String email) {
        AddressDTO address = new AddressDTO(id, "Street " + id, "Building " + id, "City", "State", "Country", "0000" + id);
        CartDTO cart = new CartDTO(id, 0.0, List.of());
        return new UserDTO(id, firstName, lastName, "123456789" + id, email, "pwd" + id, Set.of(Role.USER), address, cart);
    }

    @Test
    void noArgsConstructorShouldCreateEmptyResponse() {
        UserResponse response = new UserResponse();

        assertAll(
                () -> assertNotNull(response),
                () -> assertNull(response.getContent()),
                () -> assertNull(response.getPageNumber()),
                () -> assertNull(response.getPageSize()),
                () -> assertNull(response.getTotalElements()),
                () -> assertNull(response.getTotalPages()),
                () -> assertFalse(response.isLastPage())
        );
    }

    @Test
    void allArgsConstructorShouldSetFields() {
        List<UserDTO> content = Arrays.asList(
                buildUser(1L, "John", "Doe", "john@example.com"),
                buildUser(2L, "Jane", "Smith", "jane@example.com")
        );

        UserResponse response = new UserResponse(content, 1, 10, 2L, 1, true);

        assertAll(
                () -> assertEquals(content, response.getContent()),
                () -> assertEquals(1, response.getPageNumber()),
                () -> assertEquals(10, response.getPageSize()),
                () -> assertEquals(2L, response.getTotalElements()),
                () -> assertEquals(1, response.getTotalPages()),
                () -> assertTrue(response.isLastPage())
        );
    }

    @Test
    void settersShouldUpdateFields() {
        UserResponse response = new UserResponse();
        List<UserDTO> users = List.of(buildUser(3L, "Alice", "Brown", "alice@example.com"));

        response.setContent(users);
        response.setPageNumber(2);
        response.setPageSize(5);
        response.setTotalElements(15L);
        response.setTotalPages(3);
        response.setLastPage(false);

        assertAll(
                () -> assertEquals(users, response.getContent()),
                () -> assertEquals(2, response.getPageNumber()),
                () -> assertEquals(5, response.getPageSize()),
                () -> assertEquals(15L, response.getTotalElements()),
                () -> assertEquals(3, response.getTotalPages()),
                () -> assertFalse(response.isLastPage())
        );
    }

    @Test
    void equalsAndHashCodeShouldDependOnFields() {
        List<UserDTO> content1 = Arrays.asList(
                buildUser(1L, "John", "Doe", "john@example.com"),
                buildUser(2L, "Jane", "Smith", "jane@example.com")
        );

        List<UserDTO> content2 = Arrays.asList(
                buildUser(1L, "John", "Doe", "john@example.com"),
                buildUser(2L, "Jane", "Smith", "jane@example.com")
        );

        UserResponse response1 = new UserResponse(content1, 1, 10, 2L, 1, false);
        UserResponse response2 = new UserResponse(content2, 1, 10, 2L, 1, false);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void toStringShouldIncludeFieldNames() {
        UserResponse response = new UserResponse(
                List.of(buildUser(5L, "Sam", "Blue", "sam@example.com")),
                0,
                1,
                1L,
                1,
                true
        );

        String value = response.toString();

        assertAll(
                () -> assertNotNull(value),
                () -> assertTrue(value.contains("content")),
                () -> assertTrue(value.contains("pageNumber")),
                () -> assertTrue(value.contains("pageSize")),
                () -> assertTrue(value.contains("totalElements")),
                () -> assertTrue(value.contains("totalPages")),
                () -> assertTrue(value.contains("lastPage"))
        );
    }
}
