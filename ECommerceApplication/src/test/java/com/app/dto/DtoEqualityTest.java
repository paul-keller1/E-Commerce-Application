package com.app.dto;

import com.app.model.Role;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DtoEqualityTest {

    private ProductDTO product(long id) {
        return new ProductDTO(id, "Name" + id, "img" + id, "desc" + id, 2, 10.0, 1.0, 9.0);
    }

    private CartItemDTO cartItem(long id) {
        return new CartItemDTO(id, cart(id), product(id), 1, 0.5, 9.5);
    }

    private CartDTO cart(long id) {
        return new CartDTO(id, 25.0, List.of(product(id)));
    }

    private AddressDTO address(long id) {
        return new AddressDTO(id, "Street " + id, "Building " + id, "City", "State", "Country", "12345");
    }

    private PaymentDTO payment(long id) {
        return new PaymentDTO(id, "CARD");
    }

    private OrderItemDTO orderItem(long id) {
        return new OrderItemDTO(id, product(id), 2, 1.5, 19.0);
    }

    private OrderDTO order(long id) {
        return new OrderDTO(id, "user" + id + "@example.com", List.of(orderItem(id)), LocalDate.of(2024, 1, 10),
                payment(id), 50.0, "NEW");
    }

    private UserDTO user(long id) {
        return new UserDTO(id, "First", "Last", "1234567890", "user" + id + "@example.com", "secret",
                Set.of(Role.USER), address(id), cart(id));
    }

    @Test
    void dtoClassesRespectEqualsContracts() {
        Stream.of(
                ProductDTO.class,
                OrderDTO.class,
                CartDTO.class,
                APIResponse.class,
                CategoryDTO.class,
                LoginCredentials.class,
                PaymentDTO.class,
                UserResponse.class,
                AddressDTO.class,
                UserDTO.class,
                UserCreateDTO.class,
                OrderResponse.class,
                CategoryResponse.class,
                CartItemDTO.class,
                OrderItemDTO.class,
                ProductResponse.class
        ).forEach(clazz -> EqualsVerifier.forClass(clazz)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify());
    }

    @Test
    void productDtoEqualityAndCanEqualBranchesAreCovered() {
        ProductDTO dto = product(1);
        ProductDTO same = product(1);
        ProductDTO different = product(2);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("ProductDTO"));

        ProductDTO blocker = new ProductDTO(1L, "Name1", "img1", "desc1", 2, 10.0, 1.0, 9.0) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void orderDtoEqualityAndCanEqualBranchesAreCovered() {
        OrderDTO dto = order(1);
        OrderDTO same = order(1);
        OrderDTO different = order(2);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("OrderDTO"));

        OrderDTO blocker = new OrderDTO(1L, "user1@example.com", dto.getOrderItems(), dto.getOrderDate(), dto.getPayment(),
                dto.getTotalAmount(), dto.getOrderStatus()) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void cartDtoEqualityAndCanEqualBranchesAreCovered() {
        CartDTO dto = cart(1);
        CartDTO same = cart(1);
        CartDTO different = cart(2);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("CartDTO"));

        CartDTO blocker = new CartDTO(1L, 25.0, dto.getProducts()) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void apiResponseEqualityAndCanEqualBranchesAreCovered() {
        APIResponse dto = new APIResponse("ok", true);
        APIResponse same = new APIResponse("ok", true);
        APIResponse different = new APIResponse("no", false);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("APIResponse"));

        APIResponse blocker = new APIResponse("ok", true) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void categoryDtoEqualityAndCanEqualBranchesAreCovered() {
        CategoryDTO dto = new CategoryDTO(1L, "Cat");
        CategoryDTO same = new CategoryDTO(1L, "Cat");
        CategoryDTO different = new CategoryDTO(2L, "Other");

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("CategoryDTO"));

        CategoryDTO blocker = new CategoryDTO(1L, "Cat") {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void loginCredentialsEqualityAndCanEqualBranchesAreCovered() {
        LoginCredentials dto = new LoginCredentials("user@example.com", "pw");
        LoginCredentials same = new LoginCredentials("user@example.com", "pw");
        LoginCredentials different = new LoginCredentials("other@example.com", "pw");

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("LoginCredentials"));

        LoginCredentials blocker = new LoginCredentials("user@example.com", "pw") {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void paymentDtoEqualityAndCanEqualBranchesAreCovered() {
        PaymentDTO dto = payment(1);
        PaymentDTO same = payment(1);
        PaymentDTO different = payment(2);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("PaymentDTO"));

        PaymentDTO blocker = new PaymentDTO(1L, "CARD") {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void userResponseEqualityAndCanEqualBranchesAreCovered() {
        UserResponse dto = new UserResponse(List.of(user(1)), 0, 10, 1L, 1, false);
        UserResponse same = new UserResponse(List.of(user(1)), 0, 10, 1L, 1, false);
        UserResponse different = new UserResponse(List.of(user(2)), 1, 5, 2L, 1, true);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("UserResponse"));

        UserResponse blocker = new UserResponse(List.of(user(1)), 0, 10, 1L, 1, false) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void addressDtoEqualityAndCanEqualBranchesAreCovered() {
        AddressDTO dto = address(1);
        AddressDTO same = address(1);
        AddressDTO different = address(2);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("AddressDTO"));

        AddressDTO blocker = new AddressDTO(1L, "Street 1", "Building 1", "City", "State", "Country", "12345") {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void userDtoEqualityAndCanEqualBranchesAreCovered() {
        UserDTO dto = user(1);
        UserDTO same = user(1);
        UserDTO different = user(2);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("UserDTO"));

        UserDTO blocker = new UserDTO(1L, "First", "Last", "1234567890", "user1@example.com", "secret",
                Set.of(Role.USER), address(1), cart(1)) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void userCreateDtoEqualityAndCanEqualBranchesAreCovered() {
        UserCreateDTO dto = new UserCreateDTO(1L, "First", "Last", "1234567890", "user1@example.com", "pw", address(1));
        UserCreateDTO same = new UserCreateDTO(1L, "First", "Last", "1234567890", "user1@example.com", "pw", address(1));
        UserCreateDTO different = new UserCreateDTO(2L, "First", "Last", "1234567890", "user1@example.com", "pw", address(1));

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("UserCreateDTO"));

        UserCreateDTO blocker = new UserCreateDTO(1L, "First", "Last", "1234567890", "user1@example.com", "pw", address(1)) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void orderResponseEqualityAndCanEqualBranchesAreCovered() {
        OrderResponse dto = new OrderResponse(List.of(order(1)), 0, 10, 1L, 1, false);
        OrderResponse same = new OrderResponse(List.of(order(1)), 0, 10, 1L, 1, false);
        OrderResponse different = new OrderResponse(List.of(order(2)), 1, 5, 2L, 1, true);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("OrderResponse"));

        OrderResponse blocker = new OrderResponse(List.of(order(1)), 0, 10, 1L, 1, false) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void categoryResponseEqualityAndCanEqualBranchesAreCovered() {
        CategoryResponse dto = new CategoryResponse(List.of(new CategoryDTO(1L, "Cat")), 0, 5, 1L, 1, false);
        CategoryResponse same = new CategoryResponse(List.of(new CategoryDTO(1L, "Cat")), 0, 5, 1L, 1, false);
        CategoryResponse different = new CategoryResponse(List.of(new CategoryDTO(2L, "Other")), 1, 5, 2L, 1, true);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("CategoryResponse"));

        CategoryResponse blocker = new CategoryResponse(List.of(new CategoryDTO(1L, "Cat")), 0, 5, 1L, 1, false) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void cartItemDtoEqualityAndCanEqualBranchesAreCovered() {
        CartItemDTO dto = cartItem(1);
        CartItemDTO same = cartItem(1);
        CartItemDTO different = cartItem(2);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("CartItemDTO"));

        CartItemDTO blocker = new CartItemDTO(1L, cart(1), product(1), 1, 0.5, 9.5) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void orderItemDtoEqualityAndCanEqualBranchesAreCovered() {
        OrderItemDTO dto = orderItem(1);
        OrderItemDTO same = orderItem(1);
        OrderItemDTO different = orderItem(2);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("OrderItemDTO"));

        OrderItemDTO blocker = new OrderItemDTO(1L, product(1), 2, 1.5, 19.0) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }

    @Test
    void productResponseEqualityAndCanEqualBranchesAreCovered() {
        ProductResponse dto = new ProductResponse(List.of(product(1)), 0, 10, 1L, 1, false);
        ProductResponse same = new ProductResponse(List.of(product(1)), 0, 10, 1L, 1, false);
        ProductResponse different = new ProductResponse(List.of(product(2)), 1, 5, 2L, 1, true);

        assertEquals(dto, dto);
        assertEquals(dto, same);
        assertEquals(dto.hashCode(), same.hashCode());
        assertNotEquals(dto, different);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "other");
        assertTrue(dto.toString().contains("ProductResponse"));

        ProductResponse blocker = new ProductResponse(List.of(product(1)), 0, 10, 1L, 1, false) {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };
        assertNotEquals(dto, blocker);
    }
}
