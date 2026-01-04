package com.app.security;

import com.app.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JWTFilterTest {

    private final JWTService jwtService = new JWTService();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateWhenBearerTokenValid() throws Exception {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);
        UserDetails userDetails = new User(email, "pwd", List.of());

        JWTFilter filter = new JWTFilter();
        setField(filter, "jwtService", jwtService);
        setField(filter, "context", createContext(new StubUserDetailsService(userDetails)));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        TrackingFilterChain chain = new TrackingFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertTrue(chain.invoked);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }

    @Test
    void shouldSkipAuthenticationWhenNoHeaderOrInvalidUser() throws Exception {
        JWTFilter filter = new JWTFilter();
        setField(filter, "jwtService", jwtService);
        UserDetails userDetails = new User("other@example.com", "pwd", List.of());
        setField(filter, "context", createContext(new StubUserDetailsService(userDetails)));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtService.generateToken("original@example.com"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        TrackingFilterChain chain = new TrackingFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertTrue(chain.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // no header case
        SecurityContextHolder.clearContext();
        MockHttpServletRequest requestNoHeader = new MockHttpServletRequest();
        TrackingFilterChain chain2 = new TrackingFilterChain();
        filter.doFilterInternal(requestNoHeader, new MockHttpServletResponse(), chain2);
        assertTrue(chain2.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // header present but not Bearer
        SecurityContextHolder.clearContext();
        MockHttpServletRequest wrongHeader = new MockHttpServletRequest();
        wrongHeader.addHeader("Authorization", "Token abc");
        TrackingFilterChain chain3 = new TrackingFilterChain();
        filter.doFilterInternal(wrongHeader, new MockHttpServletResponse(), chain3);
        assertTrue(chain3.invoked);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // authentication already present should skip JWT validation
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
        TrackingFilterChain chain4 = new TrackingFilterChain();
        filter.doFilterInternal(request, new MockHttpServletResponse(), chain4);
        assertTrue(chain4.invoked);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    private ApplicationContext createContext(UserDetailsServiceImpl uds) {
        StaticApplicationContext ctx = new StaticApplicationContext();
        ctx.getBeanFactory().registerSingleton("userDetailsServiceImpl", uds);
        return ctx;
    }

    private static class TrackingFilterChain implements FilterChain {
        boolean invoked = false;

        @Override
        public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) throws IOException, ServletException {
            invoked = true;
        }
    }

    private static class StubUserDetailsService extends UserDetailsServiceImpl {
        private final UserDetails user;

        StubUserDetailsService(UserDetails user) {
            this.user = user;
        }

        @Override
        public UserDetails loadUserByUsername(String username) {
            return user;
        }
    }

}
