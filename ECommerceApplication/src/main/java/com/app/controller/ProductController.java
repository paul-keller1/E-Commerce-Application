package com.app.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.app.config.AppConstants;
import com.app.model.Product;
import com.app.dto.ProductDTO;
import com.app.dto.ProductResponse;
import com.app.service.ProductService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class ProductController {

	@Autowired
	private ProductService productService;

	/*@
	  private invariant productService != null;
	@*/

	/*@
	  public normal_behavior
	  requires productDTO != null;
	  requires categoryId != null;
	  requires categoryId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 201;
	  ensures \result.getBody() != null;
	@*/
	@PostMapping("/admin/product/{categoryId}/")
	public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
												 @PathVariable Long categoryId) {

		ProductDTO savedProduct = productService.addProduct(categoryId, productDTO);
		return new ResponseEntity<ProductDTO>(savedProduct, HttpStatus.CREATED);
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
	@GetMapping("/user/products")
	public ResponseEntity<ProductResponse> getAllProducts(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		ProductResponse productResponse =
				productService.getAllProductsResponse(pageNumber, pageSize, sortBy, sortOrder);

		return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires categoryId != null;
	  requires categoryId.longValue() > 0;
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
	@GetMapping("/user/product/{categoryId}/")
	public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
																 @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
																 @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
																 @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
																 @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		ProductResponse productResponse =
				productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);

		return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires keyword != null;
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
	@GetMapping("/user/products/keyword/{keyword}")
	public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
																@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
																@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
																@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
																@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		ProductResponse productResponse =
				productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);

		return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires product != null;
	  requires productId != null;
	  requires productId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@PutMapping("/admin/product/{productId}")
	public ResponseEntity<ProductDTO> updateProduct(@RequestBody Product product,
													@PathVariable Long productId) {
		ProductDTO updatedProduct = productService.updateProduct(productId, product);
		return new ResponseEntity<ProductDTO>(updatedProduct, HttpStatus.OK);
	}

	/*@
	  public normal_behavior
	  requires productId != null;
	  requires productId.longValue() > 0;
	  requires image != null;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@PutMapping("/admin/product/{productId}/image")
	public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
														 @RequestParam("image") MultipartFile image) throws IOException {
		ProductDTO updatedProduct = productService.updateProductImage(productId, image);
		return new ResponseEntity<ProductDTO>(updatedProduct, HttpStatus.OK);
	}

	/*@
	  public normal_behavior
	  requires productId != null;
	  requires productId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@DeleteMapping("/admin/product/{productId}")
	public ResponseEntity<String> deleteProductByCategory(@PathVariable Long productId) {
		String status = productService.deleteProduct(productId);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
