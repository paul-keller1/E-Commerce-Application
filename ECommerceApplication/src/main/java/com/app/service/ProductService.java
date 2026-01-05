package com.app.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.app.model.Product;
import com.app.dto.ProductDTO;
import com.app.dto.ProductResponse;

public interface ProductService {


	/*@
		public normal_behavior
		  requires categoryId != null;
		  requires productDTO != null;
		  requires productDTO.getName() != null;
		  requires productDTO.getDescription() != null;
		  requires productDTO.getQuantity() != null;
		  requires productDTO.getPrice() != 0;
		  requires productDTO.getDiscount() != 0;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires categoryId != null;
		  requires productDTO != null;
		  requires productDTO.getName() != null;
		  requires productDTO.getDescription() != null;
		  requires productDTO.getQuantity() != null;
		  requires productDTO.getPrice() != 0;
		  requires productDTO.getDiscount() != 0;
		  signals (APIException e) true;
	@*/
	ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

	/*@
		public normal_behavior
		  ensures \result != null;
	@*/
	List<Product> getAllProductsFull();

	/*@
		public normal_behavior
		  requires pageNumber != null;
		  requires pageSize != null;
		  requires sortBy != null;
		  requires sortOrder != null;
		  requires pageSize.intValue() > 0;
		  requires pageNumber.intValue() >= 0;
		  ensures \result != null;
	@*/
	ProductResponse getAllProductsResponse(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);



	/*@
		public exceptional_behavior
		  requires categoryId != null;
		  requires pageNumber != null;
		  requires pageSize != null;
		  requires sortBy != null;
		  requires sortOrder != null;
		  requires pageSize.intValue() > 0;
		  requires pageNumber.intValue() >= 0;
		  ensures \result != null;
		  signals (APIException e) true;



	@*/
	ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder);

	/*@
		public normal_behavior
		  requires keyword != null;
		  requires pageNumber != null;
		  requires pageSize != null;
		  requires sortBy != null;
		  requires sortOrder != null;
		  requires pageSize.intValue() > 0;
		  requires pageNumber.intValue() >= 0;
		  ensures \result != null;


		also


		public exceptional_behavior
		  requires keyword != null;
		  requires pageNumber != null;
		  requires pageSize != null;
		  requires sortBy != null;
		  requires sortOrder != null;
		  requires pageSize.intValue() > 0;
		  requires pageNumber.intValue() >= 0;
		  signals (APIException e) true;
	@*/
	ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder);

	/*@
		public normal_behavior
		  requires productId != null;
		  requires product != null;
		  ensures \result != null;



		also


		public exceptional_behavior
		  requires productId != null;
		  requires product != null;
		  signals (APIException e) true;
	@*/
	ProductDTO updateProduct(Long productId, Product product);


	/*@
		public normal_behavior
		  requires productId != null;
		  requires image != null;
		  ensures \result != null;



		also


		public exceptional_behavior
		  requires productId != null;
		  requires image != null;
		  signals (APIException e) true;
		  signals (IOException e) true;
	@*/
	ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;


	/*@
		public normal_behavior
		  requires productId != null;
		  ensures \result != null;


		also


		public exceptional_behavior
		  requires productId != null;
		  signals (APIException e) true;
		  signals (IOException e) true;

	@*/
	String deleteProduct(Long productId);

}
