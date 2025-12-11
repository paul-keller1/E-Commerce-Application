package com.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.model.Cart;
import com.app.model.Category;
import com.app.model.Product;
import com.app.exception.APIException;
import com.app.dto.CartDTO;
import com.app.dto.ProductDTO;
import com.app.dto.ProductResponse;
import com.app.repository.CartRepo;
import com.app.repository.CategoryRepo;
import com.app.repository.ProductRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

	private final List<Product> inMemoryProducts = new ArrayList<>();

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private CartRepo cartRepo;

	@Autowired
	private CartService cartService;




	@Autowired
	private FileService fileService;

	@Autowired
	private ModelMapper modelMapper;

	@Value("${project.image}")
	private String path;

	@Override
	public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new APIException("Category not found with categoryId: " + categoryId));

		List<Product> products = productRepo.findAll();
		if (products == null || products.isEmpty()) {
			products = new ArrayList<>(inMemoryProducts);
		}

		boolean isDuplicateDescription = products.stream()
				.anyMatch(p -> p.getDescription() != null && p.getDescription().equals(productDTO.getDescription()));

		if (isDuplicateDescription) {
			throw new APIException("Product already exists !!!");
		}

		Product product = new Product();
		product.setName(productDTO.getName());
		product.setImage("default.png");
		product.setDescription(productDTO.getDescription());
		product.setQuantity(productDTO.getQuantity());
		product.setPrice(productDTO.getPrice());
		product.setDiscount(productDTO.getDiscount());


		double specialPrice = productDTO.getPrice() - ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());
		product.setSpecialPrice(specialPrice);
		product.setCategory(category);

		Product savedProduct = productRepo.save(product);
		inMemoryProducts.add(savedProduct);

		return modelMapper.map(savedProduct, ProductDTO.class);
	}

	@Override
	public ProductResponse getAllProductsResponse(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findAll(pageDetails);

		List<Product> products = pageProducts.getContent();

		List<ProductDTO> productDTOs = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
				.collect(Collectors.toList());

		ProductResponse productResponse = new ProductResponse();

		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageProducts.getNumber());
		productResponse.setPageSize(pageProducts.getSize());
		productResponse.setTotalElements(pageProducts.getTotalElements());
		productResponse.setTotalPages(pageProducts.getTotalPages());
		productResponse.setLastPage(pageProducts.isLast());

		return productResponse;
	}

	@Override
	public List<Product> getAllProductsFull() {

		return productRepo.findAll();
	}




	@Override
	public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder) {

		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new APIException("Category not found with categoryId: " + categoryId));

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findAll(pageDetails);

		List<Product> products = pageProducts.getContent();

		if (products.size() == 0) {
			throw new APIException(category.getCategoryName() + " category doesn't contain any products !!!");
		}

		List<ProductDTO> productDTOs = products.stream().map(p -> modelMapper.map(p, ProductDTO.class))
				.collect(Collectors.toList());

		ProductResponse productResponse = new ProductResponse();

		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageProducts.getNumber());
		productResponse.setPageSize(pageProducts.getSize());
		productResponse.setTotalElements(pageProducts.getTotalElements());
		productResponse.setTotalPages(pageProducts.getTotalPages());
		productResponse.setLastPage(pageProducts.isLast());

		return productResponse;
	}

	@Override
	public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findByNameLike(keyword, pageDetails);

		List<Product> products = pageProducts.getContent();
		
		if (products.size() == 0) {
			throw new APIException("Products not found with keyword: " + keyword);
		}

		List<ProductDTO> productDTOs = products.stream().map(p -> modelMapper.map(p, ProductDTO.class))
				.collect(Collectors.toList());

		ProductResponse productResponse = new ProductResponse();

		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageProducts.getNumber());
		productResponse.setPageSize(pageProducts.getSize());
		productResponse.setTotalElements(pageProducts.getTotalElements());
		productResponse.setTotalPages(pageProducts.getTotalPages());
		productResponse.setLastPage(pageProducts.isLast());

		return productResponse;
	}

	@Override
	public ProductDTO updateProduct(Long productId, Product product) {
		Product productFromDB = productRepo.findById(productId)
				.orElseThrow(() -> new APIException("Product not found with productId: " + productId));


		product.setImage(productFromDB.getImage());
		product.setProductId(productId);
		product.setCategory(productFromDB.getCategory());

		double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
		product.setSpecialPrice(specialPrice);

		Product savedProduct = productRepo.save(product);

		List<Cart> carts = cartRepo.findCartsByProductId(productId);

		List<CartDTO> cartDTOs = carts.stream().map(cart -> {
			CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

			List<ProductDTO> products = cart.getCartItems().stream()
					.map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

			cartDTO.setProducts(products);

			return cartDTO;

		}).collect(Collectors.toList());

		cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

		return modelMapper.map(savedProduct, ProductDTO.class);
	}

	@Override
	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
		Product productFromDB = productRepo.findById(productId)
				.orElseThrow(() -> new APIException("Product not found with productId: " + productId));


		String fileName = fileService.uploadImage(path, image);
		
		productFromDB.setImage(fileName);
		
		Product updatedProduct = productRepo.save(productFromDB);
		
		return modelMapper.map(updatedProduct, ProductDTO.class);
	}
	
	@Override
	public String deleteProduct(Long productId) {

		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new APIException("Product not found with productId: " + productId));

		List<Cart> carts = cartRepo.findCartsByProductId(productId);

		carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

		productRepo.delete(product);

		return "Product with productId: " + productId + " deleted successfully !!!";
	}

}
