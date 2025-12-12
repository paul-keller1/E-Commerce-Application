package com.app.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import com.app.model.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class UserInfoConfigTest {

    @Test
    void constructorFromUser_ShouldMapEmailPasswordAndAuthorities() {
        com.app.model.User user = new com.app.model.User();
        user.setEmail("user@example.com");
        user.setPassword("secret");

        com.app.model.Role roleUser = com.app.model.Role.USER;
        com.app.model.Role roleAdmin = com.app.model.Role.ADMIN;

        user.setRoles(Set.of(roleUser, roleAdmin));

        UserInfoConfig userInfo = new UserInfoConfig(user);

        assertEquals("user@example.com", userInfo.getUsername());
        assertEquals("secret", userInfo.getPassword());

        Set<? extends GrantedAuthority> authorities =
                (Set<? extends GrantedAuthority>) userInfo.getAuthorities();

        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    void allArgsConstructor_ShouldSetFieldsCorrectly() {
        Set<GrantedAuthority> authorities =
                Set.of(new SimpleGrantedAuthority("ROLE_USER"));

        UserInfoConfig userInfo =
                new UserInfoConfig(1L, "user@example.com", "password", authorities);

        assertEquals("user@example.com", userInfo.getUsername());
        assertEquals("password", userInfo.getPassword());
        assertEquals(authorities, userInfo.getAuthorities());
    }

    @Test
    void noArgsConstructor_AndSetters_ShouldWork() {
        UserInfoConfig userInfo = new UserInfoConfig();

        Set<GrantedAuthority> authorities =
                Set.of(new SimpleGrantedAuthority("ROLE_USER"));

        userInfo.setEmail("user@example.com");
        userInfo.setPassword("password");
        userInfo.setAuthorities(authorities);

        assertEquals("user@example.com", userInfo.getUsername());
        assertEquals("password", userInfo.getPassword());
        assertEquals(authorities, userInfo.getAuthorities());
    }

    @Test
    void accountStatusFlags_ShouldAllBeTrue() {
        UserInfoConfig userInfo = new UserInfoConfig();

        assertTrue(userInfo.isAccountNonExpired());
        assertTrue(userInfo.isAccountNonLocked());
        assertTrue(userInfo.isCredentialsNonExpired());
        assertTrue(userInfo.isEnabled());
    }

    @Test
    void equalsHashCodeToStringAndGetEmail_ShouldBeCovered() {
        Set<GrantedAuthority> authorities =
                Set.of(new SimpleGrantedAuthority("ROLE_USER"));

        UserInfoConfig u1 = new UserInfoConfig(1L, "user@example.com", "password", authorities);

        int hash1 = u1.hashCode();
        int hash2 = u1.hashCode();
        assertEquals(hash1, hash2);

        assertFalse(u1.equals(null));
        assertFalse(u1.equals("not a cart"));

        Cart cart = new Cart();
        cart.setCartId(123L);
        assertFalse(u1.equals(cart));

        String s = u1.toString();
        assertEquals("id=user@example.com", s);

        assertEquals("user@example.com", u1.getEmail());
    }
}
