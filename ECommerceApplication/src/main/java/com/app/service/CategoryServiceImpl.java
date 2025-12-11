package com.app.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.model.Category;
import com.app.model.Product;
import com.app.exception.APIException;
import com.app.dto.CategoryDTO;
import com.app.dto.CategoryResponse;
import com.app.repository.CategoryRepo;

import jakarta.transaction.Transactional;

import java.lang.reflect.Field;

@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private ProductService productService;

	@Autowired
	private ModelMapper modelMapper;


	private static final Set<String> CATEGORY_SORTABLE_FIELDS =
			Arrays.stream(Category.class.getDeclaredFields())
					// ignore collection / relationship fields like List<Product>
					.filter(f -> !java.util.Collection.class.isAssignableFrom(f.getType())) //checks only if class is an  interface, maybe a bug
					.map(Field::getName)
					.collect(Collectors.toSet());



	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		if(categoryRepo.findByCategoryName(categoryDTO.getCategoryName()).isPresent()) {
			throw new APIException("Category with the name '" + categoryDTO.getCategoryName() + "' already exists !!!");
		}

		categoryRepo.save(modelMapper.map(categoryDTO, Category.class));

		return categoryDTO;
	}

	@Override
	public CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder;


		if (!CATEGORY_SORTABLE_FIELDS.contains(sortBy)) {
			throw new APIException(
					"Invalid sort field: " + sortBy + ". Allowed values: " + CATEGORY_SORTABLE_FIELDS
			);
		}


		if (sortOrder.equalsIgnoreCase("asc")) {
			sortByAndOrder = Sort.by(sortBy).ascending();
		} else if (sortOrder.equalsIgnoreCase("desc")) {
			sortByAndOrder = Sort.by(sortBy).descending();
		} else {
			throw new APIException("Invalid sort order: " + sortOrder + ". Allowed values: asc, desc");
		}



		if (categoryRepo.count() == 0) {
			throw new APIException("No category is created till now");
		}

		long total = categoryRepo.count();
		int maxPage = (int) Math.ceil((double) total / pageSize);

		if (pageNumber >= maxPage) {
			throw new APIException("Page number out of range");
		}

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		
		Page<Category> pageCategories = categoryRepo.findAll(pageDetails);

		List<Category> categories = pageCategories.getContent();



		List<CategoryDTO> categoryDTOs = categories.stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class)).collect(Collectors.toList());

		CategoryResponse categoryResponse = new CategoryResponse();
		
		categoryResponse.setContent(categoryDTOs);
		categoryResponse.setPageNumber(pageCategories.getNumber());
		categoryResponse.setPageSize(pageCategories.getSize());
		categoryResponse.setTotalElements(pageCategories.getTotalElements());
		categoryResponse.setTotalPages(pageCategories.getTotalPages());
		categoryResponse.setLastPage(pageCategories.isLast());
		
		return categoryResponse;
	}

	@Override
	public CategoryDTO updateCategory(Category category, Long categoryId) {
		categoryRepo.findById(categoryId)
				.orElseThrow(() -> new APIException("Category with the id '" + categoryId + "' doesn't exists !!!"));

		Optional<Category> existing = categoryRepo.findByCategoryName(category.getCategoryName());
		if (existing.isPresent() && !existing.get().getCategoryId().equals(categoryId)) {
			throw new APIException("Category name '" + category.getCategoryName() + "' already exists!");
		}
		category.setCategoryId(categoryId);
		Category updatedCategory = categoryRepo.save(category);

		return modelMapper.map(updatedCategory, CategoryDTO.class);
	}

	@Override
	public String deleteCategory(Long categoryId) {
		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new APIException("Category with the id '" + categoryId + "' doesn't exists !!!"));
		
		List<Product> products = category.getProducts();

		products.forEach(product -> {
			productService.deleteProduct(product.getProductId());
		});
		
		categoryRepo.delete(category);

		return "Category with categoryId: " + categoryId + " deleted successfully !!!";
	}

}
