package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.config.AppConstants;
import com.app.model.Category;
import com.app.dto.CategoryDTO;
import com.app.dto.CategoryResponse;
import com.app.service.CategoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	/*@
	  private invariant categoryService != null;
	@*/

	/*@
	  public normal_behavior
	  requires categoryDTO != null;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 201;
	  ensures \result.getBody() != null;
	@*/
	@PostMapping("/admin/category")
	public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
		CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
		return new ResponseEntity<CategoryDTO>(savedCategoryDTO, HttpStatus.CREATED);
	}

	/*@
	  public normal_behavior
	  requires pageNumber != null;
	  requires pageSize != null;
	  requires sortBy != null;
	  requires sortOrder != null;
	  requires pageNumber.intValue() >= 0;
	  requires pageSize.intValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 302;
	  ensures \result.getBody() != null;
	@*/
	@GetMapping("/user/categories")
	public ResponseEntity<CategoryResponse> getCategories(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		CategoryResponse categoryResponse = categoryService.getCategories(pageNumber, pageSize, sortBy, sortOrder);
		return new ResponseEntity<CategoryResponse>(categoryResponse, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires category != null;
	  requires categoryId != null;
	  requires categoryId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@PutMapping("/admin/categories/{categoryId}")
	public ResponseEntity<CategoryDTO> updateCategory(@RequestBody Category category,
													  @PathVariable Long categoryId) {
		CategoryDTO categoryDTO = categoryService.updateCategory(category, categoryId);
		return new ResponseEntity<CategoryDTO>(categoryDTO, HttpStatus.OK);
	}

	/*@
	  public normal_behavior
	  requires categoryId != null;
	  requires categoryId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@DeleteMapping("/admin/categories/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
		String status = categoryService.deleteCategory(categoryId);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
