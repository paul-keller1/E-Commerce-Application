package com.app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.impl.DefaultClock;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.lang.reflect.Method;
import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.security.Security;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    @Test
    void generatedTokenShouldValidateAndExposeSubject() {
        JWTService service = new JWTService();
        String email = "user@example.com";
        String token = service.generateToken(email);

        UserDetails userDetails = new User(email, "pwd", List.of());
        assertEquals(email, service.extractUserName(token));
        assertTrue(service.validateToken(token, userDetails));

        UserDetails otherUser = new User("other@example.com", "pwd", List.of());
        assertFalse(service.validateToken(token, otherUser));
    }

    @Test
    void expiredTokenShouldNotValidate() throws Exception {
        JWTService service = new JWTService();

        Method keyMethod = JWTService.class.getDeclaredMethod("getKey");
        keyMethod.setAccessible(true);
        SecretKey key = (SecretKey) keyMethod.invoke(service);

        String expiredToken = Jwts.builder()
                .subject("expired@example.com")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()-1000))
                .signWith(key)
                .compact();

        UserDetails userDetails = new User("expired@example.com", "pwd", List.of());

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> service.validateToken(expiredToken, userDetails));
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> service.extractUserName(expiredToken));
    }

    @Test
    void constructorShouldWrapMissingAlgorithm() {
        var providers = Security.getProviders();
        try {
            for (var p : providers) {
                Security.removeProvider(p.getName());
            }
            RuntimeException ex = assertThrows(RuntimeException.class, JWTService::new);
            assertTrue(ex.getCause() instanceof java.security.NoSuchAlgorithmException);
        } finally {
            int position = 1;
            for (var p : providers) {
                Security.insertProviderAt(p, position++);
            }
        }
    }
    //wtf!?
    @Test
    void validateTokenShouldReturnFalseWhenTokenExpiredButParserClockIsSkewed() throws Exception {
        JWTService service = new JWTService();

        var clockField = DefaultClock.class.getDeclaredField("INSTANCE");
        clockField.setAccessible(true);

        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);

        Object base = unsafe.staticFieldBase(clockField);
        long offset = unsafe.staticFieldOffset(clockField);

        Clock originalClock = (Clock) unsafe.getObject(base, offset);
        Clock skewedClock = () -> new Date(0);

        try {
            unsafe.putObject(base, offset, skewedClock);

            Method keyMethod = JWTService.class.getDeclaredMethod("getKey");
            keyMethod.setAccessible(true);
            SecretKey key = (SecretKey) keyMethod.invoke(service);

            String expiredToken = Jwts.builder()
                    .subject("expired@example.com")
                    .issuedAt(new Date(System.currentTimeMillis() - 10_000))
                    .expiration(new Date(System.currentTimeMillis() - 5_000))
                    .signWith(key)
                    .compact();

            UserDetails userDetails = new User("expired@example.com", "pwd", List.of());
            assertFalse(service.validateToken(expiredToken, userDetails));
        } finally {
            unsafe.putObject(base, offset, originalClock);
        }
    }
}
