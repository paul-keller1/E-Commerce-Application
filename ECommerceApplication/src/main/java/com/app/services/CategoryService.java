package com.app.services;

import com.app.model.Category;
import com.app.payloads.CategoryDTO;
import com.app.payloads.CategoryResponse;

public interface CategoryService {

	CategoryDTO createCategory(CategoryDTO categoryDTO);

	CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO updateCategory(Category category, Long categoryId);

	String deleteCategory(Long categoryId);
}
