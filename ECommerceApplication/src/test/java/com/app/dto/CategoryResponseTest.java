package com.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class CategoryResponseTest {

    @Test
    void testNoArgsConstructor() {
        CategoryResponse response = new CategoryResponse();
        assertNotNull(response);
    }

    @Test
    void testAllArgsConstructor() {
        List<CategoryDTO> categories = Arrays.asList(
                new CategoryDTO(1L, "Electronics"),
                new CategoryDTO(2L, "Books")
        );

        CategoryResponse response = new CategoryResponse(
                categories,
                1,
                10,
                20L,
                2,
                false
        );

        assertEquals(categories, response.getContent());
        assertEquals(1, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(20L, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertFalse(response.isLastPage());
    }

    @Test
    void testSetterAndGetterMethods() {
        CategoryResponse response = new CategoryResponse();

        List<CategoryDTO> categories = Collections.singletonList(
                new CategoryDTO(3L, "Clothing")
        );

        response.setContent(categories);
        response.setPageNumber(2);
        response.setPageSize(5);
        response.setTotalElements(15L);
        response.setTotalPages(3);
        response.setLastPage(true);

        assertEquals(categories, response.getContent());
        assertEquals(2, response.getPageNumber());
        assertEquals(5, response.getPageSize());
        assertEquals(15L, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertTrue(response.isLastPage());
    }

    @Test
    void testEmptyContentList() {
        CategoryResponse response = new CategoryResponse(
                Collections.emptyList(),
                0,
                10,
                0L,
                0,
                true
        );

        assertTrue(response.getContent().isEmpty());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(0L, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertTrue(response.isLastPage());
    }

    @Test
    void testNullContentList() {
        CategoryResponse response = new CategoryResponse(
                null,
                1,
                10,
                0L,
                0,
                true
        );

        assertNull(response.getContent());
        assertEquals(1, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(0L, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertTrue(response.isLastPage());
    }

    @Test
    void testEqualsAndHashCode() {
        List<CategoryDTO> categories1 = Arrays.asList(
                new CategoryDTO(1L, "Electronics"),
                new CategoryDTO(2L, "Books")
        );

        List<CategoryDTO> categories2 = Arrays.asList(
                new CategoryDTO(1L, "Electronics"),
                new CategoryDTO(2L, "Books")
        );

        CategoryResponse response1 = new CategoryResponse(
                categories1, 1, 10, 20L, 2, false
        );

        CategoryResponse response2 = new CategoryResponse(
                categories2, 1, 10, 20L, 2, false
        );

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        CategoryResponse response = new CategoryResponse(
                Arrays.asList(new CategoryDTO(1L, "Electronics")),
                1,
                10,
                20L,
                2,
                false
        );

        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("content"));
        assertTrue(toString.contains("pageNumber"));
        assertTrue(toString.contains("pageSize"));
        assertTrue(toString.contains("totalElements"));
        assertTrue(toString.contains("totalPages"));
        assertTrue(toString.contains("lastPage"));
    }

    @Test
    void testLargeDataSet() {
        List<CategoryDTO> largeCategoryList = Arrays.asList(
                new CategoryDTO(1L, "Category1"),
                new CategoryDTO(2L, "Category2"),
                new CategoryDTO(3L, "Category3"),
                new CategoryDTO(4L, "Category4"),
                new CategoryDTO(5L, "Category5")
        );

        CategoryResponse response = new CategoryResponse(
                largeCategoryList,
                1,
                5,
                25L,
                5,
                false
        );

        assertEquals(5, response.getContent().size());
        assertEquals(1, response.getPageNumber());
        assertEquals(5, response.getPageSize());
        assertEquals(25L, response.getTotalElements());
        assertEquals(5, response.getTotalPages());
        assertFalse(response.isLastPage());
    }

    @Test
    void testEdgeCases() {
        CategoryResponse response1 = new CategoryResponse(
                null,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                Long.MAX_VALUE,
                Integer.MAX_VALUE,
                true
        );

        assertEquals(Integer.MAX_VALUE, response1.getPageNumber());
        assertEquals(Integer.MAX_VALUE, response1.getPageSize());
        assertEquals(Long.MAX_VALUE, response1.getTotalElements());
        assertEquals(Integer.MAX_VALUE, response1.getTotalPages());
        assertTrue(response1.isLastPage());
        assertNull(response1.getContent());
    }

    @Test
    void testContentManipulation() {
        CategoryResponse response = new CategoryResponse();

        List<CategoryDTO> categories = Arrays.asList(
                new CategoryDTO(1L, "Electronics"),
                new CategoryDTO(2L, "Books")
        );

        response.setContent(categories);

        assertEquals(2, response.getContent().size());
        assertEquals("Electronics", response.getContent().get(0).getCategoryName());
        assertEquals(1L, response.getContent().get(0).getCategoryId());
    }

    @Test
    void testComparisonWithDifferentValues() {
        CategoryResponse response1 = new CategoryResponse(
                Arrays.asList(new CategoryDTO(1L, "Electronics")),
                1,
                10,
                20L,
                2,
                false
        );

        CategoryResponse response2 = new CategoryResponse(
                Arrays.asList(new CategoryDTO(2L, "Books")),
                2,
                5,
                15L,
                3,
                true
        );

        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }
}
