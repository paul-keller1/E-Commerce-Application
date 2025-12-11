package com.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.app.exceptions.APIException;
import com.app.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.model.User;
import com.app.repositories.UserRepo;

public class UserDetailsServiceImplTest {


    private AutoCloseable autoCloseable;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setMobileNumber("1234567890");
        user.setRoles(Set.of(Role.USER));
    }

    @AfterEach
    void tearDown() {
        try  {
            autoCloseable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // 1. loadUserByUsername - success
    // ---------------------------------------------------------

    @Test
    void testLoadUserByUsername_Success() {
        String email = user.getEmail();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        // you can also check password mapping if UserInfoConfig exposes it as-is
        assertEquals(user.getPassword(), userDetails.getPassword());

        verify(userRepo, times(1)).findByEmail(email);
    }

    // ---------------------------------------------------------
    // 2. loadUserByUsername - user not found
    // ---------------------------------------------------------

    @Test
    void testLoadUserByUsername_UserNotFound_ThrowsAPIException() {
        String email = user.getEmail();

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );

        String expectedMessage = "User with email " + email + " not found!!!";
        assertEquals(expectedMessage, ex.getMessage());

        verify(userRepo, times(1)).findByEmail(email);
    }
}
