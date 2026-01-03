package com.app.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.model.Role;
import com.app.model.User;
import com.app.repository.UserRepo;
import com.app.security.JWTFilter;
import com.app.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;
import java.util.Set;


@SpringBootTest
class SecurityConfigTest {


    @MockitoBean
    private JWTFilter jwtFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    @Qualifier("filterChain")
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void securityFilterChainBean_ShouldBeCreated() {
        assertNotNull(securityFilterChain);
    }

    @Test
    void passwordEncoderBean_ShouldBeBCrypt() {
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void daoAuthenticationProviderBean_ShouldUseConfiguredServices() {
        assertNotNull(daoAuthenticationProvider);

        Object internalEncoder =
                ReflectionTestUtils.invokeMethod(daoAuthenticationProvider, "getPasswordEncoder");

        assertSame(passwordEncoder, internalEncoder);

    }


    @Test
    void authenticationManagerBean_ShouldBeCreated() {
        assertNotNull(authenticationManager);
    }
}
