package com.app.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDTOTest {

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        CategoryDTO dto = new CategoryDTO();

        dto.setCategoryId(1L);
        dto.setCategoryName("Electronics");

        assertEquals(1L, dto.getCategoryId());
        assertEquals("Electronics", dto.getCategoryName());
    }

    @Test
    void allArgsConstructorShouldSetAllFields() {
        CategoryDTO dto = new CategoryDTO(1L, "Clothing");

        assertEquals(1L, dto.getCategoryId());
        assertEquals("Clothing", dto.getCategoryName());
    }

    @Test
    void equalsAndHashCodeShouldWorkForIdenticalObjects() {
        CategoryDTO c1 = new CategoryDTO(1L, "Home");
        CategoryDTO c2 = new CategoryDTO(1L, "Home");
        CategoryDTO c3 = new CategoryDTO(2L, "Books");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
    }

    @Test
    void equalsShouldHandleNullAndDifferentType() {
        CategoryDTO dto = new CategoryDTO(1L, "Sports");

        assertEquals(dto, dto);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "string");
    }

    @Test
    void toStringShouldContainClassNameAndFields() {
        CategoryDTO dto = new CategoryDTO(1L, "Sports");

        String s = dto.toString();

        assertNotNull(s);
        assertTrue(s.contains("CategoryDTO"));
        assertTrue(s.contains("Sports"));
    }
}
