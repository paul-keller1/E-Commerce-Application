package com.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.app.dto.UserDTO;
import com.app.dto.UserResponse;
import com.app.service.UserService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserContollerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setEmail("user@test.com");
        userDTO.setRoles(Set.of());
    }

    @Test
    void getUsers_ShouldReturnResponse() {
        UserResponse responseDto = new UserResponse(List.of(userDTO), 0, 5, 1L, 1, true);
        when(userService.getAllUsers(0, 5, "userId", "asc")).thenReturn(responseDto);

        ResponseEntity<UserResponse> response = userController.getUsers(0, 5, "userId", "asc");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void getUser_ShouldReturnUser() {
        when(userService.getUserById(1L)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUser(1L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        when(userService.updateUser(1L, userDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateUser(userDTO, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void deleteUser_ShouldReturnStatus() {
        when(userService.deleteUser(1L)).thenReturn("deleted");

        ResponseEntity<String> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("deleted", response.getBody());
    }
}
