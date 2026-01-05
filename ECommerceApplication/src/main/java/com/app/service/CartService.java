package com.app.service;

import java.util.List;

import com.app.dto.CartDTO;
import com.app.exception.APIException;

public interface CartService {

	/*@
		public normal_behavior
		  requires productId != null;
		  requires quantity != null;
		  ensures \result != null;

		also


		public exceptional_behavior
			requires productId != null;
		    requires quantity != null;
		    signals (APIException e) true;
		    signals (SecurityException e) true;
	@*/
	CartDTO addProductToCart(Long productId, Integer quantity) throws APIException, SecurityException;



	/*@
    public normal_behavior
      ensures \result != null;

	also


	public exceptional_behavior
		signals (APIException e) true;

@*/
	List<CartDTO> getAllCarts() throws APIException;


	/*@
    public normal_behavior
      requires emailId != null;
      requires cartId != null;
      ensures \result != null;


    also


	public exceptional_behavior
		requires emailId != null;
		requires cartId != null;
		signals (APIException e) true;

@*/
	CartDTO getCart(String emailId, Long cartId) throws APIException;

	/*@
    public normal_behavior
      requires cartId != null;
      requires productId != null;
      requires quantity != null;
      ensures \result != null;


    also


	public exceptional_behavior
		requires cartId != null;
	    requires productId != null;
	    requires quantity != null;
		signals (APIException e) true;
@*/
	CartDTO updateProductQuantityInCart(Long cartId, Long productId, Integer quantity) throws APIException;

	/*@
    public normal_behavior
      requires cartId != null;
      requires productId != null;

    also


	public exceptional_behavior
		requires cartId != null;
	    requires productId != null;
		signals (APIException e) true;


@*/
	void updateProductInCarts(Long cartId, Long productId) throws APIException;

	/*@
    public normal_behavior
      requires cartId != null;
      requires productId != null;
      ensures \result != null;


    also


	public exceptional_behavior
		requires cartId != null;
	    requires productId != null;
		signals (APIException e) true;

@*/
	String deleteProductFromCart(Long cartId, Long productId) throws APIException;
	
}
