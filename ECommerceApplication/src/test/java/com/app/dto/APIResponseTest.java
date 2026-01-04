package com.app.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class APIResponseTest {

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        APIResponse res = new APIResponse();

        res.setMessage("Success");
        res.setStatus(true);

        assertEquals("Success", res.getMessage());
        assertTrue(res.isStatus());
    }

    @Test
    void allArgsConstructorShouldSetAllFields() {
        APIResponse res = new APIResponse("Created", true);

        assertEquals("Created", res.getMessage());
        assertTrue(res.isStatus());
    }

    @Test
    void equalsAndHashCodeShouldWorkForIdenticalObjects() {
        APIResponse r1 = new APIResponse("OK", true);
        APIResponse r2 = new APIResponse("OK", true);
        APIResponse r3 = new APIResponse("Error", false);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void equalsShouldHandleNullAndDifferentType() {
        APIResponse res = new APIResponse("OK", true);

        assertEquals(res, res);
        assertNotEquals(res, null);
        assertNotEquals(res, "string");
    }

    @Test
    void toStringShouldContainClassNameAndFields() {
        APIResponse res = new APIResponse("Hi", true);

        String s = res.toString();

        assertNotNull(s);
        assertTrue(s.contains("APIResponse"));
        assertTrue(s.contains("Hi"));
        assertTrue(s.contains("true"));
    }
}
