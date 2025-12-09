package com.app.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.model.Cart;
import com.app.model.CartItem;
import com.app.model.Product;
import com.app.exceptions.APIException;
import com.app.payloads.CartDTO;
import com.app.payloads.ProductDTO;
import com.app.repositories.CartItemRepo;
import com.app.repositories.CartRepo;
import com.app.repositories.ProductRepo;

import java.util.Optional;


import jakarta.transaction.Transactional;

@Transactional
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepo cartRepo;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private CartItemRepo cartItemRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CartDTO addProductToCart(Long cartId, Long productId, Integer quantity) {

		Cart cart = cartRepo.findById(cartId)
				.orElseThrow(() -> new APIException("Cart with cartId" +  cartId + "was not found!!!"));

		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new APIException("Product with productId" + productId + "was not found!!!"));

		Optional<CartItem>  cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);

		if (cartItem.isPresent()) {
			throw new APIException("Product " + product.getName() + " already exists in the cart!!!");
		}

		if (product.getQuantity() == 0) {
			throw new APIException(product.getName() + " is not available!!!");
		}

		if (product.getQuantity() < quantity) {
			throw new APIException("Please, make an order of the " + product.getName()
					+ " less than or equal to the quantity " + product.getQuantity() + "!!!");
		}

		CartItem newCartItem = new CartItem();

		newCartItem.setProduct(product);
		newCartItem.setCart(cart);
		newCartItem.setQuantity(quantity);
		newCartItem.setDiscount(product.getDiscount());
		newCartItem.setProductPrice(product.getSpecialPrice());

		cartItemRepo.save(newCartItem);

		product.setQuantity(product.getQuantity() - quantity);

		cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

		CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

		List<ProductDTO> productDTOs = cart.getCartItems().stream()
				.map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

		cartDTO.setProducts(productDTOs);

		return cartDTO;

	}

	@Override
	public List<CartDTO> getAllCarts() {
		List<Cart> carts = cartRepo.findAll();

		if (carts.size() == 0) {
			throw new APIException("No cart exists!!!");
		}

        return carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());
	}

	@Override
	public CartDTO getCart(String emailId, Long cartId) {
		Cart cart = cartRepo.findCartByEmailAndCartId(emailId, cartId);

		if (cart == null) {
			throw new APIException("Cart with cartId" + cartId + "was not found!!!");
		}

		CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
		
		List<ProductDTO> products = cart.getCartItems().stream()
				.map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

		cartDTO.setProducts(products);

		return cartDTO;
	}

	@Override
	public void updateProductInCarts(Long cartId, Long productId) {
		Cart cart = cartRepo.findById(cartId)
				.orElseThrow(() -> new APIException("Cart with cartId" + cartId + "was not found!!!"));

		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new APIException("Product with productId" + productId + "was not found!!!"));

		CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId)
				.orElseThrow(() -> new APIException("Product " + product.getName() + " not available in the cart!!!"));


		double cartPriceWithoutPreviousQuantity = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

		cartItem.setProductPrice(product.getSpecialPrice());

		cart.setTotalPrice(cartPriceWithoutPreviousQuantity + (cartItem.getProductPrice() * cartItem.getQuantity()));

		cartItemRepo.save(cartItem);
	}

	@Override
	public CartDTO updateProductQuantityInCart(Long cartId, Long productId, Integer quantity) {
		Cart cart = cartRepo.findById(cartId)
				.orElseThrow(() -> new APIException("Cart with cartId" + cartId + "was not found!!!"));

		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new APIException("Product with productId" + productId + "was not found!!!"));

		if (product.getQuantity() == 0) {
			throw new APIException(product.getName() + " is not available!!!");
		}

		if (product.getQuantity() < quantity) {
			throw new APIException("Please, make an order of the " + product.getName()
					+ " less than or equal to the quantity " + product.getQuantity() + "!!!");
		}

		CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId)
				.orElseThrow(() -> new APIException("Product " + product.getName() + " not available in the cart!!!"));


		double cartPriceWithoutPreviousQuantity = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

		product.setQuantity(product.getQuantity() + cartItem.getQuantity() - quantity);

		cartItem.setProductPrice(product.getSpecialPrice());
		cartItem.setQuantity(quantity);
		cartItem.setDiscount(product.getDiscount());

		cart.setTotalPrice(cartPriceWithoutPreviousQuantity + (cartItem.getProductPrice() * quantity));


		cartItemRepo.save(cartItem);

		CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

		List<ProductDTO> productDTOs = cart.getCartItems().stream()
				.map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

		cartDTO.setProducts(productDTOs);

		return cartDTO;

	}

	@Override
	public String deleteProductFromCart(Long cartId, Long productId) {
		Cart cart = cartRepo.findById(cartId)
				.orElseThrow(() -> new APIException("Cart with cartId" + cartId + "was not found!!!"));

		CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId)
				.orElseThrow(() -> new APIException("Cart item with cartId" + cartId  + "and productId" + productId + "was not found!!!"));



		cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

		Product product = cartItem.getProduct();
		product.setQuantity(product.getQuantity() + cartItem.getQuantity());

		cartItemRepo.deleteCartItemByProductIdAndCartId(cartId, productId);

		return "Product " + cartItem.getProduct().getName() + " removed from the cart !!!";
	}

}
