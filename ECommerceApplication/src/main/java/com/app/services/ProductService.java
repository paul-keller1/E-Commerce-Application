package com.app.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.app.model.Product;
import com.app.payloads.ProductDTO;
import com.app.payloads.ProductResponse;

public interface ProductService {

	ProductDTO addProduct(Long categoryId, ProductDTO productDTO);


	List<Product> getAllProductsFull();

	ProductResponse getAllProductsResponse(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder);

	ProductDTO updateProduct(Long productId, Product product);

	ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

	ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder);

	String deleteProduct(Long productId);

}
