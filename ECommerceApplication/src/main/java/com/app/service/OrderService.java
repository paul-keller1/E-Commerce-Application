package com.app.service;

import java.util.List;

import com.app.dto.OrderDTO;
import com.app.dto.OrderResponse;
import com.app.exception.APIException;
import com.app.exception.ResourceNotFoundException;

public interface OrderService {

	/*@
		public normal_behavior
		  requires emailId != null;
		  requires cartId != null;
		  requires paymentMethod != null;
		  ensures \result != null;


		also


		public exceptional_behavior
		  requires emailId != null;
		  requires cartId != null;
		  requires paymentMethod != null;
		  signals (ResourceNotFoundException e) true;
		  signals (APIException e) true;


	@*/
	OrderDTO placeOrder(String emailId, Long cartId, String paymentMethod) throws APIException;



	/*@
		public normal_behavior
		  requires emailId != null;
		  requires orderId != null;
		  ensures \result != null;



		also


		public exceptional_behavior
		  requires emailId != null;
		  requires orderId != null;
		  signals (APIException e) true;

	@*/
	OrderDTO getOrder(String emailId, Long orderId) throws APIException;

	/*@
		public normal_behavior
		  requires emailId != null;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires emailId != null;
		  signals (APIException e) true;
	@*/
	List<OrderDTO> getOrdersByUser(String emailId) throws APIException;

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
	OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) throws APIException;

	/*@
    public normal_behavior
      requires emailId != null;
      requires orderId != null;
      requires orderStatus != null;
      ensures \result != null;


		also


	public exceptional_behavior
      requires emailId != null;
      requires orderId != null;
      requires orderStatus != null;
      signals (ResourceNotFoundException e) true;


@*/
	OrderDTO updateOrder(String emailId, Long orderId, String orderStatus) throws ResourceNotFoundException;
}
