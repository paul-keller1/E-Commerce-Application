package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.dto.CartDTO;
import com.app.service.CartService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CartController {

	@Autowired
	private CartService cartService;

	/*@
	  private invariant cartService != null;
	@*/

	/*@
	  public normal_behavior
	  requires productId != null;
	  requires productId.longValue() > 0;
	  requires quantity != null;
	  requires quantity.intValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 201;
	  ensures \result.getBody() != null;
	@*/
	@PostMapping("/user/carts/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
		CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
	}

	/*@
	  public normal_behavior
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 302;
	  ensures \result.getBody() != null;
	@*/
	@GetMapping("/admin/carts")
	public ResponseEntity<List<CartDTO>> getCarts() {
		List<CartDTO> cartDTOs = cartService.getAllCarts();
		return new ResponseEntity<List<CartDTO>>(cartDTOs, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires emailId != null;
	  requires cartId != null;
	  requires cartId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 302;
	  ensures \result.getBody() != null;
	@*/
	@GetMapping("/admin/users/{emailId}/carts/{cartId}")
	public ResponseEntity<CartDTO> getCartById(@PathVariable String emailId, @PathVariable Long cartId) {
		CartDTO cartDTO = cartService.getCart(emailId, cartId);
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires cartId != null;
	  requires cartId.longValue() > 0;
	  requires productId != null;
	  requires productId.longValue() > 0;
	  requires quantity != null;
	  requires quantity.intValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@PutMapping("/user/carts/{cartId}/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long cartId,
													 @PathVariable Long productId,
													 @PathVariable Integer quantity) {
		CartDTO cartDTO = cartService.updateProductQuantityInCart(cartId, productId, quantity);
		return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
	}

	/*@
	  public normal_behavior
	  requires cartId != null;
	  requires cartId.longValue() > 0;
	  requires productId != null;
	  requires productId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@DeleteMapping("/user/carts/{cartId}/product/{productId}")
	public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
		String status = cartService.deleteProductFromCart(cartId, productId);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
