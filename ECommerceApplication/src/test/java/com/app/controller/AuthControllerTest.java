package com.app.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.app.dto.AddressDTO;
import com.app.dto.LoginCredentials;
import com.app.dto.UserCreateDTO;
import com.app.dto.UserDTO;
import com.app.exception.APIException;
import com.app.exception.UserNotFoundException;
import com.app.model.Role;
import com.app.security.JWTService;
import com.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(authController, "adminRegisterSecret", "super-secret");
    }

    @Test
    void registerUser_ShouldCreateUserAndReturnToken() throws UserNotFoundException {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("User");
        createDTO.setLastName("One");
        createDTO.setEmail("user@test.com");
        createDTO.setPassword("plain");
        createDTO.setMobileNumber("1111111111");
        createDTO.setAddress(new AddressDTO());

        UserDTO savedUser = new UserDTO();
        savedUser.setEmail(createDTO.getEmail());
        savedUser.setRoles(Set.of(Role.USER));

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userService.registerUser(createDTO)).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser.getEmail())).thenReturn("jwt-token");

        ResponseEntity<Map<String, Object>> response = authController.registerHandler(createDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().get("token"));
        assertEquals(savedUser, response.getBody().get("user"));
    }

    @Test
    void registerAdmin_WithValidSecret_ShouldCreateAdminUser() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("Admin");
        createDTO.setLastName("User");
        createDTO.setEmail("admin@test.com");
        createDTO.setPassword("plain");
        createDTO.setMobileNumber("9999999999");
        createDTO.setAddress(new AddressDTO());

        UserDTO savedUser = new UserDTO();
        savedUser.setEmail(createDTO.getEmail());
        savedUser.setRoles(Set.of(Role.ADMIN, Role.USER));

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userService.registerAdmin(createDTO)).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser.getEmail())).thenReturn("jwt-token");

        ResponseEntity<Map<String, Object>> response = authController.registerAdminHandler("super-secret", createDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().get("token"));
        assertEquals(savedUser, response.getBody().get("user"));
    }

    @Test
    void registerAdmin_WithInvalidSecret_ShouldThrowApiException() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("admin@test.com");
        createDTO.setPassword("plain");
        createDTO.setAddress(new AddressDTO());

        assertThrows(APIException.class, () -> authController.registerAdminHandler("wrong", createDTO));
    }

    @Test
    void registerAdmin_WithNullAdminSecretField_ShouldThrowApiException() {
        ReflectionTestUtils.setField(authController, "adminRegisterSecret", null);

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("admin@test.com");
        createDTO.setPassword("plain");
        createDTO.setAddress(new AddressDTO());

        assertThrows(APIException.class, () -> authController.registerAdminHandler("super-secret", createDTO));
    }

    @Test
    void registerAdmin_WithBlankAdminSecretField_ShouldThrowApiException() {
        ReflectionTestUtils.setField(authController, "adminRegisterSecret", "   ");

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("admin@test.com");
        createDTO.setPassword("plain");
        createDTO.setAddress(new AddressDTO());

        assertThrows(APIException.class, () -> authController.registerAdminHandler("super-secret", createDTO));
    }

    @Test
    void login_ShouldAuthenticateAndReturnToken() {
        LoginCredentials credentials = new LoginCredentials("user@test.com", "password");

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
        when(jwtService.generateToken(credentials.getEmail())).thenReturn("jwt-token");

        Map<String, Object> response = authController.loginHandler(credentials);

        assertEquals("jwt-token", response.get("jwt-token"));
        assertEquals(1, response.size());
    }
}
