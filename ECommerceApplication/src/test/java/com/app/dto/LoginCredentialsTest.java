package com.app.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LoginCredentialsTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void allArgsConstructorShouldSetFields() {
        String email = "user@example.com";
        String password = "secret";

        LoginCredentials credentials = new LoginCredentials(email, password);

        assertAll(
                () -> assertEquals(email, credentials.getEmail()),
                () -> assertEquals(password, credentials.getPassword())
        );
    }

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        String email = "another@example.com";
        String password = "password123";

        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail(email);
        credentials.setPassword(password);

        assertAll(
                () -> assertEquals(email, credentials.getEmail()),
                () -> assertEquals(password, credentials.getPassword())
        );
    }

    @Test
    void validEmailShouldHaveNoViolations() {
        LoginCredentials credentials = new LoginCredentials("valid@example.com", "pwd");

        Set<ConstraintViolation<LoginCredentials>> violations = validator.validate(credentials);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidEmailShouldHaveViolations() {
        LoginCredentials credentials = new LoginCredentials("not-an-email", "pwd");

        Set<ConstraintViolation<LoginCredentials>> violations = validator.validate(credentials);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString()))
        );
    }
}
