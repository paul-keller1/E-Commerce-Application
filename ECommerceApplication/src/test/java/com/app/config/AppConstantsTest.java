package com.app.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppConstantsTest {

    @Test
    void constantsShouldExposeExpectedValues() {
        // touch default constructor for coverage
        assertNotNull(new AppConstants());

        assertEquals("0", AppConstants.PAGE_NUMBER);
        assertEquals("2", AppConstants.PAGE_SIZE);
        assertEquals("categoryId", AppConstants.SORT_CATEGORIES_BY);
        assertEquals("productId", AppConstants.SORT_PRODUCTS_BY);
        assertEquals("userId", AppConstants.SORT_USERS_BY);
        assertEquals("totalAmount", AppConstants.SORT_ORDERS_BY);
        assertEquals("asc", AppConstants.SORT_DIR);
        assertEquals(101L, AppConstants.ADMIN_ID);
        assertEquals(102L, AppConstants.USER_ID);
        assertEquals(10 * 60 * 1000, AppConstants.JWT_TOKEN_VALIDITY);
    }

    @Test
    void urlArraysShouldMatchConfiguration() {
        assertArrayEquals(
                new String[]{ "/v3/api-docs/**", "/swagger-ui/**", "/api/register/**", "/api/login" },
                AppConstants.PUBLIC_URLS
        );
        assertArrayEquals(new String[]{"/api/user/**"}, AppConstants.USER_URLS);
        assertArrayEquals(new String[]{"/api/admin/**"}, AppConstants.ADMIN_URLS);
    }
}
