package com.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.app.dto.CategoryDTO;
import com.app.dto.CategoryResponse;
import com.app.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDTO = new CategoryDTO(1L, "Electronics");
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() {
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> response = categoryController.createCategory(categoryDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(categoryDTO, response.getBody());
    }

    @Test
    void getCategories_ShouldReturnResponse() {
        CategoryResponse responseDto = new CategoryResponse(Collections.singletonList(categoryDTO), 0, 10, 1L, 1, true);
        when(categoryService.getCategories(0, 10, "categoryId", "asc")).thenReturn(responseDto);

        ResponseEntity<CategoryResponse> response = categoryController.getCategories(0, 10, "categoryId", "asc");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        when(categoryService.updateCategory(any(), any(Long.class))).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> response = categoryController.updateCategory(new com.app.model.Category(), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDTO, response.getBody());
    }

    @Test
    void deleteCategory_ShouldReturnStatusMessage() {
        when(categoryService.deleteCategory(1L)).thenReturn("deleted");

        ResponseEntity<String> response = categoryController.deleteCategory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("deleted", response.getBody());
    }
}
