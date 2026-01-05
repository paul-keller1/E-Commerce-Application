package com.app.service;

import com.app.exception.APIException;
import com.app.model.Category;
import com.app.dto.CategoryDTO;
import com.app.dto.CategoryResponse;

public interface CategoryService {


	/*@
		public normal_behavior
		  requires categoryDTO != null;
		  requires categoryDTO.getCategoryName() != null;
		  ensures \result != null;


		also


		public exceptional_behavior
		  requires categoryDTO != null;
		  requires categoryDTO.getCategoryName() != null;
		  signals (APIException e) true;



	@*/

	CategoryDTO createCategory(CategoryDTO categoryDTO) throws APIException;


	/*@
		public normal_behavior
		  requires pageNumber != null;
		  requires pageSize != null;
		  requires sortBy != null;
		  requires sortOrder != null;
		  requires pageSize.intValue() > 0;
		  requires pageNumber.intValue() >= 0;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires pageNumber != null;
		  requires pageSize != null;
		  requires sortBy != null;
		  requires sortOrder != null;
		  requires pageSize.intValue() > 0;
		  requires pageNumber.intValue() >= 0;
		  signals (APIException e) true;



	@*/
	CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) throws APIException;

	/*@
    	public normal_behavior
		  requires categoryId != null;
		  requires category != null;
		  requires category.getCategoryName() != null;
		  ensures \result != null;


		also


		public exceptional_behavior
		  requires categoryId != null;
		  requires category != null;
		  requires category.getCategoryName() != null;
		  signals (APIException e) true;

	@*/
	CategoryDTO updateCategory(Category category, Long categoryId) throws APIException;

	/*@
		public normal_behavior
		  requires categoryId != null;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires categoryId != null;
		  signals (APIException e) true;

	@*/
	String deleteCategory(Long categoryId) throws APIException;
}
